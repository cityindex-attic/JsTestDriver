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
var TestCaseBuilderTest = TestCase('TestCaseBuilderTest');


TestCaseBuilderTest.prototype.testCreatedTestCasesAddedToTestCaseManager =
    function() {
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase = testCaseBuilder.TestCase('testCase');
  var testCaseInfos = testCaseManager.getTestCasesInfo();

  assertEquals(1, testCaseInfos.length);
  var testCaseInfo = testCaseInfos[0];

  assertEquals(jstestdriver.TestCaseInfo.DEFAULT_TYPE, testCaseInfo.getType());
  assertEquals('testCase', testCaseInfo.getTestCaseName())
  assertSame(testCase, testCaseInfo.getTemplate());
};


TestCaseBuilderTest.prototype.testAsyncTestCasesAddedToManager = function() {
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase = testCaseBuilder.AsyncTestCase('asyncTestCase');
  var testCaseInfos = testCaseManager.getTestCasesInfo();

  assertEquals(1, testCaseInfos.length);
  var testCaseInfo = testCaseInfos[0];

  assertEquals(jstestdriver.TestCaseInfo.ASYNC_TYPE, testCaseInfo.getType());
  assertEquals('asyncTestCase', testCaseInfo.getTestCaseName());
  assertEquals(testCase, testCaseInfo.getTemplate());
};


TestCaseBuilderTest.prototype.testConditionalTestCasesAddedToManager =
    function() {
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var trueTestCase =
      testCaseBuilder.ConditionalTestCase('trueTestCase', function() {
        return true
      });
  var falseTestCase =
      testCaseBuilder.ConditionalTestCase('falseTestCase', function() {
        return false;
      });
  var testCaseInfos = testCaseManager.getTestCasesInfo();

  assertEquals(2, testCaseInfos.length);
  var trueTestInfo = testCaseInfos[0];
  var falseTestInfo = testCaseInfos[1];

  assertEquals(jstestdriver.TestCaseInfo.DEFAULT_TYPE, trueTestInfo.getType());
  assertEquals('trueTestCase', trueTestInfo.getTestCaseName());
  assertEquals(trueTestCase, trueTestInfo.getTemplate());

  assertEquals(jstestdriver.TestCaseInfo.DEFAULT_TYPE, falseTestInfo.getType());
  assertEquals('falseTestCase', falseTestInfo.getTestCaseName());
  assertEquals(
      jstestdriver.TestCaseBuilder.PlaceHolderCase, falseTestInfo.getTemplate());
};


TestCaseBuilderTest.prototype.testConditionalAsyncTestCasesAddedToManager =
    function() {
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var trueTestCase =
      testCaseBuilder.ConditionalAsyncTestCase('trueTestCase', function() {
        return true
      });
  var falseTestCase =
      testCaseBuilder.ConditionalAsyncTestCase('falseTestCase', function() {
        return false;
      });
  var testCaseInfos = testCaseManager.getTestCasesInfo();

  assertEquals(2, testCaseInfos.length);
  var trueTestInfo = testCaseInfos[0];
  var falseTestInfo = testCaseInfos[1];

  assertEquals(jstestdriver.TestCaseInfo.ASYNC_TYPE, trueTestInfo.getType());
  assertEquals('trueTestCase', trueTestInfo.getTestCaseName());
  assertEquals(trueTestCase, trueTestInfo.getTemplate());

  assertEquals(jstestdriver.TestCaseInfo.ASYNC_TYPE, falseTestInfo.getType());
  assertEquals('falseTestCase', falseTestInfo.getTestCaseName());
  assertEquals(
      jstestdriver.TestCaseBuilder.PlaceHolderCase, falseTestInfo.getTemplate());
};
