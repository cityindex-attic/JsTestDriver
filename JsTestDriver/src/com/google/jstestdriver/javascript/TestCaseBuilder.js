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
jstestdriver.TestCaseBuilder = function(testCaseManager) {
  this.testCaseManager_ = testCaseManager;
};


jstestdriver.TestCaseBuilder.prototype.TestCase = function(testCaseName, opt_proto, opt_type) {
  var testCaseClass = function() {};

  if (opt_proto) {
    testCaseClass.prototype = opt_proto;
  }
  if (typeof testCaseClass.prototype.setUp == 'undefined') {
    testCaseClass.prototype.setUp = function() {};
  }
  if (typeof testCaseClass.prototype.tearDown == 'undefined') {
    testCaseClass.prototype.tearDown = function() {};
  }
  this.testCaseManager_.add(new jstestdriver.TestCaseInfo(testCaseName, testCaseClass, opt_type));
  return testCaseClass;
};


jstestdriver.TestCaseBuilder.prototype.AsyncTestCase = function(testCaseName, opt_proto) {
  return this.TestCase(testCaseName, opt_proto, jstestdriver.TestCaseInfo.ASYNC_TYPE);
};
