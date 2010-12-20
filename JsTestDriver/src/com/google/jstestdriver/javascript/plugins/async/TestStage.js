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
 * @fileoverview Defines the TestStage class.
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a TestStage.
 *
 * A TestStage is an executable portion of a test, such as setUp, tearDown, or
 * the test method.
 *
 * @param {Function} onError An error handler.
 * @param {Function} onStageComplete A callback for stage completion.
 * @param {Object} testCase The test case that owns this test stage.
 * @param {Function} testMethod The test method this stage represents.
 * @param {Function} opt_armorConstructor The constructor of DeferredQueueArmor.
 * @param {Function} opt_queueConstructor The constructor of DeferredQueue.
 * @param {Function} opt_setTimeout The setTimeout function or suitable
 *     replacement.
 * @constructor
 */
jstestdriver.plugins.async.TestStage = function(
    onError, onStageComplete, testCase, testMethod, opt_argument,
    opt_armorConstructor, opt_queueConstructor, opt_setTimeout) {
  this.onError_ = onError;
  this.onStageComplete_ = onStageComplete;
  this.testCase_ = testCase;
  this.testMethod_ = testMethod;
  this.argument_ = opt_argument;
  this.armorConstructor_ = opt_armorConstructor ||
      jstestdriver.plugins.async.DeferredQueueArmor;
  this.queueConstructor_ = opt_queueConstructor ||
      jstestdriver.plugins.async.DeferredQueue;
  this.setTimeout_ = opt_setTimeout || jstestdriver.setTimeout;
};


/**
 * Executes this TestStage.
 */
jstestdriver.plugins.async.TestStage.prototype.execute = function() {
  var armor = new (this.armorConstructor_)();
  var queue = new (this.queueConstructor_)(
      this.setTimeout_, this.testCase_, this.onStageComplete_, armor);
  armor.setQueue(queue);

  if (this.testMethod_) {
    try {
      this.testMethod_.call(this.testCase_, armor, this.argument_);
    } catch (e) {
      this.onError_(e);
    }
  }

  queue.startStep();
};
