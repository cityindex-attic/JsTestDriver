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

var asyncTestRunnerPluginTest = TestCase('asyncTestRunnerPluginTest');

/**
 * Regression test for Issue 125:
 *     "Error in asynchronous tests:
 *      [Object object].onTestRunConfigurationComplete_ is null."
 * @bug 125
 */
asyncTestRunnerPluginTest.prototype.
    testMultipleAsyncTestCases = function() {
  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testCase0 = function() {};
  testCase0.prototype.test0 = function(queue) {};

  var info0 = new jstestdriver.TestCaseInfo(
      'testCase0', testCase0, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var config0 = {
    getTestCaseInfo: function() {return info0;},
    getTests: function() {return ['test0'];}
  };

  // ContinueTesting is the vital element to reproduce Issue 125. We use it to
  // pause the second AsyncTestCase, testCase1, which allows the stack to
  // unwind and return from the AsyncTestRunnerPlugin's first
  // onRunTestConfigurationComplete_() invocation for testCase0 and then set the
  // onRunTestConfigurationComplete_ to null. Setting to null interferes with
  // the second test case's execution, as it's paused, and causes the error:
  //     "this.onTestRunConfigurationComplete_ is not a function."
  var continueTesting;

  var testCase1 = function() {};
  testCase1.prototype.test1 = function(queue) {
    queue.defer(function(pool) {
      continueTesting = pool.add(function() {});
    });
  };

  var info1 = new jstestdriver.TestCaseInfo(
      'testCase1', testCase1, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var config1 = {
    getTestCaseInfo: function() {return info1;},
    getTests: function() {return ['test1'];}
  };

  var finalOnRunTestConfigurationCompleteIsCalled = false;

  var onTestRunConfigurationComplete = function() {
    asyncTestRunner.runTestConfiguration(config1, function() {}, function() {
      finalOnRunTestConfigurationCompleteIsCalled = true;
    });
  };

  asyncTestRunner.runTestConfiguration(
      config0, function() {}, onTestRunConfigurationComplete);

  continueTesting();

  assertTrue(finalOnRunTestConfigurationCompleteIsCalled);
};


/**
 * Regression test for Issue 137: "expectAsserts does not work with
 *    AsyncTestCase."
 * @bug 137
 */
asyncTestRunnerPluginTest.prototype.
    testExpectedAsserts_correctAmount = function() {
  // save expected assert state
  var priorExpectedAssertCount = jstestdriver.expectedAssertCount;
  var priorAssertCount = jstestdriver.assertCount;

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testCase = function() {};
  testCase.prototype.testWithExpectAsserts = function() {
    expectAsserts(3);
    assertEquals('a', 'a');
    assertTrue(true);
    assertNull(null);
  };

  var info = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var config = {
    getTestCaseInfo: function() {return info;},
    getTests: function() {return ['testWithExpectAsserts'];}
  };

  var result;
  var onTestDone = function(r) {
    result = r;
  };

  asyncTestRunner.runTestConfiguration(config, onTestDone, function() {});

  // restore expected assert state before we really assert
  jstestdriver.expectedAssertCount = priorExpectedAssertCount;
  jstestdriver.assertCount = priorAssertCount;

  assertEquals('passed', result.result);
};


/**
 * Regression test for Issue 137: "expectAsserts does not work with
 *    AsyncTestCase."
 * @bug 137
 */
asyncTestRunnerPluginTest.prototype.
    testExpectedAsserts_incorrectAmount = function() {
  // save expected assert state
  var priorExpectedAssertCount = jstestdriver.expectedAssertCount;
  var priorAssertCount = jstestdriver.assertCount;

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testCase = function() {};
  testCase.prototype.testWithExpectAsserts = function() {
    expectAsserts(5);
    assertEquals('a', 'a');
    assertTrue(true);
    assertNull(null);
  };

  var info = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var config = {
    getTestCaseInfo: function() {return info;},
    getTests: function() {return ['testWithExpectAsserts'];}
  };

  var result;
  var onTestDone = function(r) {
    result = r;
  };

  asyncTestRunner.runTestConfiguration(config, onTestDone, function() {});

  // restore expected assert state before we really assert
  jstestdriver.expectedAssertCount = priorExpectedAssertCount;
  jstestdriver.assertCount = priorAssertCount;

  assertEquals('failed', result.result);
  assertEquals('Expected \'5\' asserts but \'3\' encountered.',
      JSON.parse(result.message)[0].message);
};


asyncTestRunnerPluginTest.prototype.testScopeIsNotWindow = function() {
  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testCase = function() {};
  var testCaseInstance;
  var setUpScope;
  testCase.prototype.setUp = function() {
    testCaseInstance = asyncTestRunner.testCase_;
    setUpScope = this;
  };
  var testMethodScope;
  testCase.prototype.testMethod = function() {
    testMethodScope = this;
  };
  var tearDownScope;
  testCase.prototype.tearDown = function() {
    tearDownScope = this;
  };

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  asyncTestRunner.runTestConfiguration(
      testRunConfiguration, function() {}, function() {});

  assertFalse('window === setUpScope', window === setUpScope);
  assertFalse('window === testMethodScope', window === testMethodScope);
  assertFalse('window === tearDownScope', window === tearDownScope);

  assertTrue('testCaseInstance === setUpScope',
      testCaseInstance === setUpScope);
  assertTrue('testCaseInstance === testMethodScope',
      testCaseInstance === testMethodScope);
  assertTrue('testCaseInstance === tearDownScope',
      testCaseInstance === tearDownScope);
};


asyncTestRunnerPluginTest.prototype.testTestCaseWithWrongType = function() {
  var testCase = function() {};
  testCase.prototype.setUp = function() {};
  testCase.prototype.testMethod = function() {};
  testCase.prototype.tearDown = function() {};

  // Using DEFAULT_TYPE instead of ASYNC_TYPE
  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.DEFAULT_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, function() {}, function() {});

  assertFalse(testCaseAccepted);
};


asyncTestRunnerPluginTest.prototype.testTestCaseWithoutSteps = function() {
  var timesSetUpCalled = 0;
  var timesTestMethodCalled = 0;
  var timesTearDownCalled = 0;

  var testCase = function() {};
  testCase.prototype.setUp = function() {timesSetUpCalled += 1;};
  testCase.prototype.testMethod = function() {timesTestMethodCalled += 1;};
  testCase.prototype.tearDown = function() {timesTearDownCalled += 1;};

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testDone = false;
  var testResult;
  var onTestDone = function(result) {
    testResult = result;
    testDone = true;
  };

  var testRunConfigurationComplete = false;
  var onTestRunConfigurationComplete = function() {
    testRunConfigurationComplete = true;
  };

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, onTestDone, onTestRunConfigurationComplete);

  assertTrue(testCaseAccepted);

  assertEquals(1, timesSetUpCalled);
  assertEquals(1, timesTestMethodCalled);
  assertEquals(1, timesTearDownCalled);
  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('passed', testResult.result);
};


