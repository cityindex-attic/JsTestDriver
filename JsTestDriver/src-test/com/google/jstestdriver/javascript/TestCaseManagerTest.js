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
var testCaseManagerTest = jstestdriver.testCaseManager.TestCase('testCaseManagerTest');


testCaseManagerTest.prototype.testAddTestCaseToTestCaseManager = function() {
  var testCaseManager = new jstestdriver.TestCaseManager();
  var testCase = testCaseManager.TestCase('test');

  testCase.prototype.testFoo = function() {};
  assertEquals(1, testCaseManager.getTestCases().length);
};


testCaseManagerTest.prototype.testRunAllTestsOneTestCase = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var testCase = testCaseManager.TestCase('test');
  var testFooCalled = false;
  var testBarCalled = false;

  testCase.prototype.testFoo = function() {
    testFooCalled = true
  };
  testCase.prototype.testBar = function() {
    testBarCalled = true;
  };
  var testCaseWrapper = { name: 'test', tests: [ 'testFoo', 'testBar' ] };
  testCaseManager.runTests(new Array(testCaseWrapper), function() {}, function() {});
  assertTrue(testFooCalled);
  assertTrue(testBarCalled);
};


testCaseManagerTest.prototype.testRunAllTestsMultipleTestCases = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var testCase1 = testCaseManager.TestCase('test1');
  var testCase2 = testCaseManager.TestCase('test2');
  var testFooCalled = false;
  var testBarCalled = false;
  var testFuCalled = false;
  var testBaarCalled = false;

  testCase1.prototype.testFoo = function() {
    testFooCalled = true
  };
  testCase1.prototype.testBar = function() {
    testBarCalled = true;
  };
  testCase2.prototype.testFu = function() {
    testFuCalled = true
  };
  testCase2.prototype.testBaar = function() {
    testBaarCalled = true;
  };
  var testCaseWrapper1 = { name: 'test1', tests: [ 'testFoo', 'testBar' ] };
  var testCaseWrapper2 = { name: 'test2', tests: [ 'testFu', 'testBaar' ] };
  testCaseManager.runTests(new Array(testCaseWrapper1, testCaseWrapper2), function() {},
      function() {});
  assertTrue(testFooCalled);
  assertTrue(testBarCalled);
  assertTrue(testFuCalled);
  assertTrue(testBaarCalled);
};


testCaseManagerTest.prototype.testRunOneTest = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var testCase = testCaseManager.TestCase('test');
  var testFooCalled = false;
  var testBarCalled = false;

  testCase.prototype.testFoo = function() {
    testFooCalled = true
  };
  testCase.prototype.testBar = function() {
    testBarCalled = true;
  };
  var testCaseWrapper = { name: 'test', tests: [ 'testFoo' ] };
  testCaseManager.runTests(new Array(testCaseWrapper), function() {}, function() {});
  assertTrue(testFooCalled);
  assertFalse(testBarCalled);
};


testCaseManagerTest.prototype.testRemoveTestCaseFromListIfSameName = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));

  var testCase = testCaseManager.TestCase('test');
  var fooCalled = false;
  var barCalled = false;

  testCase.prototype.testFoo = function() { fooCalled = true; };
  testCase = testCaseManager.TestCase('test');

  testCase.prototype.testBar = function() { barCalled = true; };
  var testCaseWrapper = { name: 'test', tests: [ 'testBar', 'testFoo' ] };
  testCaseManager.runTests(new Array(testCaseWrapper), function() {}, function() {});
  assertFalse(fooCalled);
  assertTrue(barCalled);
};


testCaseManagerTest.prototype.testUseTestCaseAlias = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var fooCalled = false;
  var testCase = TestCase('testCase');

  testCase.prototype.testFoo = function() { fooCalled = true; };
  testCaseManager.runTests(new Array({name: 'testCase', tests: ['testFoo'] }), function() {},
      function() {});
  assertTrue(fooCalled);
};


testCaseManagerTest.prototype.testPassObjectAsPrototypeOnTestCaseInit = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var fooCalled = false;
  var barCalled = false;
  var testCase = TestCase('testCase', {
    testFoo: function() {
      fooCalled = true;
    },
    testBar: function() {
      barCalled = true;
    }
  });
  testCaseManager.runTests(new Array({name: 'testCase', tests: ['testFoo', 'testBar'] }),
      function() {}, function() {});
  assertTrue(fooCalled);
  assertTrue(barCalled);
};


testCaseManagerTest.prototype.testPassObjectAsPrototypeOnTestCaseInitWithSetUpAndTearDown =
  function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var setUpCalled = false;
  var tearDownCalled = false;
  var fooCalled = false;
  var barCalled = false;
  var testCase = TestCase('testCase', {
    setUp: function() {
      setUpCalled = true;
    },
    tearDown: function() {
      tearDownCalled = true;
    },
    testFoo: function() {
      fooCalled = true;
    },
    testBar: function() {
      barCalled = true;
    }
  });
  testCaseManager.runTests(new Array({name: 'testCase', tests: ['testFoo', 'testBar'] }),
      function() {}, function() {});
  assertTrue(setUpCalled);
  assertTrue(fooCalled);
  assertTrue(barCalled);
  assertTrue(tearDownCalled);
};


testCaseManagerTest.prototype.testDryRunReturnsNumberOfTestsAndTestNames = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0;
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner(function() {},
      fakeDate));
  var testCase1 = TestCase('testCase1', {
    testFoo: function() {
    },
    testBar: function() {
    }
  });
  var testCase2 = TestCase('testCase2', {
    testFu: function() {
    },
    testBaz: function() {
    }
  });
  var dryRunInfo = testCaseManager.dryRun();

  assertEquals(4, dryRunInfo.numTests);
  var testNames = dryRunInfo.testNames;

  assertEquals(4, testNames.length);
  assertEquals('testCase1.testFoo', testNames[0]);
  assertEquals('testCase1.testBar', testNames[1]);
  assertEquals('testCase2.testFu', testNames[2]);
  assertEquals('testCase2.testBaz', testNames[3]);
};
