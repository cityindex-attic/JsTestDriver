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
jstestdriver.TestRunner = function(pluginRegistrar, testRunBreaker) {
  this.pluginRegistrar_ = pluginRegistrar;
  var self = this;
  this.boundOnTestRunConfigurationComplete = function() {
    testRunBreaker(jstestdriver.bind(self,
                                     self.onTestRunConfigurationComplete_));
  };
};


jstestdriver.TestRunner.prototype.runTests = function(testRunsConfiguration, onTestDone,
    onComplete, captureConsole) {
  this.testRunsConfiguration_ = testRunsConfiguration;
  this.onTestDone_ = onTestDone;
  this.onComplete_ = onComplete;
  this.captureConsole_ = captureConsole;
  if (this.testRunsConfiguration_.length > 0) {
    this.runTestConfiguration_(this.testRunsConfiguration_.shift());
  } else {
    this.finish_();
  }
};


jstestdriver.TestRunner.prototype.runTestConfiguration_ = function(testRunConfiguration) {
  if (this.captureConsole_) {
    this.overrideConsole_();
  }
  this.pluginRegistrar_.runTestConfiguration(testRunConfiguration, this.onTestDone_,
      this.boundOnTestRunConfigurationComplete);
  if (this.captureConsole_) {
    this.resetConsole_();
  }  
};


jstestdriver.TestRunner.prototype.finish_ = function() {
  var onComplete = this.onComplete_;
  this.testRunsConfiguration_ = null;
  this.onTestDone_ = null;
  this.onComplete_ = null;
  this.captureConsole_ = false;

  onComplete();
};


jstestdriver.TestRunner.prototype.onTestRunConfigurationComplete_ = function() {
  if (this.testRunsConfiguration_.length > 0) {
    this.runTestConfiguration_(this.testRunsConfiguration_.shift());
  } else {
    this.finish_();
  }
}


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
