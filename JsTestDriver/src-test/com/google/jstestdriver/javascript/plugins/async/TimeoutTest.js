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

var timeoutTest = TestCase('timeoutTest');


timeoutTest.prototype.testArm = function() {
  var called = false;
  var testCase = this;
  var timeout = new jstestdriver.plugins.async.Timeout(
      function(callback, timeout) { return testCase.setTimeout(callback, timeout);},
      function(handle) { testCase.clearTimeout(handle);});

  assertUndefined(this.callback_);
  assertFalse(timeout.isArmed());
  timeout.arm(function() {
    called = true;
  });
  assertNotUndefined(this.callback_);
  assertNotNull(this.callback_);
  assertTrue(timeout.isArmed());
  this.timeout();
  assertTrue(called);
  assertNull(this.callback_);
  assertFalse(timeout.isArmed());
};


timeoutTest.prototype.testDisarm = function() {
  var called = false;
  var testCase = this;
  var timeout = new jstestdriver.plugins.async.Timeout(
      function(callback, timeout) { return testCase.setTimeout(callback, timeout);},
      function(handle) { testCase.clearTimeout(handle);});

  assertUndefined(this.callback_);
  assertFalse(timeout.isArmed());
  timeout.arm(function() {
    called = true;
  });
  assertNotUndefined(this.callback_);
  assertNotNull(this.callback_);
  assertTrue(timeout.isArmed());
  timeout.maybeDisarm();
  assertFalse(called);
  assertNull(this.callback_);
  assertFalse(timeout.isArmed());
};


timeoutTest.prototype.setTimeout = function(callback, timeout) {
  this.callback_ = callback;
  return 'handleIt';
};


timeoutTest.prototype.timeout = function() {
  this.callback_.apply(this, arguments);
};


timeoutTest.prototype.clearTimeout = function(handle) {
  this.callback_ = null;
};
