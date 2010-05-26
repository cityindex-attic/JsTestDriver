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


asyncTestRunnerPluginTest.MockHerd = function(setTimeout, testCase, onHerdComplete) {
  jstestdriver.plugins.async.CallbackHerd.apply(this, arguments);
};


asyncTestRunnerPluginTest.MockHerd.prototype.maybeComplete = function() {
  if (this.count_ == 0 && this.onHerdComplete_) {
    this.onHerdComplete_(this.errors_);
  }
};


asyncTestRunnerPluginTest.MockHerd.prototype.count = function() {
  return jstestdriver.plugins.async.CallbackHerd.prototype.count.apply(this, arguments);
};


asyncTestRunnerPluginTest.MockHerd.prototype.onError = function(error) {
  return jstestdriver.plugins.async.CallbackHerd.prototype.onError.apply(this, arguments);
};


asyncTestRunnerPluginTest.MockHerd.prototype.add = function(wrapped, opt_n) {
  this.count_ += opt_n || 1;
  console.log('adding. (' + this.count_ + ' in herd)');
  var callback = new jstestdriver.plugins.async.TestSafeCallbackBuilder(function() {}, function() {})
      .setHerd(this)
      .setRemainingUses(opt_n)
      .setTestCase(this.testCase_)
      .setWrapped(wrapped)
      .build();
  callback.arm(jstestdriver.plugins.async.CallbackHerd.TIMEOUT);
  return function() {
    return callback.invoke(arguments);
  };
};


asyncTestRunnerPluginTest.MockHerd.prototype.remove = function(message, opt_n) {
  return jstestdriver.plugins.async.CallbackHerd.prototype.remove.apply(this, arguments);
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
      Date, function() {}, asyncTestRunnerPluginTest.MockHerd);

  var testRunConfiguration = {};
  testRunConfiguration.getTestCaseInfo = function() {return testCaseInfo;};
  testRunConfiguration.getTests = function() {return ['testMethod'];};

  var testCaseAccepted = asyncTestRunner.runTestConfiguration(
      testRunConfiguration, function() {}, function() {});

  assertFalse(testCaseAccepted);
};


asyncTestRunnerPluginTest.prototype.testTestCaseWithoutCallbacks = function() {
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
      Date, function() {}, asyncTestRunnerPluginTest.MockHerd);

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


asyncTestRunnerPluginTest.prototype.testTestCaseWithoutCallbacksWithSetupError = function() {
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
      Date, function() {}, asyncTestRunnerPluginTest.MockHerd);

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


asyncTestRunnerPluginTest.prototype.testTestCaseWithCallbacks = function() {
  var timesSetUpCalled = 0;
  var setUpCallback;
  var timesTestMethodCalled = 0;
  var testMethodCallback;
  var timesTearDownCalled = 0;
  var tearDownCallback;

  var testCase = function() {};
  testCase.prototype.setUp = function(herd) {
    timesSetUpCalled += 1;
    setUpCallback = herd.add(function() {});
  };
  testCase.prototype.testMethod = function(herd) {
    timesTestMethodCalled += 1;
    testMethodCallback = herd.add(function() {});
  };
  testCase.prototype.tearDown = function(herd) {
    timesTearDownCalled += 1;
    tearDownCallback = herd.add(function() {});
  };

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, asyncTestRunnerPluginTest.MockHerd);

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
  assertEquals(0, timesTearDownCalled);
  assertNotUndefined(setUpCallback);
  assertUndefined(testMethodCallback);
  assertUndefined(tearDownCallback);
  assertFalse(testDone);
  assertFalse(testRunConfigurationComplete);

  setUpCallback();

  assertEquals(1, timesSetUpCalled);
  assertEquals(1, timesTestMethodCalled);
  assertEquals(0, timesTearDownCalled);
  assertNotUndefined(setUpCallback);
  assertNotUndefined(testMethodCallback);
  assertUndefined(tearDownCallback);
  assertFalse(testDone);
  assertFalse(testRunConfigurationComplete);

  testMethodCallback();

  assertEquals(1, timesSetUpCalled);
  assertEquals(1, timesTestMethodCalled);
  assertEquals(1, timesTearDownCalled);
  assertNotUndefined(setUpCallback);
  assertNotUndefined(testMethodCallback);
  assertNotUndefined(tearDownCallback);
  assertFalse(testDone);
  assertFalse(testRunConfigurationComplete);

  tearDownCallback();
  
  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('passed', testResult.result);
};


asyncTestRunnerPluginTest.prototype.testTestCaseWithCallbacksWithSetupError = function() {
  var timesSetUpCalled = 0;
  var setUpCallback;
  var timesTestMethodCalled = 0;
  var testMethodCallback;
  var timesTearDownCalled = 0;
  var tearDownCallback;

  var testCase = function() {};
  testCase.prototype.setUp = function(herd) {
    timesSetUpCalled += 1;
    setUpCallback = herd.add(function() {throw 'error';});
  };
  testCase.prototype.testMethod = function(herd) {
    timesTestMethodCalled += 1;
    testMethodCallback = herd.add(function() {});
  };
  testCase.prototype.tearDown = function(herd) {
    timesTearDownCalled += 1;
    tearDownCallback = herd.add(function() {});
  };

  var testCaseInfo = new jstestdriver.TestCaseInfo(
      'testCase', testCase, jstestdriver.TestCaseInfo.ASYNC_TYPE);

  var asyncTestRunner = new jstestdriver.plugins.async.AsyncTestRunnerPlugin(
      Date, function() {}, asyncTestRunnerPluginTest.MockHerd);

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
  assertEquals(0, timesTearDownCalled);
  assertNotUndefined(setUpCallback);
  assertUndefined(testMethodCallback);
  assertUndefined(tearDownCallback);
  assertFalse(testDone);
  assertFalse(testRunConfigurationComplete);

  try {
    setUpCallback();
  } catch (expected) {}

  assertEquals(1, timesSetUpCalled);
  assertEquals(0, timesTestMethodCalled);
  assertEquals(1, timesTearDownCalled);
  assertNotUndefined(setUpCallback);
  assertUndefined(testMethodCallback);
  assertNotUndefined(tearDownCallback);
  assertFalse(testDone);
  assertFalse(testRunConfigurationComplete);

  tearDownCallback();

  assertTrue(testDone);
  assertTrue(testRunConfigurationComplete);
  assertEquals('failed', testResult.result);
};
