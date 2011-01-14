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
 * @fileoverview Defines the CallbackPoolArmor class. Encapsulates a CallbackPool
 * behind a narrower interface. Also, validates arguments.
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a CallbackPoolArmor.
 * @constructor
 */
jstestdriver.plugins.async.CallbackPoolArmor = function(pool) {
  this.pool_ = pool;
};


/**
 * Adds a callback to the pool.
 * @param {Function} callback The callback to wrap.
 * @param {Number} opt_n An optional number of times to wait for the callback to
 *     be called.
 */
jstestdriver.plugins.async.CallbackPoolArmor.prototype.addCallback = function(callback, opt_n) {
  if (callback) {
    return this.pool_.addCallback(callback, opt_n);
  }
};


/**
 * @return {Function} An errback function to attach to an asynchronous system so
 *     that the test runner can be notified in the event of error.
 * @param {string} message A message to report to the user upon error.
 */
jstestdriver.plugins.async.CallbackPoolArmor.prototype.addErrback = function(message) {
  return this.pool_.addErrback(message);
};


/**
 * Adds a callback to the pool.
 * @param {Function} callback The callback to wrap.
 * @param {Number} opt_n An optional number of times to wait for the callback to
 *     be called.
 * @deprecated
 */
jstestdriver.plugins.async.CallbackPoolArmor.prototype.add =
    jstestdriver.plugins.async.CallbackPoolArmor.prototype.addCallback;


/**
 * A no-op callback that's useful for waiting until an asynchronous operation
 * completes without performing any action.
 * @param {Number} opt_n An optional number of times to wait for the callback to
 *     be called.
 */
jstestdriver.plugins.async.CallbackPoolArmor.prototype.noop = function(opt_n) {
  return this.pool_.addCallback(jstestdriver.EMPTY_FUNC, opt_n);
};
