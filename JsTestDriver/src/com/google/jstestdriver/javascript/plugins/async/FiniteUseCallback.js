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
 * @fileoverview Defines the FiniteUseCallback class, which decorates a
 * Javascript function by restricting the number of times the asynchronous
 * system may call it.
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a FiniteUseCallback.
 *
 * @param callback a CatchingCallback.
 * @param onDepleted a function to execute when this FiniteUseCallback depletes.
 * @param opt_remainingUses the number of permitted uses remaining; defaults to
 * one.
 */
jstestdriver.plugins.async.FiniteUseCallback = function(callback, onDepleted, opt_remainingUses) {
  this.callback_ = callback;
  this.onDepleted_ = onDepleted;
  this.remainingUses_ = opt_remainingUses || 1;
};


/**
 * Depletes the remaining permitted uses.  Calls onDepleted.
 */
jstestdriver.plugins.async.FiniteUseCallback.prototype.deplete = function() {
  this.remainingUses_ = 0;
  if (this.onDepleted_) {
    this.onDepleted_.apply();
  }
};


/**
 * Returns the number of remaining permitted uses.
 */
jstestdriver.plugins.async.FiniteUseCallback.prototype.getRemainingUses = function() {
  return this.remainingUses_;
};


/**
 * Invokes this callback if it is usable. Calls onDepleted if invoking this
 * callback depletes its remaining permitted uses.
 */
jstestdriver.plugins.async.FiniteUseCallback.prototype.invoke = function(var_args) {
  if (this.isUsable()) {
    try {
      this.remainingUses_ -= 1;
      return this.callback_.invoke.apply(this.callback_, arguments);
    } finally {
      if (this.onDepleted_ && !this.isUsable()) {
        this.onDepleted_.apply();
      }
    }
  } else {
    console.log('Warning. Attempted to call unusable callback.');
  }
};


/**
 * Returns true if any permitted uses remain.
 */
jstestdriver.plugins.async.FiniteUseCallback.prototype.isUsable = function() {
  return this.remainingUses_ > 0;
};
