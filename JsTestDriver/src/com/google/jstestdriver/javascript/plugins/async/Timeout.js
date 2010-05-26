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
 * @fileoverview Defines the Timeout class.  The arm() method is equivalent to
 * window.setTimeout() and maybeDisarm() is equivalent to window.clearTimeout().
 *  
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a Timeout. Accepts alternate implementations of setTimeout and
 * clearTimeout.
 *
 * @param setTimeout the global setTimeout function to use.
 * @param clearTimeout the global clearTimeout function to use.
 */
jstestdriver.plugins.async.Timeout = function(setTimeout, clearTimeout) {
  this.setTimeout_ = setTimeout;
  this.clearTimeout_ = clearTimeout;
  this.handle_ = null;
};


/**
 * Arms this Timeout to fire after the specified delay.
 *
 * @param callback the callback to call after the delay passes.
 * @param delay the timeout delay in milliseconds.
 */
jstestdriver.plugins.async.Timeout.prototype.arm = function(callback, delay) {
  var self = this;
  this.handle_ = this.setTimeout_(function() {
    self.maybeDisarm();
    return callback.apply(null, arguments);
  }, delay);
};

/**
 * Explicitly disarms the timeout.
 */
jstestdriver.plugins.async.Timeout.prototype.disarm_ = function() {
  this.clearTimeout_(this.handle_);
  this.handle_ = null;
};


/**
 * Returns true if the timeout is armed.
 */
jstestdriver.plugins.async.Timeout.prototype.isArmed = function() {
  return this.handle_ != null;
};


/**
 * Disarms the timeout if it is armed.
 */
jstestdriver.plugins.async.Timeout.prototype.maybeDisarm = function() {
  if (this.isArmed()) {
    this.disarm_();
  }
};