asyncTestRunnerPluginTest.prototype.
    testTestCaseWithoutStepsWithSetupError = function() {
  var timesSetUpCalled = 0;
  var timesTestMethodCalled = 0;
  var timesTearDownCalled = 0;

  var testCase = function() {};
  testCase.prototype.setUp = function() {timesSetUpCalled += 1; throw 'error';};
  testCase.prototype.testMethod = function() {timesTestMethodCalled += 1;};
  testCase.prototype.tearDown = function() {timesTearDownCalled += 1;};

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testDone = false;
  var testResult;
  var onTestDone = function(result) {
    testResult = result;
    testDone = true;
  };

  var testRunConfigurationComplete = false;
  var onTestRunConfigurationComplete = function() {
    testRunConfigurationComplete = true;
  };

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, onTestDone, onTestRunConfigurationComplete);

  assertTrue(testCaseAccepted);

  assertEquals(1, timesSetUpCalled);
  assertEquals(0, timesTestMethodCalled);
  assertEquals(1, timesTearDownCalled);
  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('failed', testResult.result);
};


asyncTestRunnerPluginTest.prototype.testTestCaseWithSteps = function() {
  var timesSetUpCalled = 0;
  var timesSetUpStepCalled = 0;
  var timesTestMethodCalled = 0;
  var timesTestMethodStepCalled = 0;
  var timesTearDownCalled = 0;
  var timesTearDownStepCalled = 0;

  var testCase = function() {};
  testCase.prototype.setUp = function(q) {
    timesSetUpCalled += 1;
    q.defer(function() {timesSetUpStepCalled += 1;});
  };
  testCase.prototype.testMethod = function(q) {
    timesTestMethodCalled += 1;
    q.defer(function() {timesTestMethodStepCalled += 1;});
  };
  testCase.prototype.tearDown = function(q) {
    timesTearDownCalled += 1;
    q.defer(function() {timesTearDownStepCalled += 1;});
  };

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testDone = false;
  var testResult;
  var onTestDone = function(result) {
    testResult = result;
    testDone = true;
  };

  var testRunConfigurationComplete = false;
  var onTestRunConfigurationComplete = function() {
    testRunConfigurationComplete = true;
  };

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, onTestDone, onTestRunConfigurationComplete);

  assertTrue(testCaseAccepted);

  assertEquals(1, timesSetUpCalled);
  assertEquals(1, timesSetUpStepCalled);
  assertEquals(1, timesTestMethodCalled);
  assertEquals(1, timesTestMethodStepCalled);
  assertEquals(1, timesTearDownCalled);
  assertEquals(1, timesTearDownStepCalled);
  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('passed', testResult.result);
};


