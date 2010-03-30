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
var TestCaseManagerTest = jstestdriver.testCaseManager.TestCase('TestCaseManagerTest');


TestCaseManagerTest.prototype.testAddTestCaseInfo = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseInfo = new jstestdriver.TestCaseInfo('name', function() {});

  testCaseManager.add(testCaseInfo);
  var testCasesInfo = testCaseManager.getTestCasesInfo();

  assertEquals(1, testCasesInfo.length);
  var testCaseInfoFromTestCaseManager = testCasesInfo[0];

  assertNotNull(testCaseInfoFromTestCaseManager);
  assertEquals(testCaseInfo, testCaseInfoFromTestCaseManager);
};


TestCaseManagerTest.prototype.testAddAndReplace = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseInfo1 = new jstestdriver.TestCaseInfo('testCase1', function() {});
  var testCaseInfo2 = new jstestdriver.TestCaseInfo('testCase2', function() {});
  var testCaseInfo3 = new jstestdriver.TestCaseInfo('testCase3', function() {});
  var testCaseInfo2Replacement = new jstestdriver.TestCaseInfo('testCase2', function() {});

  testCaseManager.add(testCaseInfo1);
  testCaseManager.add(testCaseInfo2);
  testCaseManager.add(testCaseInfo3);
  testCaseManager.add(testCaseInfo2Replacement);
  var testCasesInfo = testCaseManager.getTestCasesInfo();

  assertEquals(3, testCasesInfo.length);
  var testCaseInfoFromTestCaseManager = testCasesInfo[1];

  assertNotNull(testCaseInfoFromTestCaseManager);
  assertNotSame(testCaseInfo1, testCaseInfoFromTestCaseManager);
  assertNotSame(testCaseInfo3, testCaseInfoFromTestCaseManager);
  assertNotSame(testCaseInfo2, testCaseInfoFromTestCaseManager);
  assertSame(testCaseInfo2Replacement, testCaseInfoFromTestCaseManager);
};


TestCaseManagerTest.prototype.testDefaultRunConfigProperlyGenerated = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');
  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  var testCase2 = testCaseBuilder.TestCase('testCase2');
  testCase2.prototype.testFu = function() {};

  var testRunsConfiguration = testCaseManager.getDefaultTestRunsConfiguration();

  assertEquals(2, testRunsConfiguration.length);
  var testCase1RunConfig = testRunsConfiguration[0];
  var testCase2RunConfig = testRunsConfiguration[1];

  assertSame(testCase1, testCase1RunConfig.getTestCaseInfo().getTemplate());
  assertEquals(2, testCase1RunConfig.getTests().length);
  assertSame(testCase2, testCase2RunConfig.getTestCaseInfo().getTemplate());
  assertEquals(1, testCase2RunConfig.getTests().length);
};


TestCaseManagerTest.prototype.testCurrentlyLoadedTestInfoAreCorrect = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');

  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  var testCase2 = testCaseBuilder.TestCase('testCase2');

  testCase2.prototype.testFu = function() {};
  var currentlyLoadedTest = testCaseManager.getCurrentlyLoadedTest();
  var testNames = currentlyLoadedTest.testNames;

  assertEquals(3, currentlyLoadedTest.numTests);
  assertEquals(3, testNames.length);
  assertEquals('testCase1.testFoo', testNames[0]);
  assertEquals('testCase1.testBar', testNames[1]);
  assertEquals('testCase2.testFu', testNames[2]);
};


TestCaseManagerTest.prototype.testCurrentlyLoadedTestForExpressionsInfoAreCorrect = function() {
  var registrar = new jstestdriver.PluginRegistrar();
  registrar.register(
      new jstestdriver.plugins.DefaultPlugin(
          null, null, null, new jstestdriver.plugins.TestCaseManagerPlugin()));

  var testCaseManager = new jstestdriver.TestCaseManager(registrar);
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');

  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  var testCase2 = testCaseBuilder.TestCase('testCase2');

  testCase2.prototype.testFu = function() {};
  var expressions = [];

  expressions.push('testCase1.prototype.testFoo');
  expressions.push('testCase2.testFu');
  var currentlyLoadedTest = testCaseManager.getCurrentlyLoadedTestFor(expressions);
  var testNames = currentlyLoadedTest.testNames;

  assertEquals(2, currentlyLoadedTest.numTests);
  assertEquals(2, testNames.length);
  assertEquals('testCase1.testFoo', testNames[0]);
  assertEquals('testCase2.testFu', testNames[1]);
};


TestCaseManagerTest.prototype.testSameFilenameTestCaseIsReplaced = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');

  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  assertEquals(1, testCaseManager.getTestCasesInfo().length);
  testCaseManager.updateLatestTestCase("/test//home/foo/file.js");
  var testCase2 = testCaseBuilder.TestCase('testCase2');

  testCase2.prototype.testFu = function() {};
  testCaseManager.updateLatestTestCase("/test//home/foo/file.js");
  var testCaseInfos = testCaseManager.getTestCasesInfo();
  var testCaseInfo = testCaseInfos[0];

  assertEquals(1, testCaseInfos.length);
  assertEquals('testCase2', testCaseInfo.getTestCaseName());
  var testNames = testCaseInfo.getTestNames();

  assertEquals(1, testNames.length);
  assertEquals('testFu', testNames[0]);
};


TestCaseManagerTest.prototype.testTestCaseDifferentFilenameIsReplaced = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');

  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  assertEquals(1, testCaseManager.getTestCasesInfo().length);
  testCaseManager.updateLatestTestCase("/test//home/foo/file1.js");
  var testCase2 = testCaseBuilder.TestCase('testCase1');

  testCase2.prototype.testFu = function() {};
  testCaseManager.updateLatestTestCase("/test//home/foo/file2.js");
  var testCaseInfos = testCaseManager.getTestCasesInfo();
  var testCaseInfo = testCaseInfos[0];

  assertEquals(1, testCaseInfos.length);
  assertEquals('testCase1', testCaseInfo.getTestCaseName());
  var testNames = testCaseInfo.getTestNames();

  assertEquals(1, testNames.length);
  assertEquals('testFu', testNames[0]);
};


TestCaseManagerTest.prototype.testRemoveTestCaseForFilename = function() {
  var testCaseManager = new jstestdriver.TestCaseManager(null);
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');

  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  assertEquals(1, testCaseManager.getTestCasesInfo().length);
  testCaseManager.updateLatestTestCase("/test//home/foo/file1.js");
  var testCase2 = testCaseBuilder.TestCase('testCase2');

  testCase2.prototype.testFu = function() {};
  testCaseManager.updateLatestTestCase("/test//home/foo/file2.js");

  testCaseManager.removeTestCaseForFilename("/test//home/foo/file1.js");
  var testCaseInfos = testCaseManager.getTestCasesInfo();
  var testCaseInfo = testCaseInfos[0];

  assertEquals(1, testCaseInfos.length);
  assertEquals('testCase2', testCaseInfo.getTestCaseName());
  var testNames = testCaseInfo.getTestNames();

  assertEquals(1, testNames.length);
  assertEquals('testFu', testNames[0]);
};
