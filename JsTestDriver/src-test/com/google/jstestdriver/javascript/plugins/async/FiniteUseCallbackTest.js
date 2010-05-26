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

var finiteUseCallbackTest = TestCase('finiteUseCallbackTest');


finiteUseCallbackTest.prototype.testInvoke = function() {
  var useCount = 0;
  var depleted = false;
  var callback = new jstestdriver.plugins.async.FiniteUseCallback(
      this.buildCallback_(function() {useCount += 1; return 'hi'}),
      function() {depleted = true;}, 2);

  assertTrue(callback.isUsable());
  assertEquals('hi', callback.invoke());
  assertEquals(1, useCount);
  
  assertFalse(depleted);
  assertTrue(callback.isUsable());
  assertEquals('hi', callback.invoke());
  assertEquals(2, useCount);

  assertTrue(depleted);
  assertFalse(callback.isUsable());
  assertUndefined(callback.invoke());
  assertEquals(2, useCount);
};


finiteUseCallbackTest.prototype.testInvokeWithDefaultCount = function() {
  var useCount = 0;
  var depleted = false;
  var callback = new jstestdriver.plugins.async.FiniteUseCallback(
      this.buildCallback_(function() {useCount += 1; return 'hi'}),
      function() {depleted = true;});

  assertTrue(callback.isUsable());
  assertEquals('hi', callback.invoke());
  assertEquals(1, useCount);

  assertTrue(depleted);
  assertFalse(callback.isUsable());
  assertUndefined(callback.invoke());
  assertEquals(1, useCount);
};


finiteUseCallbackTest.prototype.testInvokeWithArguments = function() {
  var capturedName;
  var capturedAge;
  var callback = new jstestdriver.plugins.async.FiniteUseCallback(
      this.buildCallback_(function(name, age) {capturedName = name; capturedAge = age;}));

  callback.invoke('Robert', 23);

  assertEquals('Robert', capturedName);
  assertEquals(23, capturedAge);
};


finiteUseCallbackTest.prototype.testDeplete = function() {
  var useCount = 0;
  var depleted = false;
  var callback = new jstestdriver.plugins.async.FiniteUseCallback(
      this.buildCallback_(function() {useCount += 1; return 'hi';}),
      function() {depleted = true;}, 2);

  assertTrue(callback.isUsable());

  callback.deplete();

  assertFalse(callback.isUsable());
  assertUndefined(callback.invoke());
  assertEquals(0, useCount);
  assertTrue(depleted);
};


finiteUseCallbackTest.prototype.buildCallback_ = function(wrapped) {
  var callback = {};
  callback.invoke = function() {
    return wrapped.apply(null, arguments);
  };
  return callback;
};
