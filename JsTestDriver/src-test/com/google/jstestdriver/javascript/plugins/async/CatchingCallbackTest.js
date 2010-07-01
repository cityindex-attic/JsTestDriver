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


catchingCallbackTest.MockPool = function() {
  this.lastError = null;
  this.lastMessage = null;
};


catchingCallbackTest.MockPool.prototype.onError = function(error) {
  this.lastError = error;
};


catchingCallbackTest.MockPool.prototype.remove = function(message) {
  this.lastMessage = message;
};


catchingCallbackTest.prototype.testInvokeWithNoArguments = function() {
  var pool = new catchingCallbackTest.MockPool();
  var called = false;
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, pool, function() {called = true;});

  var result = callback.invoke();

  assertTrue('Should execute its callback function.', called);
  this.assertCallbackSuccess_(pool);
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.testInvokeWithArguments = function() {
  var pool = new catchingCallbackTest.MockPool();
  var capturedName = null;
  var capturedAge = null;
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, pool, function(name, age) {
    capturedName = name;
    capturedAge = age;
  });

  var result = callback.invoke('Robert', 23);

  this.assertCallbackReceivedParameter_('name', 'Robert', capturedName);
  this.assertCallbackReceivedParameter_('age', 23, capturedAge);
  this.assertCallbackSuccess_(pool);
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.testInvokeWithReturnValue = function() {
  var pool = new catchingCallbackTest.MockPool();
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, pool, function() {return 'return value';});

  var result = callback.invoke();

  this.assertCallbackSuccess_(pool);
  assertEquals('Should return the return value of the wrapped callback.',
      'return value', result);
};


catchingCallbackTest.prototype.testInvokeWithError = function() {
  var pool = new catchingCallbackTest.MockPool();
  var error = new Error();
  var callback = new jstestdriver.plugins.async.CatchingCallback({}, pool, function() {throw error;});

  var result;
  assertException('callback.invoke()', function() {
    result = callback.invoke();
  });

  assertEquals('Should report any caught errors to the pool.', error, pool.lastError);
  assertNotNull('Should remove itself from the pool with the message \'failure\'.',
      pool.lastMessage.match(/failure: .*/));
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.testInvokeOnObject = function() {
  var object = {};
  object.field1 = true;
  object.field2 = '0909101';
  var pool = new catchingCallbackTest.MockPool();
  var callback = new jstestdriver.plugins.async.CatchingCallback(object, pool, function() {
    assertUndefined(this.field0);
    assertTrue(this.field1);
    assertEquals('0909101', this.field2);
  });

  var result = callback.invoke();

  this.assertCallbackSuccess_(pool);
  this.assertCallbackReturnedUndefined_(result);
};


catchingCallbackTest.prototype.assertCallbackSuccess_ = function(pool) {
  assertNull('Should report no errors to the pool.', pool.lastError);
  assertEquals('Should remove itself from the pool with the message \'success\'.',
      'success.', pool.lastMessage);
};


catchingCallbackTest.prototype.assertCallbackReturnedUndefined_ = function(result) {
  assertUndefined('Should return undefined.', result);
};


catchingCallbackTest.prototype.assertCallbackReceivedParameter_ = function(name, expected, actual) {
  assertEquals('Should receive the ' + name + 'parameter.', expected, actual);
};
