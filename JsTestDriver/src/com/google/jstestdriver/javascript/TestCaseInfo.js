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
jstestdriver.TestCaseInfo = function(testCaseName, template, opt_type) {
  this.testCaseName_ = testCaseName;
  this.template_ = template;
  this.type_ = opt_type || jstestdriver.TestCaseInfo.DEFAULT_TYPE;
};


jstestdriver.TestCaseInfo.DEFAULT_TYPE = 'default';


jstestdriver.TestCaseInfo.ASYNC_TYPE = 'async';


jstestdriver.TestCaseInfo.prototype.getType = function() {
  return this.type_;
};


jstestdriver.TestCaseInfo.prototype.getTestCaseName = function() {
  return this.testCaseName_;
};


jstestdriver.TestCaseInfo.prototype.getTemplate = function() {
  return this.template_;
};


jstestdriver.TestCaseInfo.prototype.getTestNames = function() {
  var testNames = [];

  for (var property in this.template_.prototype) {
    if (property.indexOf('test') == 0) {
      testNames.push(property);
    }
  }
  return testNames;
};


jstestdriver.TestCaseInfo.prototype.getDefaultTestRunConfiguration = function() {
  return this.createTestRunConfiguration_(this.getTestNames());
};


jstestdriver.TestCaseInfo.prototype.createTestRunConfiguration_ = function(tests) {
  return new jstestdriver.TestRunConfiguration(this, tests);
};


jstestdriver.TestCaseInfo.prototype.getTestRunConfigurationFor = function(expressions) {
  var testRunsConfigurationMap = {};
  var expressionsSize = expressions.length;

  for (var i = 0; i < expressionsSize; i++) {
    var expr = expressions[i];

    if (expr == 'all' || expr == '*') {
      return this.getDefaultTestRunConfiguration();
    }

    var tokens = expr.split('.');
    var tests = testRunsConfigurationMap[tokens[0]];

    if (!tests) {
      tests = [];
      testRunsConfigurationMap[tokens[0]] = tests;
    }
    if (tokens.length == 2) {
      tests.push(tokens[1]);
    } else if (tokens.length == 3 && tokens[1] == 'prototype') {
      tests.push(tokens[2]);
    } else {
      // not recognized, what to do?
    }
  }
  var testNames = testRunsConfigurationMap[this.testCaseName_];

  if (!testNames) {
    return null;
  }
  if (testNames.length == 0) {
    return this.createTestRunConfiguration_(this.getTestNames());
  }
  var filteredTests = [];
  var testNamesSize = testNames.length;

  for (var i = 0; i < testNamesSize; i++) {
    var testName = testNames[i];

    if (testName in this.template_.prototype) {
      filteredTests.push(testName);
    }
  }
  return this.createTestRunConfiguration_(filteredTests);
};


jstestdriver.TestCaseInfo.prototype.equals = function(obj) {
  return obj && typeof obj.getTestCaseName != 'undefined'
      && obj.getTestCaseName() == this.testCaseName_;
};


jstestdriver.TestCaseInfo.prototype.toString = function() {
  return "TestCaseInfo(" +
    this.testCaseName_ +
    "," + this.template_ +
    "," + this.type_ + ")";
};
