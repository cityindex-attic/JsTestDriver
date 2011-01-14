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
var TestRunnerTest = jstestdriver.testCaseManager.TestCase('TestRunnerTest');


TestRunnerTest.prototype.testRunTests = function() {
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
  var testRunsConfigurationThatRan = [];
  pluginRegistrar.register({
    name : "testPlugin",
    runTestConfiguration: function(testRunConfig, onTestDone, onTestRunConfigurationComplete) {
      testRunsConfigurationThatRan.push(testRunConfig);
      onTestRunConfigurationComplete();
      return true;
    }
  });
  var testRunner = new jstestdriver.TestRunner(pluginRegistrar);
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCase1 = testCaseBuilder.TestCase('testCase1');
  var testCase2 = testCaseBuilder.TestCase('testCase2');
  var testCase3 = testCaseBuilder.TestCase('testCase3');
  var testCase4 = testCaseBuilder.TestCase('testCase4');

  testCase1.prototype.setUp = function() {};
  testCase1.prototype.tearDown = function() {};
  testCase1.prototype.testFoo = function() {};
  testCase1.prototype.testBar = function() {};
  testCase2.prototype.testMooh = function() {};
  testCase3.prototype.testSpiderman = function() {};
  testCase3.prototype.testSuperman = function() {};
  testCase4.prototype.testPacman = function() {};
  testCase4.prototype.testMissPacman = function() {};
  var onCompleteCalled = false;
  var onComplete = function() {
    onCompleteCalled = true;
  };

  testRunner.runTests(testCaseManager.getDefaultTestRunsConfiguration(),
      function() {}, onComplete, false);
  assertEquals(4, testRunsConfigurationThatRan.length);
  assertTrue(onCompleteCalled);
};


TestRunnerTest.prototype.testTestCaseMap = function() {
  var map = new jstestdriver.TestRunner.TestCaseMap();
  map.startCase('foo');
  assertTrue(map.hasActiveCases());
  map.stopCase('foo');
  assertFalse(map.hasActiveCases());
};
