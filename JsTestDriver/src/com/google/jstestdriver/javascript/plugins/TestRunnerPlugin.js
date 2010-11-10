/*
 * Copyright 2009 Google Inc.
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
jstestdriver.plugins.TestRunnerPlugin = function(dateObj, clearBody, opt_runTestLoop) {
  this.dateObj_ = dateObj;
  this.clearBody_ = clearBody;
  this.boundRunTest_ = jstestdriver.bind(this, this.runTest);
  this.runTestLoop_ = opt_runTestLoop || jstestdriver.plugins.defaultRunTestLoop;
};


jstestdriver.plugins.pausingRunTestLoop =
    function(testCaseName, template, tests, runTest, onTest, onComplete) {
  var i = 0;
  function nextTest() {
    if (tests[i]) {
      onTest(runTest(testCaseName, template, tests[i++]));
      jstestdriver.setTimeout(nextTest, 10);
    } else {
      onComplete();
    }
  }
  nextTest();
};


jstestdriver.plugins.defaultRunTestLoop =
    function(testCaseName, template, tests, runTest, onTest, onComplete) {
  for (var i = 0; tests[i]; i++) {
    onTest(runTest(testCaseName, template, tests[i]));
  }
  onComplete();
};


jstestdriver.plugins.TestRunnerPlugin.prototype.runTestConfiguration =
    function(testRunConfiguration, onTestDone, onTestRunConfigurationComplete) {
  var testCaseInfo = testRunConfiguration.getTestCaseInfo();
  var tests = testRunConfiguration.getTests();
  var size = tests.length;

  if (testCaseInfo.getType() != jstestdriver.TestCaseInfo.DEFAULT_TYPE) {
    for (var i = 0; tests[i]; i++) {
      onTestDone(new jstestdriver.TestResult(
          testCaseName,
          tests[i],
          'error',
          testCaseInfo.getTestCaseName() +
            ' is an unhandled test case: ' +
            testCaseInfo.getType(),
          '',
          0));
    }
    onTestRunConfigurationComplete();
    return;
  }

  this.runTestLoop_(testCaseInfo.getTestCaseName(),
                    testCaseInfo.getTemplate(),
                    tests,
                    this.boundRunTest_,
                    onTestDone,
                    onTestRunConfigurationComplete)
};


jstestdriver.plugins.TestRunnerPlugin.prototype.runTest =
    function(testCaseName, testCase, testName) {
  var testCaseInstance;
  try {
    try {
      testCaseInstance = new testCase();
    } catch (e) {
      return new jstestdriver.TestResult(
          testCaseName,
          testName,
          jstestdriver.TestResult.RESULT.ERROR,
          testCaseName + ' is not a test case',
          '',
          0);
    }
    var start = new this.dateObj_().getTime();
  
    jstestdriver.expectedAssertCount = -1;
    jstestdriver.assertCount = 0;
    var res = jstestdriver.TestResult.RESULT.PASSED;
    var msg = '';
  
    try {
      if (testCaseInstance.setUp) {
        testCaseInstance.setUp();
      }
      if (!(testName in testCaseInstance)) {
        var err = new Error(testName + ' not found in ' + testCaseName);
        err.name = 'AssertError';
        throw err;
      }
      testCaseInstance[testName]();
      if (jstestdriver.expectedAssertCount != -1 &&
          jstestdriver.expectedAssertCount != jstestdriver.assertCount) {
        var err = new Error("Expected '" +
            jstestdriver.expectedAssertCount +
            "' asserts but '" +
            jstestdriver.assertCount +
            "' encountered.");
  
        err.name = 'AssertError';
        throw err;
      }
    } catch (e) {
      // We use the global here because of a circular dependency. The isFailure plugin should be
      // refactored.
      res = jstestdriver.pluginRegistrar.isFailure(e) ?
          jstestdriver.TestResult.RESULT.FAILED :
            jstestdriver.TestResult.RESULT.ERROR;
      msg = this.serializeError(e);
    }
    try {
      if (testCaseInstance.tearDown) {
        testCaseInstance.tearDown();
      }
      this.clearBody_();
    } catch (e) {
      if (res == jstestdriver.TestResult.RESULT.PASSED && msg == '') {
        res = jstestdriver.TestResult.RESULT.ERROR;
      }
      msg = this.serializeError(e);
    }
    var end = new this.dateObj_().getTime();
    return new jstestdriver.TestResult(testCaseName, testName, res, msg,
            jstestdriver.console.getAndResetLog(), end - start);
  } catch (e) {
    return new jstestdriver.TestResult(testCaseName, testName,
            'error', 'Unexpected runner error: ' + e.toString(),
            jstestdriver.console.getAndResetLog(), 0);
  }
};

/**
 *@param {Error} e
 */
jstestdriver.plugins.TestRunnerPlugin.prototype.serializeError = function(e) {
  //return JSON.stringify(e);
  return JSON.stringify({
    'message' : String(e.message),
    'name' : String(e.name),
    'fileName' : String(e.fileName),
    'lineNumber' : Number(e.lineNumber),
    'number' : Number(e.number),
    'description' : String(e.description),
    'stack' : String(e.stack)
  });
};
