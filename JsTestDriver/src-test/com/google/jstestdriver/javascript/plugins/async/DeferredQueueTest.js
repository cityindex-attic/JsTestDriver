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
  var caughtErrors;
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


deferredQueueTest.prototype.testNestedSteps = function() {
  var step = 0;
  var queueComplete = false;
  var caughtErrors;
  var onQueueComplete = function(errors) {
    queueComplete = true;
    caughtErrors = errors;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  var stepTwoCalled = false;
  q.defer('Step 1', function(unused, childQ) {
    stepOneCalled = true;
    assertEquals(0, step);
    step = 1;

    childQ.defer('Step 2', function() {
      stepTwoCalled = true;
      assertEquals(1, step);
      step = 2;
    });
  });
  var stepThreeCalled = false;
  q.defer('Step 3', function() {
    stepThreeCalled = true;
    assertEquals(2, step);
    step = 3;
  });
  q.startStep();
  assertEquals(0, caughtErrors.length);
  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
  assertTrue(stepThreeCalled);
};


deferredQueueTest.prototype.testNestedStepsWithFailure = function() {
  var step = 0;
  var queueComplete = false;
  var caughtErrors;
  var onQueueComplete = function(errors) {
    queueComplete = true;
    caughtErrors = errors;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  var stepTwoCalled = false;
  q.defer('Step 1', function(unused, childQ) {
    stepOneCalled = true;
    assertEquals(0, step);
    step = 1;

    childQ.defer('Step 2', function() {
      stepTwoCalled = true;
      assertEquals(1, step);
      step = 2;
      throw 'error';
    });
  });
  var stepThreeCalled = false;
  q.defer('Step 3', function() {
    stepThreeCalled = true;
    assertEquals(2, step);
    step = 3;
  });
  q.startStep();
  assertEquals(1, caughtErrors.length);
  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
  assertFalse(stepThreeCalled);
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
  q.defer('Step 1', function(pool) {
    stepOneCalled = true;
    stepOneCallbackOne = pool.add(function() {});
    stepOneCallbackTwo = pool.add(function() {});
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
  q.defer('Step 1', function(pool) {
    stepOneCalled = true;
    stepOneCallbackOne = pool.add(function() {throw 'error';});
    stepOneCallbackTwo = pool.add(function() {});
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


deferredQueueTest.prototype.testNestedStepsWithAsynchronousCalls = function() {
  var queueComplete = false;
  var onQueueComplete = function() {
    queueComplete = true;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  var stepOneCallback;
  var stepTwoCalled = false;
  var stepTwoCallback;
  q.defer('Step 1', function(pool, child) {
    stepOneCalled = true;
    stepOneCallback = pool.add(function() {});

    child.defer('Step 2', function(pool) {
      stepTwoCalled = true;
      stepTwoCallback = pool.add(function() {});
    });
  });
  var stepThreeCalled = false;
  q.defer('Step 3', function() {
    stepThreeCalled = true;
  });
  q.startStep();
  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);
  assertFalse(stepThreeCalled);

  stepOneCallback();

  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
  assertFalse(stepThreeCalled);

  stepTwoCallback();

  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
  assertTrue(stepThreeCalled);
};


deferredQueueTest.prototype.testNestedStepsWithAsynchronousCallsWithFailure = function() {
  var queueComplete = false;
  var caughtErrors = [];
  var onQueueComplete = function(errors) {
    queueComplete = true;
    caughtErrors = errors;
  };
  var q = new jstestdriver.plugins.async.DeferredQueue(
      function(callback) {callback();}, {}, onQueueComplete);
  var stepOneCalled = false;
  var stepOneCallback;
  var stepTwoCalled = false;
  var stepTwoCallback;
  q.defer('Step 1', function(pool, child) {
    stepOneCalled = true;
    stepOneCallback = pool.add(function() {});

    child.defer('Step 2', function(pool) {
      stepTwoCalled = true;
      stepTwoCallback = pool.add(function() {throw 'error';});
    });
  });
  var stepThreeCalled = false;
  q.defer('Step 3', function() {
    stepThreeCalled = true;
  });
  q.startStep();
  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertFalse(stepTwoCalled);
  assertFalse(stepThreeCalled);

  stepOneCallback();

  assertFalse(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
  assertFalse(stepThreeCalled);

  try {
    stepTwoCallback();
  } catch (expected) {}
  
  assertTrue(queueComplete);
  assertTrue(stepOneCalled);
  assertTrue(stepTwoCalled);
  assertFalse(stepThreeCalled);
  assertEquals(1, caughtErrors.length);
};
