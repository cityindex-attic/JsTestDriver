/*
 * Copyright 2011 Google Inc.
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
 * @fileoverview
 * @author corysmith@google.com (Cory Smith)
 * @author rdionne@google.com (Robert Dionne)
 */



/**
 * Constructs a TestCaseBuilder.
 * @param {jstestdriver.TestCaseManager} testCaseManager The appropriate test
 *     case manager.
 * @constructor
 */
jstestdriver.TestCaseBuilder = function(testCaseManager) {
  this.testCaseManager_ = testCaseManager;
};


/**
 * Defines a test case.
 * @param {string} testCaseName The name of the test case.
 * @param {Object} opt_proto An optional prototype.
 * @param {Object} opt_type Either DEFAULT_TYPE or ASYNC_TYPE.
 * @return {Function} Base function that represents the test case class.
 */
jstestdriver.TestCaseBuilder.prototype.TestCase =
    function(testCaseName, opt_proto, opt_type) {
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
  this.testCaseManager_.add(
      new jstestdriver.TestCaseInfo(testCaseName, testCaseClass, opt_type));
  return testCaseClass;
};


/**
 * Defines an asynchronous test case.
 * @param {string} testCaseName The name of the test case.
 * @param {Object} opt_proto An optional prototype.
 * @return {Function} Base function that represents the asyncronous test case
 *     class.
 */
jstestdriver.TestCaseBuilder.prototype.AsyncTestCase =
    function(testCaseName, opt_proto) {
  return this.TestCase(
      testCaseName, opt_proto, jstestdriver.TestCaseInfo.ASYNC_TYPE);
};


/**
 * A TestCase that will only be executed when a certain condition is true.
 * @param {string} The name of the TestCase.
 * @param {function():boolean} A function that indicates if this case should be
 *     run.
 * @param {Object} opt_proto An optional prototype for the test case class.
 * @param {Object} opt_type Either DEFAULT_TYPE or ASYNC_TYPE.
 * @return {Function} Base function that represents the TestCase class.
 */
jstestdriver.TestCaseBuilder.prototype.ConditionalTestCase =
    function(testCaseName, condition, opt_proto, opt_type) {
  if (condition()) {
    return this.TestCase(testCaseName, opt_proto, opt_type);
  }
  this.testCaseManager_.add(
      new jstestdriver.TestCaseInfo(
          testCaseName,
          jstestdriver.TestCaseBuilder.PlaceHolderCase,
          opt_type));
  return function(){};
};


/**
 * An AsyncTestCase that will only be executed when a certain condition is true.
 * @param {String} The name of the AsyncTestCase.
 * @param {function():boolean} A function that indicates if this case should be
 *     run.
 * @param {Object} opt_proto An optional prototype for the test case class.
 * @return {Function} Base function that represents the TestCase class.
 */
jstestdriver.TestCaseBuilder.prototype.ConditionalAsyncTestCase =
    function(testCaseName, condition, opt_proto) {
  return this.ConditionalTestCase(
      testCaseName, condition, opt_proto, jstestdriver.TestCaseInfo.ASYNC_TYPE);
};


/**
 * Constructs a place holder test case.
 * @constructor
 */
jstestdriver.TestCaseBuilder.PlaceHolderCase = function() {};


/**
 * Ensures there is at least one test to demonstrate a correct exclusion.
 */
jstestdriver.TestCaseBuilder.PlaceHolderCase.prototype.testExcludedByCondition =
      jstestdriver.EMPTY_FUNC;

