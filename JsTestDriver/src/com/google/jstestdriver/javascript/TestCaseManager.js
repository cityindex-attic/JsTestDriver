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

/**
 * Handles the TestCases
 */
jstestdriver.TestCaseManager = function(pluginRegistrar) {
  this.testCasesInfo_ = [];
  this.fileToTestCaseMap_ = {};
  this.latestTestCaseInfo_ = null;
  this.pluginRegistrar_ = pluginRegistrar;
};


jstestdriver.TestCaseManager.prototype.add = function(testCaseInfo) {
  var index = this.indexOf_(testCaseInfo);

  if (index != -1) {
    this.testCasesInfo_.splice(index, 1, testCaseInfo);
  } else {
    this.testCasesInfo_.push(testCaseInfo);
  }
  this.latestTestCaseInfo_ = testCaseInfo;
};


jstestdriver.TestCaseManager.prototype.testCaseAdded = function() {
  return this.latestTestCaseInfo_ != null;
};


jstestdriver.TestCaseManager.prototype.updateLatestTestCase = function(filename) {
  if (this.latestTestCaseInfo_ != null) {
    var indexOfLatestTestCaseInfo = this.indexOf_(this.latestTestCaseInfo_);

    if (indexOfLatestTestCaseInfo == this.testCasesInfo_.length - 1) {
      var index = this.fileToTestCaseMap_[filename];

      if (index != undefined && index != indexOfLatestTestCaseInfo) {
        this.removeTestCase_(index);
      }
    }
    this.fileToTestCaseMap_[filename] = indexOfLatestTestCaseInfo;
    this.latestTestCaseInfo_ = null;
  }
};


jstestdriver.TestCaseManager.prototype.removeTestCaseForFilename = function(filename) {
  var index = this.fileToTestCaseMap_[filename];

  if (index != undefined) {
    this.removeTestCase_(index);
  }
};


jstestdriver.TestCaseManager.prototype.removeTestCase_ = function(index) {
  this.testCasesInfo_.splice(index, 1);
};


jstestdriver.TestCaseManager.prototype.indexOf_ = function(testCaseInfo) {
  var size = this.testCasesInfo_.length;

  for (var i = 0; i < size; i++) {
    var currentTestCaseInfo = this.testCasesInfo_[i];

    if (currentTestCaseInfo.equals(testCaseInfo)) {
      return i;
    }
  }
  return -1;
};


jstestdriver.TestCaseManager.prototype.getDefaultTestRunsConfiguration = function() {
  var testRunsConfiguration = [];
  var size = this.testCasesInfo_.length;

  for (var i = 0; i < size; i++) {
    var testCaseInfo = this.testCasesInfo_[i];

    testRunsConfiguration.push(testCaseInfo.getDefaultTestRunConfiguration());
  }
  return testRunsConfiguration;
};


jstestdriver.TestCaseManager.prototype.getTestRunsConfigurationFor = function(expressions) {
  var testRunsConfiguration = [];
  this.pluginRegistrar_.getTestRunsConfigurationFor(this.testCasesInfo_,
                                                    expressions,
                                                    testRunsConfiguration);
  return testRunsConfiguration;
};


jstestdriver.TestCaseManager.prototype.getTestCasesInfo = function() {
  return this.testCasesInfo_;
};


jstestdriver.TestCaseManager.prototype.getCurrentlyLoadedTest = function() {
  var testNames = [];
  var size = this.testCasesInfo_.length;

  for (var i = 0; i < size; i++) {
    var testCaseInfo = this.testCasesInfo_[i];
    var testCaseName = testCaseInfo.getTestCaseName();
    var tests = testCaseInfo.getTestNames();
    var testsSize = tests.length;

    for (var j = 0; j < testsSize; j++) {
      testNames.push(testCaseName + '.' + tests[j]);
    }
  }
  return {
    numTests: testNames.length,
    testNames: testNames
  };
};


jstestdriver.TestCaseManager.prototype.getCurrentlyLoadedTestFor = function(expressions) {
  var testRunsConfiguration = this.getTestRunsConfigurationFor(expressions);
  var size = testRunsConfiguration.length;
  var testNames = [];

  for (var i = 0; i < size; i++) {
    var testRunConfiguration = testRunsConfiguration[i];
    var testCaseName = testRunConfiguration.getTestCaseInfo().getTestCaseName();
    var tests = testRunConfiguration.getTests();
    var testsSize = tests.length;

    for (var j = 0; j < testsSize; j++) {
      var testName = tests[j];

      testNames.push(testCaseName + '.' + testName);
    }
  }
  return {
    numTests: testNames.length,
    testNames: testNames
  };
};
