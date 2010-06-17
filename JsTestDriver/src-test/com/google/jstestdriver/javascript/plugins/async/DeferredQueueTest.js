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

var deferredQueueTest = TestCase('deferredQueueTest');


deferredQueueTest.prototype.testEmptyQueue = function() {
  var queueComplete = false;
  var onQueueComplete = function() {
    queueComplete = true;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  q.startStep();
  assertTrue(queueComplete);
};


deferredQueueTest.prototype.testOneStep = function() {
  var queueComplete = false;
  var onQueueComplete = function() {
    queueComplete = true;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  q.defer('Step 1', function() {
    stepOneCalled = true;
  });
  q.startStep();
  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
};


deferredQueueTest.prototype.testMultipleSteps = function() {
  var queueComplete = false;
  var onQueueComplete = function() {
    queueComplete = true;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  q.defer('Step 1', function() {
    stepOneCalled = true;
  });
  var stepTwoCalled = false;
  q.defer('Step 2', function() {
    stepTwoCalled = true;
  });
  q.startStep();
  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
};


deferredQueueTest.prototype.testMultipleStepsWithFailure = function() {
  var queueComplete = false;
  var caughtErrors = [];
  var onQueueComplete = function(errors) {
    queueComplete = true;
    caughtErrors = errors;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  q.defer('Step 1', function() {
    stepOneCalled = true;
    throw 'error';
  });
  var stepTwoCalled = false;
  q.defer('Step 2', function() {
    stepTwoCalled = true;
  });
  q.startStep();
  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);
  assertEquals(1, caughtErrors.length);
};


deferredQueueTest.prototype.testMultipleStepsWithAsynchronousCalls = function() {
  var queueComplete = false;
  var onQueueComplete = function() {
    queueComplete = true;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  var stepOneCallbackOne;
  var stepOneCallbackTwo;
  q.defer('Step 1', function(herd) {
    stepOneCalled = true;
    stepOneCallbackOne = herd.add(function() {});
    stepOneCallbackTwo = herd.add(function() {});
  });
  var stepTwoCalled = false;
  q.defer('Step 2', function() {
    stepTwoCalled = true;
  });
  q.startStep();
  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);

  stepOneCallbackOne();

  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);

  stepOneCallbackTwo();

  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
};


deferredQueueTest.prototype.testMultipleStepsWithAsynchronousCallsWithFailure = function() {
  var queueComplete = false;
  var caughtErrors = [];
  var onQueueComplete = function(errors) {
    queueComplete = true;
    caughtErrors = errors;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  var stepOneCallbackOne;
  var stepOneCallbackTwo;
  q.defer('Step 1', function(herd) {
    stepOneCalled = true;
    stepOneCallbackOne = herd.add(function() {throw 'error';});
    stepOneCallbackTwo = herd.add(function() {});
  });
  var stepTwoCalled = false;
  q.defer('Step 2', function() {
    stepTwoCalled = true;
  });
  q.startStep();
  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);
  assertEquals(0, caughtErrors.length);

  try {
    stepOneCallbackOne();
  } catch (expected) {}

  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);
  assertEquals(0, caughtErrors.length);

  stepOneCallbackTwo();

  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);
  assertEquals(1, caughtErrors.length);
};
