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
jstestdriver.TestRunner = function(pluginRegistrar, testBreather) {
  this.pluginRegistrar_ = pluginRegistrar;

  var runNextConfiguration_ =
      jstestdriver.bind(this,
                        this.runNextConfiguration_);

  this.boundRunNextConfiguration_ = function() {
    testBreather(runNextConfiguration_);
  };
};


/**
 * Runs all TestRunConfigurations.
 * @param {Array.<TestRunConfiguration>} testRunsConfiguration Configurations to
 *      run. This array willl be modified...
 * @param {function(jstestdriver.TestResult):null} onTestDone
 * 
 */
jstestdriver.TestRunner.prototype.runTests = function(testRunsConfiguration,
                                                      onTestDone,
                                                      onComplete,
                                                      captureConsole) {

  this.pluginRegistrar_.onTestsStart();
  this.testRunsConfiguration_ = testRunsConfiguration;
  this.onTestDone_ = onTestDone;
  this.onComplete_ = onComplete;
  this.captureConsole_ = captureConsole;
  this.runNextConfiguration_();
};


jstestdriver.TestRunner.prototype.finish_ = function() {
  var onComplete = this.onComplete_;
  this.pluginRegistrar_.onTestsFinish();
  this.testRunsConfiguration_ = null;
  this.onTestDone_ = null;
  this.onComplete_ = null;
  this.captureConsole_ = false;
  onComplete();
};


jstestdriver.TestRunner.prototype.runNextConfiguration_ = function() {
  if (this.testRunsConfiguration_.length == 0) {
    this.finish_();
    return;
  }
  this.runConfiguration(
      this.testRunsConfiguration_.shift(),
      this.onTestDone_,
      this.boundRunNextConfiguration_);
}


/**
 * Runs a test configuration.
 * @param {jstestdriver.TestRunConfiguration} config
 * @param {function(jstestdriver.TestCaseResult):null} onTestDone
 *     Function to be called when test is done.
 * @param {Function} onComplete Function to be called when all tests are done.
 */
jstestdriver.TestRunner.prototype.runConfiguration = function(config,
                                                              onTestDone,
                                                              onComplete) {
  if (this.captureConsole_) {
    this.overrideConsole_();
  }

  this.pluginRegistrar_.runTestConfiguration(
      config,
      onTestDone,
      onComplete);

  if (this.captureConsole_) {
    this.resetConsole_();
  }
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



/**
 * A map to manage the state of running TestCases.
 */
jstestdriver.TestRunner.TestCaseMap = function() {
  this.testCases_ = {};
};


/**
 * Start a TestCase.
 * @param {String} testCaseName The name of the test case to start.
 */
jstestdriver.TestRunner.TestCaseMap.prototype.startCase = function(testCaseName) {
  this.testCases_[testCaseName] = true;
};


/**
 * Stops a TestCase.
 * @param {String} testCaseName The name of the test case to stop.
 */
jstestdriver.TestRunner.TestCaseMap.prototype.stopCase = function(testCaseName) {
  this.testCases_[testCaseName] = false;
};


/**
 * Indicates if there are still cases running.
 * @param {String} testCaseName The name of the test case to stop.
 */
jstestdriver.TestRunner.TestCaseMap.prototype.hasActiveCases = function() {
  for (var testCase in this.testCases_) {
    if (this.testCases_.hasOwnProperty(testCase) && this.testCases_[testCase]) {
      return true;
    }
  }
  return false;
};
