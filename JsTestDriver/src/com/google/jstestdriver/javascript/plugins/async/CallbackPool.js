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


/**
 * @fileoverview Defines the CallbackPool class, which decorates given callback
 * functions with safeguards and tracks them until they execute or expire.
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a CallbackPool.
 *
 * @param setTimeout the global setTimeout function.
 * @param testCase the test case instance.
 * @param onPoolComplete a function to call when the pool empties.
 */
jstestdriver.plugins.async.CallbackPool = function(setTimeout, testCase, onPoolComplete) {
  this.setTimeout_ = setTimeout;
  this.testCase_ = testCase;
  this.onPoolComplete_ = onPoolComplete;
  this.errors_ = [];
  this.count_ = 0;
  this.active_ = false;
};


/**
 * The number of milliseconds to wait before expiring a delinquent callback.
 */
jstestdriver.plugins.async.CallbackPool.TIMEOUT = 30000;


/**
 * Calls onPoolComplete if the pool is active and empty.
 */
jstestdriver.plugins.async.CallbackPool.prototype.maybeComplete = function() {
  if (this.active_ && this.count_ == 0 && this.onPoolComplete_) {
    var pool = this;
    this.setTimeout_(function() {
      pool.onPoolComplete_(pool.errors_);
    }, 0);
  }
};


/**
 * Activates the pool and calls maybeComplete.
 */
jstestdriver.plugins.async.CallbackPool.prototype.activate = function() {
    this.active_ = true;
    this.maybeComplete();
};


/**
 * Returns the number of outstanding callbacks in the pool.
 */
jstestdriver.plugins.async.CallbackPool.prototype.count = function() {
  return this.count_;
};


/**
 * Accepts errors to later report them to the test runner via onPoolComplete.
 * @param error the error to report
 */
jstestdriver.plugins.async.CallbackPool.prototype.onError = function(error) {
  this.errors_.push(error);
};


/**
 * Adds a callback function to the pool, optionally more than once.
 *
 * @param wrapped the callback function to decorate with safeguards and to add
 * to the pool.
 * @param opt_n the number of permitted uses of the given callback; defaults to one.
 */
jstestdriver.plugins.async.CallbackPool.prototype.addCallback = function(wrapped, opt_n) {
  this.count_ += opt_n || 1;
  //console.log('adding. (' + this.count_ + ' in pool)');
  var callback = new jstestdriver.plugins.async.TestSafeCallbackBuilder()
      .setPool(this)
      .setRemainingUses(opt_n)
      .setTestCase(this.testCase_)
      .setWrapped(wrapped)
      .build();
  callback.arm(jstestdriver.plugins.async.CallbackPool.TIMEOUT);
  return function() {
    return callback.invoke.apply(callback, arguments);
  };
};


/**
 * Adds a callback function to the pool, optionally more than once.
 *
 * @param wrapped the callback function to decorate with safeguards and to add
 * to the pool.
 * @param opt_n the number of permitted uses of the given callback; defaults to one.
 * @deprecated
 */
jstestdriver.plugins.async.CallbackPool.prototype.add =
    jstestdriver.plugins.async.CallbackPool.prototype.addCallback;


/**
 * @return {Function} An errback function to attach to an asynchronous system so
 *     that the test runner can be notified in the event of error.
 * @param {string} message A message to report to the user upon error.
 */
jstestdriver.plugins.async.CallbackPool.prototype.addErrback = function(message) {
  var pool = this;
  return function() {
    pool.onError(new Error(
        'Errback ' + message + ' called with arguments: ' +
            Array.prototype.slice.call(arguments)));
    pool.count_ = 0;
    pool.maybeComplete();
  };
};


/**
 * Removes a callback from the pool, optionally more than one.
 *
 * @param message a message to pass to the pool for logging purposes; usually the
 * reason that the callback was removed from the pool.
 * @param opt_n the number of callbacks to remove from the pool.
 */
jstestdriver.plugins.async.CallbackPool.prototype.remove = function(message, opt_n) {
  if (this.count_ > 0) {
    this.count_ -= opt_n || 1;
    /*if (message) {
      console.log(message + ' (' + this.count_ + ' in pool)');
    }*/
    this.maybeComplete();
  }
};
