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


asyncTestRunnerPluginTest.prototype.testTestCaseWithoutStepsWithSetupError = function() {
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


asyncTestRunnerPluginTest.prototype.testTestCaseWithCallbacksWithSetupError = function() {
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
