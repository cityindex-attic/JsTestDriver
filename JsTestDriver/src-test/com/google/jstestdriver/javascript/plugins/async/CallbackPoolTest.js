/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

var callbackPoolTest = TestCase('callbackPoolTest');


/**
 * Regression test for Issue 140: "If all callbacks in a CallbackPool complete
 *    before the test case function returns, the test runner goes haywire."
 * @bug 140
 */
callbackPoolTest.prototype.testCallbackCalledBeforePoolIsActive = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    complete = true;
  });

  var callbackA = pool.add(function() {});
  assertFalse(pool.active_);

  callbackA();

  assertFalse(pool.active_);
  assertFalse(complete);
};


/**
 * Tests #activate(), added to fix Issue 140.
 * @bug 140
 */
callbackPoolTest.prototype.testActivate = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    complete = true;
  });

  var callbackA = pool.add(function() {});
  assertFalse(pool.active_);

  pool.activate();
  callbackA();

  assertTrue(pool.active_);
  assertTrue(complete);
};


callbackPoolTest.prototype.testAdd = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var callbackA = pool.add(function() {});
  assertEquals(1, pool.count());
  assertFalse(complete);

  var callbackB = pool.add(function() {});
  assertEquals(2, pool.count());
  assertFalse(complete);

  pool.activate();

  callbackA();
  assertEquals(1, pool.count());
  assertFalse(complete);

  callbackB();
  assertEquals(0, pool.count());
  assertTrue(complete);
};


callbackPoolTest.prototype.testScopeIsNotWindow = function() {
  var complete = false;
  var testCase = {};
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, testCase, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var callbackAScope;
  var callbackA = pool.add(function() {callbackAScope = this;});
  assertEquals(1, pool.count());
  assertFalse(complete);

  pool.activate();

  callbackA();
  assertEquals(0, pool.count());
  assertTrue(complete);
  assertFalse('window === callbackAScope', window === callbackAScope);
  assertTrue('testCase === callbackAScope', testCase === callbackAScope);
};


callbackPoolTest.prototype.testAddWithArguments = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var capturedOne;
  var capturedTwo;
  var callbackA = pool.add(function(one, two) {
    capturedOne = one;
    capturedTwo = two;
  });
  assertEquals(1, pool.count());
  assertFalse(complete);

  var capturedThree;
  var callbackB = pool.add(function(three) {
    capturedThree = three;
  });
  assertEquals(2, pool.count());
  assertFalse(complete);
  assertUndefined(capturedOne);
  assertUndefined(capturedTwo);
  assertUndefined(capturedThree);

  pool.activate();

  callbackA(1, 2);
  assertEquals(1, pool.count());
  assertEquals(1, capturedOne);
  assertEquals(2, capturedTwo);
  assertFalse(complete);

  callbackB(3);
  assertEquals(0, pool.count());
  assertEquals(3, capturedThree);
  assertTrue(complete);
};


callbackPoolTest.prototype.testAddRepeated = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var callbackA = pool.add(function() {}, 2);
  assertEquals(2, pool.count());
  assertFalse(complete);

  pool.activate();

  callbackA();
  assertEquals(1, pool.count());
  assertFalse(complete);

  callbackA();
  assertEquals(0, pool.count());
  assertTrue(complete);
};


callbackPoolTest.prototype.testAddNested = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var callbackA = pool.add(function() {return pool.add(function() {});});
  assertEquals(1, pool.count());
  assertFalse(complete);

  pool.activate();

  var callbackB = callbackA();
  assertEquals(1, pool.count());
  assertFalse(complete);

  callbackB();
  assertEquals(0, pool.count());
  assertTrue(complete);
};


callbackPoolTest.prototype.testAddWithErrors = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(1, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var callbackA = pool.add(function() {throw 'error';});
  assertEquals(1, pool.count());
  assertFalse(complete);

  pool.activate();

  try {
    callbackA();
  } catch (expected) {}
  assertEquals(0, pool.count());
  assertTrue(complete);
};


callbackPoolTest.prototype.testAddErrback = function() {
  var complete = false;
  var pool = new jstestdriver.plugins.async.CallbackPool(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(1, errors.length);
    complete = true;
  });

  assertEquals(0, pool.count());

  var errback = pool.addErrback('oops');
  assertEquals(0, pool.count());
  assertFalse(complete);

  errback();
  pool.activate();

  assertEquals(0, pool.count());
  assertTrue(complete);
};
