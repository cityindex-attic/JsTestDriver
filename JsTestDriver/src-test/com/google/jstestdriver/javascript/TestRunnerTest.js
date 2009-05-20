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
var testRunnerTest = jstestdriver.testCaseManager.TestCase('testRunnerTest');

testRunnerTest.prototype.testRunOneTest = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');
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
  testRunner.runTest('test', testCaseClass, 'testFoo');
  assertTrue(setUpCalled);
  assertTrue(testFooCalled);
  assertTrue(tearDownCalled);
  assertFalse(testBarCalled);
};


testRunnerTest.prototype.testCheckResultObject = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  testCaseClass.prototype.testBar = function() {};
  var result = testRunner.runTest('test', testCaseClass, 'testFoo');

  assertEquals('testFoo', result.testName);
  assertEquals('passed', result.result);
  assertEquals(0, result.time);
};


testRunnerTest.prototype.testClearBody = function() {
  var clearBodyCalled = false;
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() { clearBodyCalled = true; }, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  var result = testRunner.runTest('test', testCaseClass, 'testFoo');

  assertTrue(clearBodyCalled);
};


testRunnerTest.prototype.testWrongTest = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  var result = testRunner.runTest('test', testCaseClass, 'testBar');

  assertEquals('testBar not found in test', jsonParse(result.message).message);
};


testRunnerTest.prototype.testWrongTestCase = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.testFoo = function() {};
  var result = testRunner.runTest('test', {}, 'testFoo');

  assertEquals('test is not a test case', result.message);
  result = testRunner.runTest('test', null, 'testFoo');

  assertEquals('test is not a test case', result.message);
};


testRunnerTest.prototype.testWrongTestCase = function() {
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
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.setUp = function() {
    setUpCallOrder = callOrder++;
  };
  testCaseClass.prototype.tearDown = function() {
    tearDownCallOrder = callOrder++;
  };
  testCaseClass.prototype.testFoo = function() {
    testFooCallOrder = callOrder++;
  };
  var result = testRunner.runTest('test', testCaseClass, 'testFoo');

  assertEquals(2, dateCalls.length);
  assertEquals(0, dateCalls[0]);
  assertEquals(1, setUpCallOrder);
  assertEquals(2, testFooCallOrder);
  assertEquals(3, tearDownCallOrder);
  assertEquals(4, dateCalls[1]);
};


testRunnerTest.prototype.testSetUpError = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.setUp = function() {
    throw new Error('a big setUp error');
  };
  testCaseClass.prototype.tearDown = function() {};
  testCaseClass.prototype.testFoo = function() {};
  var result = testRunner.runTest('test', testCaseClass, 'testFoo');

  assertEquals("error", result.result);
  assertEquals("a big setUp error", jsonParse(result.message).message);
};


testRunnerTest.prototype.testTearDownError = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testRunner = new jstestdriver.TestRunner(function() {}, fakeDate);
  var testCaseManager = new jstestdriver.TestCaseManager(testRunner);
  var testCaseClass = testCaseManager.TestCase('test');

  testCaseClass.prototype.setUp = function() {};
  testCaseClass.prototype.tearDown = function() {
    throw new Error('a big tearDown error');
  };
  testCaseClass.prototype.testFoo = function() {};
  var result = testRunner.runTest('test', testCaseClass, 'testFoo');

  assertEquals("error", result.result);
  assertEquals("a big tearDown error", jsonParse(result.message).message);
};
