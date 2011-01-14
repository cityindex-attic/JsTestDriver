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
 * Executes tests for the standalone runner.
 * 
 * @param {jstestdriver.TestCaseManager}
 * @param {jstestdriver.TestRunner}
 * @param {jstestdriver.PluginRegistrar}
 * @param {function():BrowserInfo}
 * @param {jstestdriver.StandAloneTestReporter} reporter The reporter object for the stand alone runner.
 * @param {function():Number}
 * @param {function(String):Object}
 */
jstestdriver.StandAloneRunTestsCommand = function(testCaseManager,
                                                  testRunner,
                                                  pluginRegistrar,
                                                  getBrowserInfo,
                                                  reporter,
                                                  now,
                                                  jsonParse,
                                                  streamContinue,
                                                  streamStop) {
  this.testCaseManager_ = testCaseManager;
  this.testRunner_ = testRunner;
  this.pluginRegistrar_ = pluginRegistrar;
  this.jsonParse_ = jsonParse;
  this.now_ = now;
  this.boundOnTestDone_ = jstestdriver.bind(this, this.onTestDone_);
  this.boundOnComplete_ = jstestdriver.bind(this, this.onComplete);
  this.testsDone_ = [];
  this.getBrowserInfo_ = getBrowserInfo;
  this.reporter_ = reporter;
  this.streamContinue_ = streamContinue;
  this.streamStop_ = streamStop;
};


jstestdriver.StandAloneRunTestsCommand.prototype.createLog_ = function(message) {
  return new jstestdriver.BrowserLog(0, 'jstestdriver.StandAloneRunTestsCommand',
    message, this.getBrowserInfo_());
};


jstestdriver.StandAloneRunTestsCommand.prototype.runAllTests = function(args) {
  this.streamContinue_(
      new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.LOG,
          jstestdriver.JSON.stringify(this.createLog_('all tests started.')),
          this.getBrowserInfo_()));
  var captureConsole = args[0];
  this.debug_ = Boolean(args[2]);

  this.runTestCases_(this.testCaseManager_.getDefaultTestRunsConfiguration(),
      captureConsole == "true" ? true : false);
};


jstestdriver.StandAloneRunTestsCommand.prototype.runTests = function(args) {
  this.streamContinue_(
      new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.LOG,
          jstestdriver.JSON.stringify(this.createLog_('started tests.')),
          this.getBrowserInfo_()));
  var expressions = jsonParse('{"expressions":' + args[0] + '}').expressions;
  var captureConsole = args[1];
  this.debug_ = Boolean(args[2]);

  this.runTestCases_(this.testCaseManager_.getTestRunsConfigurationFor(expressions),
                     captureConsole == "true" ? true : false,
                     false);
};


jstestdriver.StandAloneRunTestsCommand.prototype.runTestCases_ = function(testRunsConfiguration,
    captureConsole) {
  this.reporter_.startTests(this.now_());
  this.totaltestruns_ = testRunsConfiguration.length;
  this.testRunner_.runTests(testRunsConfiguration,
                            this.boundOnTestDone_,
                            this.boundOnComplete_,
                            captureConsole);
};


jstestdriver.StandAloneRunTestsCommand.prototype.onTestDone_ = function(result) {
  this.reporter_.updateIsSuccess(
      result.result == jstestdriver.TestResult.RESULT.PASSED);
  this.addTestResult(result);
};


jstestdriver.StandAloneRunTestsCommand.prototype.onComplete = function() {
  var serializedTests = jstestdriver.JSON.stringify(this.testsDone_);
  this.streamContinue_(new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.TEST_RESULT,
          serializedTests,
          this.getBrowserInfo_()));
  this.reporter_.setReport(serializedTests);
  this.testsDone_ = [];
  this.reporter_.finishTests(this.now_());
  this.reporter_.setIsFinished(true);
  this.streamStop_(
      new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.LOG,
          jstestdriver.JSON.stringify(this.createLog_(
              'testing complete, isSuccess:' +
              this.reporter_.isSuccess() +
              ', isFinished:' +
              this.reporter_.isFinished())),
          this.getBrowserInfo_()));
};


jstestdriver.StandAloneRunTestsCommand.prototype.addTestResult = function(testResult) {
  this.reporter_.addTestResult(testResult);
  this.pluginRegistrar_.processTestResult(testResult);
  this.testsDone_.push(testResult);
};
