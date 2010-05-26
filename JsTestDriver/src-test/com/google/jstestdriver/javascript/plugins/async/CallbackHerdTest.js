/*
 * Copyright 2010 Google Inc.
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

var callbackHerdTest = TestCase('callbackHerdTest');


callbackHerdTest.prototype.testAdd = function() {
  var complete = false;
  var herd = new jstestdriver.plugins.async.CallbackHerd(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, herd.count());

  var callbackA = herd.add(function() {});
  assertEquals(1, herd.count());
  assertFalse(complete);

  var callbackB = herd.add(function() {});
  assertEquals(2, herd.count());
  assertFalse(complete);

  callbackA();
  assertEquals(1, herd.count());
  assertFalse(complete);

  callbackB();
  assertEquals(0, herd.count());
  assertTrue(complete);
};


callbackHerdTest.prototype.testAddRepeated = function() {
  var complete = false;
  var herd = new jstestdriver.plugins.async.CallbackHerd(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, herd.count());

  var callbackA = herd.add(function() {}, 2);
  assertEquals(2, herd.count());
  assertFalse(complete);

  callbackA();
  assertEquals(1, herd.count());
  assertFalse(complete);

  callbackA();
  assertEquals(0, herd.count());
  assertTrue(complete);
};


callbackHerdTest.prototype.testAddNested = function() {
  var complete = false;
  var herd = new jstestdriver.plugins.async.CallbackHerd(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(0, errors.length);
    complete = true;
  });

  assertEquals(0, herd.count());

  var callbackA = herd.add(function() {return herd.add(function() {});});
  assertEquals(1, herd.count());
  assertFalse(complete);

  var callbackB = callbackA();
  assertEquals(1, herd.count());
  assertFalse(complete);

  callbackB();
  assertEquals(0, herd.count());
  assertTrue(complete);
};


callbackHerdTest.prototype.testAddWithErrors = function() {
  var complete = false;
  var herd = new jstestdriver.plugins.async.CallbackHerd(function(callback) {
    callback();
  }, {}, function(errors) {
    assertEquals(1, errors.length);
    complete = true;
  });

  assertEquals(0, herd.count());

  var callbackA = herd.add(function() {throw 'error';});
  assertEquals(1, herd.count());
  assertFalse(complete);

  try {
    callbackA();
  } catch (expected) {}
  assertEquals(0, herd.count());
  assertTrue(complete);
};
