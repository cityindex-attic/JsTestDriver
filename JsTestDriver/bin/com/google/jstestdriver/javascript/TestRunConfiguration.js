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
 * Represents all of the necessary information to run a test case.
 * @param {jstestdriver.TestCaseInfo} testCaseInfo The test case information, containing
 * @param {Array.<String>} tests The names of all the tests to run.
 */
jstestdriver.TestRunConfiguration = function(testCaseInfo, tests) {
  this.testCaseInfo_ = testCaseInfo;
  this.tests_ = tests;
};


jstestdriver.TestRunConfiguration.prototype.getTestCaseInfo = function() {
  return this.testCaseInfo_;
};


jstestdriver.TestRunConfiguration.prototype.getTests = function() {
  return this.tests_;
};
