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
jstestdriver.plugins.TestRunnerPlugin = function(dateObj, clearBody) {
  this.dateObj_ = dateObj;
  this.clearBody_ = clearBody;
};


jstestdriver.plugins.TestRunnerPlugin.prototype.runTestConfiguration = function(
    testRunConfiguration, onTestDone, onTestRunConfigurationComplete) {
  var testCaseInfo = testRunConfiguration.getTestCaseInfo();
  var tests = testRunConfiguration.getTests();
  var size = tests.length;

  for (var i = 0; i < size; i++) {
    var testName = tests[i];

    onTestDone(this.runTest(testCaseInfo.getTestCaseName(), testCaseInfo.getTemplate(), testName));
  }
  onTestRunConfigurationComplete();
};


jstestdriver.plugins.TestRunnerPlugin.prototype.runTest = function(testCaseName, testCase,
    testName) {
  var testCaseInstance;

  try {
    testCaseInstance = new testCase();
  } catch (e) {
    return new jstestdriver.TestResult(testCaseName, testName, 'error',
        testCaseName + ' is not a test case', '', 0);
  }
  var start = new this.dateObj_().getTime();

  jstestdriver.expectedAssertCount = -1;
  jstestdriver.assertCount = 0;
  var res = 'passed';
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
    res = jstestdriver.pluginRegistrar.isFailure(e) ? 'failed' : 'error';
    msg = JSON.stringify(e);
  }
  try {
    if (testCaseInstance.tearDown) {
      testCaseInstance.tearDown();
    }
  } catch (e) {
    if (res == 'passed' && msg == '') {
      res = 'error';
      msg = JSON.stringify(e);
    }
  }
  this.clearBody_();
  var end = new this.dateObj_().getTime();

  return new jstestdriver.TestResult(testCaseName, testName, res, msg,
      jstestdriver.console.getLog(), end - start);  
};
