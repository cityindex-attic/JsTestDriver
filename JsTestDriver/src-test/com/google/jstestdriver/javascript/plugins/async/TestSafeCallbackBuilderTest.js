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

var testSafeCallbackBuilderTest = TestCase('testSafeCallbackBuilderTest');


testSafeCallbackBuilderTest.MockPool = function() {
  this.lastError = null;
  this.lastMessage = null;
};


testSafeCallbackBuilderTest.MockPool.prototype.onError = function(error) {
  this.lastError = error;
};


testSafeCallbackBuilderTest.MockPool.prototype.remove = function(message) {
  this.lastMessage = message;
};


testSafeCallbackBuilderTest.MockTimeout = function() {
  this.armed = false;
  this.disarmed = false;
  testSafeCallbackBuilderTest.mockTimeoutInstance = this;
};


testSafeCallbackBuilderTest.MockTimeout.prototype.arm = function() {
  this.armed = true;
};


testSafeCallbackBuilderTest.MockTimeout.prototype.maybeDisarm = function() {
  this.disarmed = true;
};


testSafeCallbackBuilderTest.prototype.testBuild = function() {
  var pool = new testSafeCallbackBuilderTest.MockPool();
  var builder = new jstestdriver.plugins.async.TestSafeCallbackBuilder(
      function() {}, function() {}, testSafeCallbackBuilderTest.MockTimeout);
  var called = false;
  var callback = builder.setPool(pool).setWrapped(function() {called = true;}).build();

  callback.arm(2000);
  assertTrue(testSafeCallbackBuilderTest.mockTimeoutInstance.armed);

  var result = callback.invoke();

  assertTrue(called);
  assertTrue(testSafeCallbackBuilderTest.mockTimeoutInstance.disarmed);
  assertNull(pool.lastError);
  assertEquals('success.', pool.lastMessage);
};