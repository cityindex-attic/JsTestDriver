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
jstestdriver.TestCaseManager = function() {
  this.testCasesInfo_ = [];
};


jstestdriver.TestCaseManager.prototype.add = function(testCaseInfo) {
  var index = this.indexOf_(testCaseInfo);

  if (index != -1) {
    this.testCasesInfo_.splice(index, 1, testCaseInfo);
  } else {
    this.testCasesInfo_.push(testCaseInfo);
  }
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
  var size = this.testCasesInfo_.length;

  for (var i = 0; i < size; i++) {
    var testCaseInfo = this.testCasesInfo_[i];
    var testRunConfiguration = testCaseInfo.getTestRunConfigurationFor(expressions);

    if (testRunConfiguration != null) {
      testRunsConfiguration.push(testRunConfiguration);
    }
  }
  return testRunsConfiguration;
};


jstestdriver.TestCaseManager.prototype.getTestCasesInfo = function() {
  return this.testCasesInfo_;
};


jstestdriver.TestCaseManager.prototype.getCurrentlyLoadedTest = function() {
  var numberOfTests = 0;
  var testNames = [];
  var size = this.testCasesInfo_.length;

  for (var i = 0; i < size; i++) {
    var testCaseInfo = this.testCasesInfo_[i];
    var testCaseName = testCaseInfo.getTestCaseName();
    var tests = testCaseInfo.getTestNames();
    var testsSize = tests.length;

    for (var j = 0; j < testsSize; j++) {
      numberOfTests++;
      testNames.push(testCaseName + '.' + tests[j]);
    }
  }
  return {
    numTests: numberOfTests,
    testNames: testNames
  };
};
