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
 * @fileoverview Defines the DeferredQueueArmor class. Encapsulates a DeferredQueue
 * behind a narrower interface. Also, validates arguments. 
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a DeferredQueueArmor.
 */
jstestdriver.plugins.async.DeferredQueueArmor = function(q) {
  this.q_ = q;
  this.step_ = 1;
};


jstestdriver.plugins.async.DeferredQueueArmor.prototype.defer = function(description, operation) {
  if (typeof description == 'function') {
    operation = description;
    description = 'Step ' + this.step_;
  }

  if (operation) {
    this.q_.defer(description, operation);
    this.step_ += 1;
  }
};
