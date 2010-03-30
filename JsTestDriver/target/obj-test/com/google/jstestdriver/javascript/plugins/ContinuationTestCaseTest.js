
var ContinuationTestCase = TestCase('ContinuationTestCase');

ContinuationTestCase.prototype.setUp = function() {
  this.timeouts = [];
};


ContinuationTestCase.prototype.mockTimeOut = function(callback, interval) {
  this.timeouts.push({callback:callback, interval:interval});
};


ContinuationTestCase.prototype.tearDown = function() {
};

ContinuationTestCase.prototype.getTestRunConfig = function(testProto) {
  var info;
  var testCaseManager = {
    add : function(testCaseInfo) {
      info = testCaseInfo;
    }
  };
  
  var factory =
      new jstestdriver.plugins.asynch.ContinuationTestCaseFactory(
          testCaseManager);
  factory.create('TestCase', testProto);
  return new jstestdriver.TestRunConfiguration(info,
                                               ['testFoo']);
};


ContinuationTestCase.prototype.a_testWaitForCondition = function() {
  var log = [];
  var console = {
    getLog : function() {
      return [];
    }
  };
  
  var actualResult;
  function onTestDone(result) {
    actualResult = result;
  }
  
  function onComplete() {
    onComplete.called = true;
  }
  
  var ranContinuation = false;
  var testProto = {
      foo : false,
      testDone : false,
      testFoo : function(asynch) {
        function continuation(asynch) {
          fail();
        }

        asynch.waitForCondition(function() {
          return this.foo;
        }, continuation);
      }
    };
  
  var config = this.getTestRunConfig(testProto);
  
  var plugin =
      new jstestdriver.plugins.asynch.ContinuationTestRunnerPlugin(
          console,
          jstestdriver.bind(this, this.mockTimeOut),
          Date);
  plugin.runTestConfiguration(config, onTestDone, onComplete);
  
//  assertTrue("the test was not executed asynch.", !!this.timeouts[0]);
//  assertNotNull(actualResult);
//  assertEquals(log, actualResult.log);
//  assertTrue(onComplete.called);
//  assertEquals('failed', actualResult.result);
}

ContinuationTestCase.prototype.testPhaseExecute = function() {
  var done = false;
  var done2 = false;
  var phaseResult = null;
  function onPhaseDone(result) {
    phaseResult = result;
  }

  var phase = new jstestdriver.plugins.asynch.TestPhase(
      jstestdriver.bind(this, this.mockTimeOut),
      onPhaseDone
  );
  phase.addStep(function(onDone, start) {
    done = true;
    onDone(start);
  });
  phase.addStep(function(onDone, start) {
    done2 = true;
    onDone(start);
  });
  phase.execute(new Date());
  assertTrue(done);
  assertTrue(done2);
  assertNull(phaseResult);
};
