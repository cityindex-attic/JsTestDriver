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
var TestRunnerPluginTest = jstestdriver.testCaseManager.TestCase('TestRunnerPluginTest');


TestRunnerPluginTest.prototype.testRunOneTest = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');
  var testFooCalled = false;
  var setUpCalled = false;
  var tearDownCalled = false;
  var testBarCalled = false;

  testCaseClass.prototype.setUp = function() {
    setUpCalled = true;
  };
  testCaseClass.prototype.tearDown = function() {
    tearDownCalled = true;
  };
  testCaseClass.prototype.testFoo = function() {
    testFooCalled = true;
  };
  testCaseClass.prototype.testBar = function() {
    testBarCalled = true;
  };
  var onTestDoneCalled = 0;

  var onTestDone = function() {
    onTestDoneCalled++;
  };
  var onTestRunConfigurationCompleteCalled = false;
  var onTestRunConfigurationComplete = function() {
    onTestRunConfigurationCompleteCalled = true;
  };
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
      testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone,
      onTestRunConfigurationComplete);
  assertEquals(1, onTestDoneCalled);
  assertTrue(onTestRunConfigurationCompleteCalled);
  assertTrue(setUpCalled);
  assertTrue(testFooCalled);
  assertTrue(tearDownCalled);
  assertFalse(testBarCalled);
};


TestRunnerPluginTest.prototype.testCheckResultObject = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  testCaseClass.prototype.testBar = function() {};
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result = null;
  var onTestDone = function(res) {
    result = res;
  };

  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});
  assertNotNull(result);
  assertEquals('test', result.testCaseName);
  assertEquals('testFoo', result.testName);
  assertEquals('passed', result.result);
  assertEquals(0, result.time);
};


TestRunnerPluginTest.prototype.testClearBody = function() {
  var clearBodyCalled = false;
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate,
      function() { clearBodyCalled = true; });
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));

  testRunnerPlugin.runTestConfiguration(testRunConfiguration, function() {}, function() {});
  assertTrue(clearBodyCalled);
};


TestRunnerPluginTest.prototype.testClearBodyError = function() {
  var clearBodyCalled = false;
  var fakeDate = function() {};
  
  fakeDate.prototype.getTime = function() {
    return 0;
  };
  
  var clearBodyError = new Error("body not there");
  
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate,
          function() { throw clearBodyError; });
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');
  
  testCaseClass.prototype.testFoo = function() {};
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  
  var result = null;
  function captureResult(actualResult) {
    result = actualResult;
  }

  testRunnerPlugin.runTestConfiguration(testRunConfiguration, captureResult, function() {});

  assertNotNull(result);
  assertEquals("error", result.result);
  assertEquals(testRunnerPlugin.serializeError([clearBodyError]), result.message);
};


TestRunnerPluginTest.prototype.testWrongTest = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testBar'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});

  assertEquals('testBar not found in test', jsonParse(result.message)[0].message);
};


TestRunnerPluginTest.prototype.testWrongTestCase = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  var testRunConfigurationEmptyTemplate = new jstestdriver.TestRunConfiguration(
          new jstestdriver.TestCaseInfo('test', {}), new Array('testFoo'));
  var testRunConfigurationNullTemplate = new jstestdriver.TestRunConfiguration(
          new jstestdriver.TestCaseInfo('test', null), new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfigurationEmptyTemplate, onTestDone,
      function() {});

  assertEquals('test is not a test case', result.message);
  testRunnerPlugin.runTestConfiguration(testRunConfigurationNullTemplate, onTestDone,
      function() {});

  assertEquals('test is not a test case', result.message);
};


TestRunnerPluginTest.prototype.testWrongTestCase = function() {
  var callOrder = 0;
  var setUpCallOrder;
  var tearDownCallOrder;
  var testFooCallOrder;
  var dateCalls = [];
  var fakeDate = function() { };

  fakeDate.prototype.getTime = function() {
    dateCalls.push(callOrder++);
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.setUp = function() {
    setUpCallOrder = callOrder++;
  };
  testCaseClass.prototype.tearDown = function() {
    tearDownCallOrder = callOrder++;
  };
  testCaseClass.prototype.testFoo = function() {
    testFooCallOrder = callOrder++;
  };
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});

  assertEquals(2, dateCalls.length);
  assertEquals(0, dateCalls[0]);
  assertEquals(1, setUpCallOrder);
  assertEquals(2, testFooCallOrder);
  assertEquals(3, tearDownCallOrder);
  assertEquals(4, dateCalls[1]);
};


TestRunnerPluginTest.prototype.testSetUpError = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.setUp = function() {
    throw new Error('a big setUp error');
  };
  testCaseClass.prototype.tearDown = function() {};
  testCaseClass.prototype.testFoo = function() {};
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});

  assertEquals("error", result.result);
  assertEquals("a big setUp error", jsonParse(result.message)[0].message);
};


TestRunnerPluginTest.prototype.testTearDownError = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype.setUp = function() {};
  testCaseClass.prototype.tearDown = function() {
    throw new Error('a big tearDown error');
  };
  testCaseClass.prototype.testFoo = function() {};
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});

  assertEquals("error", result.result);
  assertEquals("a big tearDown error", jsonParse(result.message)[0].message);
};


TestRunnerPluginTest.prototype.testTearDownCalledWhenTestFails = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');
  var tearDownCalled = false;

  testCaseClass.prototype.setUp = function() {};
  testCaseClass.prototype.tearDown = function() {
    tearDownCalled = true;
  };
  testCaseClass.prototype.testFoo = function() {
    var err = new Error('test error');

    err.name = 'AssertError';
    throw err;
  };
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});

  assertEquals("failed", result.result);
  assertTrue(tearDownCalled);
};


TestRunnerPluginTest.prototype.testErrorInTearDownAndTest = function() {
  var fakeDate = function() {};
  
  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');
  var tearDownCalled = false;
  
  testCaseClass.prototype.setUp = function() {};
  testCaseClass.prototype.tearDown = function() {
    throw new Error('Whoops!');
  };
  testCaseClass.prototype.testFoo = function() {
    var err = new Error('test error');
    err.name = 'AssertError';
    throw err;
  };
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
    testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});
  
  assertEquals("failed", result.result);
  assertEquals(2, JSON.parse(result.message).length);
};


TestRunnerPluginTest.prototype.testSetUpAndTearDownNotCalledWhenNotPresent = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunnerPlugin = new jstestdriver.plugins.TestRunnerPlugin(fakeDate, function() {});
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCaseBuilder = new jstestdriver.TestCaseBuilder(testCaseManager);
  var testCaseClass = testCaseBuilder.TestCase('test');

  testCaseClass.prototype = {
      testFoo: function() {
      }
  };
  var testRunConfiguration = new jstestdriver.TestRunConfiguration(
          testCaseManager.getTestCasesInfo()[0], new Array('testFoo'));
  var result;
  var onTestDone = function(res) {
    result = res;
  };
  testRunnerPlugin.runTestConfiguration(testRunConfiguration, onTestDone, function() {});

  assertEquals("passed", result.result);
};
