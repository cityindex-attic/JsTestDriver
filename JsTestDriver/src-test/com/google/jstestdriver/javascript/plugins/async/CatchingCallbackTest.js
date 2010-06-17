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

var catchingCallbackTest = TestCase('catchingCallbackTest');


catchingCallbackTest.MockDeferredQueue = function() {
  this.lastError = null;
  this.lastMessage = null;
};


catchingCallbackTest.MockDeferredQueue.prototype.onError = function(error) {
  this.lastError = error;
};


catchingCallbackTest.MockDeferredQueue.prototype.remove = function(message) {
  this.lastMessage = message;
};


catchingCallbackTest.prototype.testInvokeWithNoArguments = function() {
  var herd = new catchingCallbackTest.MockDeferredQueue();
  var called = false;
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, herd, function() {called = true;});

  var result = callback.invoke();

  assertTrue('Should execute its callback function.', called);
  this.assertCallbackSuccess_(herd);
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.testInvokeWithArguments = function() {
  var herd = new catchingCallbackTest.MockDeferredQueue();
  var capturedName = null;
  var capturedAge = null;
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, herd, function(name, age) {
    capturedName = name;
    capturedAge = age;
  });

  var result = callback.invoke('Robert', 23);

  this.assertCallbackReceivedParameter_('name', 'Robert', capturedName);
  this.assertCallbackReceivedParameter_('age', 23, capturedAge);
  this.assertCallbackSuccess_(herd);
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.testInvokeWithReturnValue = function() {
  var herd = new catchingCallbackTest.MockDeferredQueue();
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, herd, function() {return 'return value';});

  var result = callback.invoke();

  this.assertCallbackSuccess_(herd);
  assertEquals('Should return the return value of the wrapped callback.',
      'return value', result);
};


catchingCallbackTest.prototype.testInvokeWithError = function() {
  var herd = new catchingCallbackTest.MockDeferredQueue();
  var error = new Error();
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, herd, function() {throw error;});

  var result;
  assertException('callback.invoke()', function() {
    result = callback.invoke();
  });

  assertEquals('Should report any caught errors to the herd.', error, herd.lastError);
  assertNotNull('Should remove itself from the herd with the message \'failure\'.',
      herd.lastMessage.match(/failure: .*/));
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.testInvokeOnObject = function() {
  var object = {};
  object.field1 = true;
  object.field2 = '0909101';
  var herd = new catchingCallbackTest.MockDeferredQueue();
  var callback = new jstestdriver.plugins.async.CatchingCallback(object, herd, function() {
    assertUndefined(this.field0);
    assertTrue(this.field1);
    assertEquals('0909101', this.field2);
  });

  var result = callback.invoke();

  this.assertCallbackSuccess_(herd);
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.assertCallbackSuccess_ = function(herd) {
  assertNull('Should report no errors to the herd.', herd.lastError);
  assertEquals('Should remove itself from the herd with the message \'success\'.',
      'success.', herd.lastMessage);
};


catchingCallbackTest.prototype.assertCallbackReturnedUndefined_ = function(result) {
  assertUndefined('Should return undefined.', result);
};


catchingCallbackTest.prototype.assertCallbackReceivedParameter_ = function(name, expected, actual) {
  assertEquals('Should receive the ' + name + 'parameter.', expected, actual);
};
