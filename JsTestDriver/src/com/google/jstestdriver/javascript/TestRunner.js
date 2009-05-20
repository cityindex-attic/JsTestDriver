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
jstestdriver.TestRunner = function(clearBody, date) {
  this.clearBody_ = clearBody;
  this.dateObj_ = date;
};


jstestdriver.TestRunner.prototype.runTest = function(testCaseName, testCase, testName, captureConsole) {
  var testCaseInstance;

  try {
    testCaseInstance = new testCase();
  } catch (e) {
    return { testCaseName: testCaseName,
        testName: testName,
        result: 'error',
        message: testCaseName + ' is not a test case',
        log: '',
        time: 0
     };
  }
  var start = new this.dateObj_().getTime();

  jstestdriver.expectedAssertCount = -1;
  jstestdriver.assertCount = 0;
  var res = 'passed';
  var msg = '';

  if (captureConsole) {
    this.overrideConsole_();
  }
  try {
    testCaseInstance.setUp();
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
    testCaseInstance.tearDown();
  } catch (e) {
    if (e.name == 'AssertError') {
      res = 'failed';
    } else {
      res = 'error';
    }
    msg = JSON.stringify(e);
  } finally {
    if (captureConsole) {
      this.resetConsole_();
    }
  }
  var end = new this.dateObj_().getTime();

  this.clearBody_();
  return {
    testCaseName: testCaseName,
    testName: testName,
    result: res,
    message: msg,
    log: jstestdriver.console.getLog(),
    time: end - start
  };
};


jstestdriver.TestRunner.prototype.overrideConsole_ = function() {
  this.logMethod_ = console.log;
  this.logDebug_ = console.debug;
  this.logInfo_ = console.info;
  this.logWarn_ = console.warn;
  this.logError_ = console.error;
  console.log = function() { jstestdriver.console.log.apply(jstestdriver.console, arguments); };
  console.debug = function() { jstestdriver.console.debug.apply(jstestdriver.console, arguments); };
  console.info = function() { jstestdriver.console.info.apply(jstestdriver.console, arguments); };
  console.warn = function() { jstestdriver.console.warn.apply(jstestdriver.console, arguments); };
  console.error = function() { jstestdriver.console.error.apply(jstestdriver.console, arguments); };
};


jstestdriver.TestRunner.prototype.resetConsole_ = function() {
  console.log = this.logMethod_;
  console.debug = this.logDebug_;
  console.info = this.logInfo_;
  console.warn = this.logWarn_;
  console.error = this.logError_;  
};
