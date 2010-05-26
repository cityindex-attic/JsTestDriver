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
 * @fileoverview Defines the ExpiringCallback class, which decorates a
 * Javascript function by restricting the length of time the asynchronous system
 * may delay before calling the function.
 * 
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs an ExpiringCallback.
 *
 * @param herd the herd to which this callback belongs.
 * @param callback a FiniteUseCallback.
 * @param timeout a Timeout object.
 */
jstestdriver.plugins.async.ExpiringCallback = function(herd, callback, timeout) {
  this.herd_ = herd;
  this.callback_ = callback;
  this.timeout_ = timeout;
};


/**
 * Arms this callback to expire after the given delay.
 * 
 * @param delay the amount of time before this callback expires.
 */
jstestdriver.plugins.async.ExpiringCallback.prototype.arm = function(delay) {
  var callback = this;
  this.timeout_.arm(function() {
    callback.herd_.onError(new Error('expired.'));
    callback.herd_.remove('expired.', callback.callback_.getRemainingUses());
    callback.callback_.deplete();
  }, delay);
};


/**
 * Invokes this callback.
 */
jstestdriver.plugins.async.ExpiringCallback.prototype.invoke = function() {
  return this.callback_.invoke.apply(this.callback_, arguments);
};

