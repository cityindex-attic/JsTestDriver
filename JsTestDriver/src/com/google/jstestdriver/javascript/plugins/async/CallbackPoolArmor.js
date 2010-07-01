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
 * @fileoverview Defines the CallbackPoolArmor class. Encapsulates a CallbackPool
 * behind a narrower interface. Also, validates arguments.
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a CallbackPoolArmor.
 */
jstestdriver.plugins.async.CallbackPoolArmor = function(pool) {
  this.pool_ = pool;
};


jstestdriver.plugins.async.CallbackPoolArmor.prototype.add = function(wrapped, opt_n) {
  if (wrapped) {
    return this.pool_.add(wrapped, opt_n);
  }
};