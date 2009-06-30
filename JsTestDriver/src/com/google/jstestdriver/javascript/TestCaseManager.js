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
jstestdriver.initializeTestCaseManager = function() {
  jstestdriver.testCaseManager = new jstestdriver.TestCaseManager(
      new jstestdriver.TestRunner(function() {
        jstestdriver.jQuery('body').children().remove();
        jstestdriver.jQuery(document).unbind();
        jstestdriver.jQuery(document).die();
        }, Date));
};


jstestdriver.TestCaseManager = function(testRunner) {
  this.testRunner_ = testRunner;
  this.testCases_ = {};
  TestCase = jstestdriver.bind(this, this.TestCase);
};


jstestdriver.TestCaseManager.prototype.TestCase = function(testCaseName, proto) {
  var testCaseClass = function() {};

  if (proto) {
    testCaseClass.prototype = proto;
  }
  if (typeof testCaseClass.prototype.setUp == 'undefined') {
    testCaseClass.prototype.setUp = function() {};
  }
  if (typeof testCaseClass.prototype.tearDown == 'undefined') {
    testCaseClass.prototype.tearDown = function() {};
  }
  this.testCases_[testCaseName] = testCaseClass;
  return testCaseClass;
};


jstestdriver.TestCaseManager.prototype.getTestCases = function() {
  var testCases = [];

  for (var testCaseName in this.testCases_) {
    testCases.push({ 'name': testCaseName,
      'tests': this.getTestNamesForTestCase(this.testCases_[testCaseName]) });
  }
  return testCases;
};


jstestdriver.TestCaseManager.prototype.getTestNamesForTestCase = function(testCase) {
  var testNames = [];

  for (var property in testCase.prototype) {
    if (property.indexOf('test') == 0) {
      testNames.push(property);
    }
  }
  return testNames;
};


jstestdriver.TestCaseManager.prototype.runTests = function(testCases, onTestDone, onComplete,
    captureConsole) {
  var testCasesLength = testCases.length;

  for (var i = 0; i < testCasesLength; i++) {
    var testCase = testCases[i];
    var testCaseName = testCase.name;
    var tests = testCase.tests;
    var testsLength = tests.length;

    if (testsLength == 0) {
      var testCaseClass = this.testCases_[testCaseName];

      if (testCaseClass) {
        tests = this.getTestNamesForTestCase(testCaseClass);
        testsLength = tests.length;
      }
    }
    for (var j = 0; j < testsLength; j++) {
      var test = tests[j];

      onTestDone(this.testRunner_.runTest(testCaseName, this.testCases_[testCaseName], test,
          captureConsole));
    }
  }
  onComplete();
};
