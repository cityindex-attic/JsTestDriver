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
};


jstestdriver.StandAloneTestReporter.prototype.isFinished = function() {
  return this.finished_;
};


/**
 * @return {String} A json representation of the test results.
 */
jstestdriver.StandAloneTestReporter.prototype.getReport = function() {
  return this.report_;
};


jstestdriver.StandAloneTestReporter.prototype.getNumFilesLoaded = function() {
  return this.filesLoaded_;
};


jstestdriver.StandAloneTestReporter.prototype.setIsFinished = function(finished) {
  this.finished_ = finished;
};


jstestdriver.StandAloneTestReporter.prototype.setIsSuccess = function(success) {
  this.success_ = success;
};


jstestdriver.StandAloneTestReporter.prototype.isSuccess = function() {
  return this.success_;
};


jstestdriver.StandAloneTestReporter.prototype.updateIsSuccess = function(success) {
  this.success_ = success && this.success_;
};


jstestdriver.StandAloneTestReporter.prototype.setReport = function(report) {
  this.report_ = report;
};


jstestdriver.StandAloneTestReporter.prototype.setNumFilesLoaded = function(filesLoaded) {
  this.filesLoaded_ = filesLoaded;
};
