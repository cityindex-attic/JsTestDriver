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
jstestdriver.RunTestsCommand = function(testCaseManager,
                                        testRunner,
                                        pluginRegistrar,
                                        getBrowserInfo,
                                        now,
                                        jsonParse,
                                        streamContinue,
                                        streamStop) {
  this.testCaseManager_ = testCaseManager;
  this.testRunner_ = testRunner;
  this.pluginRegistrar_ = pluginRegistrar;
  this.jsonParse_ = jsonParse;
  this.streamContinue_ = streamContinue;
  this.streamStop_ = streamStop;
  this.now_ = now;
  this.boundOnTestDone_ = jstestdriver.bind(this, this.onTestDone_);
  this.boundOnComplete_ = jstestdriver.bind(this, this.onComplete_);
  this.testsDone_ = [];
  this.getBrowserInfo_ = getBrowserInfo;
};


jstestdriver.RunTestsCommand.prototype.runAllTests = function(args) {
  var captureConsole = args[0];
  this.debug_ = Boolean(args[2]);

  this.runTestCases_(this.testCaseManager_.getDefaultTestRunsConfiguration(),
      captureConsole == "true" ? true : false);
};


jstestdriver.RunTestsCommand.prototype.runTests = function(args) {
  var expressions = this.jsonParse_('{"expressions":' + args[0] + '}').expressions;
  var captureConsole = args[1];
  this.debug_ = Boolean(args[2]);

  jstestdriver.log('jstestdriver.RunTestsCommand.prototype.runTests: running tests ' + args);
  this.runTestCases_(this.testCaseManager_.getTestRunsConfigurationFor(expressions),
      captureConsole == "true" ? true : false);
};


jstestdriver.RunTestsCommand.prototype.runTestCases_ =
    function(testRunsConfiguration, captureConsole) {
  this.lastTestResultsSent_ = this.now_();
  this.testRunner_.runTests(testRunsConfiguration,
                            this.boundOnTestDone_,
                            this.boundOnComplete_,
                            captureConsole);
};


jstestdriver.RunTestsCommand.prototype.sendTestResults = function() {
  if (this.testsDone_.length > 0) {
    var response = new jstestdriver.Response(
            jstestdriver.RESPONSE_TYPES.TEST_RESULT,
            JSON.stringify(this.testsDone_),
            this.getBrowserInfo_());

    this.testsDone_ = [];
    this.streamContinue_(response);
  }
};


//TODO(corysmith): refactor the testsDone collection into a separate object.
jstestdriver.RunTestsCommand.prototype.onTestDone_ = function(result) {
  this.addTestResult(result);
  var elapsed = this.now_() - this.lastTestResultsSent_;
  if ((result.result == 'error' ||
       result.log != '' ||
       this.debug_ ||
       elapsed > jstestdriver.TIMEOUT)) {
    this.lastTestResultsSent_ = this.now_();
    this.sendTestResults();
  }
};


// TODO(corysmith): refactor the testsDone collection into a separate object.
jstestdriver.RunTestsCommand.prototype.addTestResult = function(testResult) {
  this.pluginRegistrar_.processTestResult(testResult);
  this.testsDone_.push(testResult);
};


jstestdriver.RunTestsCommand.prototype.sendTestResultsOnComplete_ = function() {
  var response = new jstestdriver.Response(
      jstestdriver.RESPONSE_TYPES.TEST_RESULT,
      JSON.stringify(this.testsDone_),
      this.getBrowserInfo_());
  this.testsDone_ = [];
  this.streamStop_(response);
};


jstestdriver.RunTestsCommand.prototype.onComplete_ = function() {
  this.sendTestResultsOnComplete_();
};
