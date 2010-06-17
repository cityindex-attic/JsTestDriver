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


/**
 * @fileoverview Defines the CallbackHerd class, which decorates given callback
 * functions with safeguards and tracks them until they execute or expire.
 * 
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a CallbackHerd.
 *
 * @param setTimeout the global setTimeout function.
 * @param testCase the test case instance.
 * @param onHerdComplete a function to call when the herd empties.
 */
jstestdriver.plugins.async.CallbackHerd = function(setTimeout, testCase, onHerdComplete) {
  this.setTimeout_ = setTimeout;
  this.testCase_ = testCase;
  this.onHerdComplete_ = onHerdComplete;
  this.errors_ = [];
  this.count_ = 0;
};


/**
 * The number of milliseconds to wait before expiring a delinquent callback.
 */
jstestdriver.plugins.async.CallbackHerd.TIMEOUT = 30000;


/**
 * Calls onHerdComplete if the herd is empty.
 */
jstestdriver.plugins.async.CallbackHerd.prototype.maybeComplete = function() {
  if (this.count_ == 0 && this.onHerdComplete_) {
    var herd = this;
    this.setTimeout_(function() {
      herd.onHerdComplete_(herd.errors_);
    }, 0);
  }
};


/**
 * Returns the number of outstanding callbacks in the herd.
 */
jstestdriver.plugins.async.CallbackHerd.prototype.count = function() {
  return this.count_;
};


/**
 * Accepts errors to later report them to the test runner via onHerdComplete.
 * @param error the error to report
 */
jstestdriver.plugins.async.CallbackHerd.prototype.onError = function(error) {
  this.errors_.push(error);
};


/**
 * Adds a callback function to the herd, optionally more than once.
 *
 * @param wrapped the callback function to decorate with safeguards and to add
 * to the herd.
 * @param opt_n the number of permitted uses of the given callback; defaults to one.
 */
jstestdriver.plugins.async.CallbackHerd.prototype.add = function(wrapped, opt_n) {
  this.count_ += opt_n || 1;
  console.log('adding. (' + this.count_ + ' in herd)');
  var callback = new jstestdriver.plugins.async.TestSafeCallbackBuilder()
      .setHerd(this)
      .setRemainingUses(opt_n)
      .setTestCase(this.testCase_)
      .setWrapped(wrapped)
      .build();
  callback.arm(jstestdriver.plugins.async.CallbackHerd.TIMEOUT);
  return function() {
    return callback.invoke(arguments);
  };
};


/**
 * Removes a callback from the herd, optionally more than one.
 *
 * @param message a message to pass to the herd for logging purposes; usually the
 * reason that the callback was removed from the herd.
 * @param opt_n the number of callbacks to remove from the herd.
 */
jstestdriver.plugins.async.CallbackHerd.prototype.remove = function(message, opt_n) {
  if (this.count_ > 0) {
    this.count_ -= opt_n || 1;
    if (message) {
      console.log(message + ' (' + this.count_ + ' in herd)');
    }
    this.maybeComplete();
  }
};
