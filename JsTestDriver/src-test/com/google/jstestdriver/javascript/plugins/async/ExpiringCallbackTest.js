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

var expiringCallbackTest = TestCase('expiringTestCase');


expiringCallbackTest.MockPool = function() {
  this.lastError = null;
  this.lastMessage = null;
};


expiringCallbackTest.MockPool.prototype.onError = function(error) {
  this.lastError = error;
};


expiringCallbackTest.MockPool.prototype.remove = function(message) {
  this.lastMessage = message;
};


expiringCallbackTest.MockTimeout = function() {};


expiringCallbackTest.MockTimeout.prototype.arm = function(callback, timeout) {
  this.callback = callback;
  this.timeout = timeout;
};


expiringCallbackTest.MockTimeout.prototype.trigger = function() {
  return this.callback.apply(null, arguments);
};


expiringCallbackTest.prototype.testArmAndTimeout = function() {
  var pool = new expiringCallbackTest.MockPool();
  var timeout = new expiringCallbackTest.MockTimeout();
  var testCase = this;
  var callback = new jstestdriver.plugins.async.ExpiringCallback(
      pool,
      this.buildCallback_(function() {}),
      timeout);

  callback.arm(1000);

  assertEquals(1000, timeout.timeout);
  assertTrue(callback.callback_.isUsable());

  timeout.trigger();

  assertNotNull(pool.lastError);
  assertEquals('expired.', pool.lastMessage);
  assertFalse(callback.callback_.isUsable());
};


expiringCallbackTest.prototype.testInvokeWithArguments = function() {
  var capturedName;
  var capturedAge;
  var timeout = new expiringCallbackTest.MockTimeout();
  var callback = new jstestdriver.plugins.async.ExpiringCallback(
      null,
      this.buildCallback_(function(name, age) {
        capturedName = name;
        capturedAge = age;
      }),
      timeout);

  callback.invoke('Robert', 23);

  assertEquals('Robert', capturedName);
  assertEquals(23, capturedAge);
};


expiringCallbackTest.prototype.buildCallback_ = function(wrapped) {
  var callback = {};
  callback.invoke = function() {
    return wrapped.apply(null, arguments);
  };
  return new jstestdriver.plugins.async.FiniteUseCallback(callback);
};
