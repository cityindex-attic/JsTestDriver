/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the 'License'); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

jstestdriver.plugins.asynch = (function(){

  var TESTCASE_TYPE = 'continuationtestcase';

  /**
   * Creates ContinuationTestCases
   */
  function ContinuationTestCaseFactory(testCaseManager) {
    this.manager_ = testCaseManager;
  }

  ContinuationTestCaseFactory.prototype.create =
      function(testCaseName, opt_proto) {
    function TestCase(){}

    var proto = opt_proto || TestCase.prototype;

    if (!proto.setUp) {
      proto.setUp = jstestdriver.EMPTY_FUNC;
    }
    if (!proto.tearDown) {
      proto.tearDown = jstestdriver.EMPTY_FUNC;
    }
    TestCase.prototype = proto;
    this.manager_.add(new jstestdriver.TestCaseInfo(testCaseName,
                                                    TestCase,
                                                    TESTCASE_TYPE));
    return TestCase;
  };

  function TestStep(condition, continuation, opt_interval, opt_timeout) {
    this.condition_ = condition;
    this.continuation_ = continuation;
    this.interval = interval;
    this.timeout = timeout;
  }
  
  function TestPhase(timeout, onPhaseDone) {
    this.steps_ = [];
    this.onDone_ = onPhaseDone;
    this.timeout_ = timeout;
    this.boundExecute_ = jstestdriver.bind(this, this.execute);
  }
  
  TestPhase.prototype.waitForCondition = function(condition,
                                                  continuation,
                                                  opt_interval,
                                                  opt_timeout) {
    this.steps_.push(new TestStep(condition,
                                 continuation,
                                 opt_interval,
                                 opt_timeout));
  };
  
  TestPhase.prototype.execute = function(startTime) {
    if (!this.steps_.length) {
      this.onDone_();
    } else {
      this.steps_.shift()(this.boundExecute_, startTime);
    }
  };
  
  TestPhase.prototype.addStep = function(step) {
    this.steps_.push(step);
  }


  
  function ContinuationRunner(onTestDone, start, setTimeout) {
    this.phases = [];
  }
  
  ContinuationRunner.prototype
  
  ContinuationRunner.prototype.addPhase = function(phase) {
    
  }

  /**
   * @param setTimeout {function(Function, number):null}
   */
  function ContinuationTestRunnerPlugin(console, setTimeout, date) {
    this.console_ = console;
    this.setTimeout_ = setTimeout;
    this.date_ = date;
  }


  ContinuationTestRunnerPlugin.prototype.name = 'continuationtestplugin';


  /**
   * Runs a ContinuationTestCase.
   */
  ContinuationTestRunnerPlugin.prototype.runTestConfiguration =
      function(testRunConfiguration,
               onTestDone,
               onTestRunConfigurationComplete) {

    var testCaseInfo = testRunConfiguration.getTestCaseInfo();
    jstestdriver.console.debug(testCaseInfo);
    if (testCaseInfo.getType() != TESTCASE_TYPE) {
      return false;
    }
    var tests = testRunConfiguration.getTests();
    var size = tests.length;
    var testCaseName = testCaseInfo.getTestCaseName();
    var testCaseTemplate = testCaseInfo.getTemplate();

    for (var i = 0; tests[i]; i++) {
      var testName = tests[i];
      var testCase = new testCaseTemplate();
      var runner = new ContinuationRunner(onTestDone,
                                              new this.date_(),
                                              this.setTimeout_);
      testCase[testName](runner);
    }
    return true;
  };


  return {
    ContinuationTestRunnerPlugin : ContinuationTestRunnerPlugin,
    ContinuationTestCaseFactory : ContinuationTestCaseFactory,
    TESTCASE_TYPE : TESTCASE_TYPE,
    TestPhase : TestPhase
  };
})();
