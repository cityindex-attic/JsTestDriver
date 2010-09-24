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
 * @fileoverview Defines the DeferredQueue class.
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs a DeferredQueue.
 */
jstestdriver.plugins.async.DeferredQueue = function(setTimeout, testCase, onQueueComplete,
    opt_queueConstructor, opt_queueArmorConstructor, opt_poolConstructor, opt_poolArmorConstructor) {
  this.setTimeout_ = setTimeout;
  this.testCase_ = testCase;
  this.onQueueComplete_ = onQueueComplete;
  this.queueConstructor_ = opt_queueConstructor || jstestdriver.plugins.async.DeferredQueue;
  this.queueArmorConstructor_ = opt_queueArmorConstructor || jstestdriver.plugins.async.DeferredQueueArmor;
  this.poolConstructor_ = opt_poolConstructor || jstestdriver.plugins.async.CallbackPool;
  this.poolArmorConstructor_ = opt_poolArmorConstructor || jstestdriver.plugins.async.CallbackPoolArmor;
  this.descriptions_ = [];
  this.operations_ = [];
  this.errors_ = [];
};


jstestdriver.plugins.async.DeferredQueue.prototype.execute_ = function(operation, onQueueComplete) {
  var queue = new (this.queueConstructor_)(this.setTimeout_, this.testCase_, onQueueComplete);
  var queueArmor = new (this.queueArmorConstructor_)(queue);

  var onPoolComplete = function(errors) {
    queue.finishStep_(errors);
  };
  var pool = new (this.poolConstructor_)(this.setTimeout_, this.testCase_, onPoolComplete);
  var poolArmor = new (this.poolArmorConstructor_)(pool);

  if (operation) {
    try {
      operation.call(this.testCase_, poolArmor, queueArmor);
    } catch (e) {
      this.errors_.push(e);
    }
  }

  pool.activate();
};


jstestdriver.plugins.async.DeferredQueue.prototype.defer = function(description, operation) {
  this.descriptions_.push(description);
  this.operations_.push(operation);
};


jstestdriver.plugins.async.DeferredQueue.prototype.startStep = function() {
  var nextDescription = this.descriptions_.shift();
  var nextOp = this.operations_.shift();
  if (nextOp) {
    console.log('Starting step: \'' + nextDescription + '\'');
    var q = this;
    this.execute_(nextOp, function(errors) {
      q.finishStep_(errors);
    });
  } else {
    this.onQueueComplete_([]);
  }
};


jstestdriver.plugins.async.DeferredQueue.prototype.finishStep_ = function(errors) {
  this.errors_ = this.errors_.concat(errors);
  if (this.errors_.length) {
    this.onQueueComplete_(this.errors_);
  } else {
    this.startStep();
  }
};