asyncTestRunnerPluginTest.prototype.testTestCaseWithErrback = function() {
  var timesTestMethodCalled = 0;
  var timesTestMethodStepCalled = 0;

  var testCase = function() {};
  testCase.prototype.testMethod = function(driver) {
    timesTestMethodCalled += 1;
    driver.call(function(callbacks) {
      timesTestMethodStepCalled += 1;
      callbacks.addErrback()(new Error('whoopsie daisy'));
    });
  };

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testDone = false;
  var testResult;
  var onTestDone = function(result) {
    testResult = result;
    testDone = true;
  };

  var testRunConfigurationComplete = false;
  var onTestRunConfigurationComplete = function() {
    testRunConfigurationComplete = true;
  };

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, onTestDone, onTestRunConfigurationComplete);

  assertTrue(testCaseAccepted);

  assertEquals(1, timesTestMethodCalled);
  assertEquals(1, timesTestMethodStepCalled);
  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('failed', testResult.result);
};


asyncTestRunnerPluginTest.prototype.
    testTestCaseWithStepsWithSetupError = function() {
  var timesSetUpCalled = 0;
  var timesSetUpStepCalled = 0;
  var timesTestMethodCalled = 0;
  var timesTestMethodStepCalled = 0;
  var timesTearDownCalled = 0;
  var timesTearDownStepCalled = 0;

  var testCase = function() {};
  testCase.prototype.setUp = function(q) {
    timesSetUpCalled += 1;
    q.defer(function() {timesSetUpStepCalled += 1; throw 'error';});
  };
  testCase.prototype.testMethod = function(q) {
    timesTestMethodCalled += 1;
    q.defer(function() {timesTestMethodStepCalled += 1;});
  };
  testCase.prototype.tearDown = function(q) {
    timesTearDownCalled += 1;
    q.defer(function() {timesTearDownStepCalled += 1;});
  };

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, function(callback) {callback();});

  var testDone = false;
  var testResult;
  var onTestDone = function(result) {
    testResult = result;
    testDone = true;
  };

  var testRunConfigurationComplete = false;
  var onTestRunConfigurationComplete = function() {
    testRunConfigurationComplete = true;
  };

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, onTestDone, onTestRunConfigurationComplete);

  assertTrue(testCaseAccepted);

  assertEquals(1, timesSetUpCalled);
  assertEquals(1, timesSetUpStepCalled);
  assertEquals(0, timesTestMethodCalled);
  assertEquals(0, timesTestMethodStepCalled);
  assertEquals(1, timesTearDownCalled);
  assertEquals(1, timesTearDownStepCalled);
  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('failed', testResult.result);
};
