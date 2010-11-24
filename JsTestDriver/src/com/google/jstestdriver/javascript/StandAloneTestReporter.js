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
 * Reporter for test results when running in stand alone mode.
 * @constructor
 */
jstestdriver.StandAloneTestReporter = function() {
  this.finished_ = false;
  this.success_ = 1;
  this.report_ = '';
  this.filesLoaded_ = 0;
  this.lastTestResult_ = "none";
};


/** 
 * @export
 */
jstestdriver.StandAloneTestReporter.prototype.isFinished = function() {
  return this.finished_;
};


jstestdriver.StandAloneTestReporter.prototype.startTests = function(when) {
};


jstestdriver.StandAloneTestReporter.prototype.finishTests = function(when) {
};


jstestdriver.StandAloneTestReporter.prototype.startLoading = function(when) {
}


jstestdriver.StandAloneTestReporter.prototype.finishLoading = function(when) {
}


/**
 * @export
 * @return {String} A json representation of the test results.
 */
jstestdriver.StandAloneTestReporter.prototype.getReport = function() {
  return this.report_;
};


/**
 * @export
 */
jstestdriver.StandAloneTestReporter.prototype.getNumFilesLoaded = function() {
  return this.filesLoaded_;
};


jstestdriver.StandAloneTestReporter.prototype.setIsFinished = function(finished) {
  this.log("finished: " + finished + ": success" + this.success_);
  this.finished_ = finished;
};


jstestdriver.StandAloneTestReporter.prototype.log = function(msg) {
  //var div = document.body.appendChild(document.createElement('div'));
  //div.innerHTML = "LOG: " + msg;
}


jstestdriver.StandAloneTestReporter.prototype.setIsSuccess = function(success) {
  this.log("success" + this.success_);
  this.success_ = success;
};


/**
 * Adds a test result to the current run.
 * @param {jstestdriver.TestResult}
 */
jstestdriver.StandAloneTestReporter.prototype.addTestResult = function(testResult) {
  this.lastTestResult_ = testResult.testCaseName + "." + testResult.testName + " " + testResult.result;
  this.log("testresult: " + this.lastTestResult_);
};


jstestdriver.StandAloneTestReporter.prototype.isSuccess = function() {
  return !!this.success_;
};


jstestdriver.StandAloneTestReporter.prototype.updateIsSuccess = function(success) {
  if (this != window.top.G_testRunner) {
    // this is a horrible hack to work around overwrites happening on file importing.
    window.top.G_testRunner = this;
  }
  this.success_ = success & this.success_;
  this.log("success" + this.success_);
};


jstestdriver.StandAloneTestReporter.prototype.setReport = function(report) {
  this.report_ = report;
};


jstestdriver.StandAloneTestReporter.prototype.addLoadedFileResults = function(filesLoaded) {
  var numberOfFilesLoaded = filesLoaded.length;
  this.log("files loaded: " + numberOfFilesLoaded);
  if (this != window.top.G_testRunner) {
    // this is a horrible hack to work around overwrites happening on file importing.
    window.top.G_testRunner = this;
  }
  this.filesLoaded_ += numberOfFilesLoaded;
};


jstestdriver.StandAloneTestReporter.prototype.toString = function() {
  return "StandAloneTestReporter(success=["
      + this.success_ + "], finished=["
      + this.finished_ + "], lastTestResult=["
      + this.lastTestResult_ + "], filesLoaded=["
      + this.filesLoaded_ + "] report=["
      + this.report_ + "])";
};
