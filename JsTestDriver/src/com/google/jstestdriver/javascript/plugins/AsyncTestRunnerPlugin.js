/*
 * Copyright 2010 Google Inc.
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
 * @fileoverview Defines the AsyncTestRunnerPlugin class, which executes
 * asynchronous test cases within JsTestDriver.
 *
 *      +----------------------------- more tests? ------------ nextTest() <--------------+
 *     |                                                                                 |
 *     v                                                                                 |
 * startSetUp() ---- execute ---> finishSetUp(errors)                                    |
 *                                     |                                                 |
 * startTestMethod() <--- no errors ---+---- errors ----+                                |
 *        |                                             |                                |
 *     execute                                          |                                |
 *        |                                             |                                |
 *        v                                             v                                |
 * finishTestMethod(errors) -- errors or no errors -> startTearDown() -- execute -> finishTearDown(errors)
 *
 * @author rdionne@google.com (Robert Dionne)
 */


/**
 * Constructs an AsyncTestRunnerPlugin.
 *
 * @param dateObj the date object constructor
 * @param clearBody a function to call to clear the document body.
 * @param opt_setTimeout window.setTimeout replacement.
 * @param opt_queueConstructor a constructor for obtaining new DeferredQueues.
 * @param opt_armorConstructor a constructor for obtaining new DeferredQueueArmors.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin = function(
    dateObj, clearBody, opt_setTimeout, opt_queueConstructor, opt_armorConstructor) {
  this.name = "AsyncTestRunnerPlugin";
  this.dateObj_ = dateObj;
  this.clearBody_ = clearBody;
  this.setTimeout_ = opt_setTimeout || jstestdriver.setTimeout;
  this.queueConstructor_ = opt_queueConstructor || jstestdriver.plugins.async.DeferredQueue;
  this.armorConstructor_ = opt_armorConstructor || jstestdriver.plugins.async.DeferredQueueArmor;
  this.testRunConfiguration_ = null;
  this.testCaseInfo_ = null;
  this.onTestDone_ = null;
  this.onTestRunConfigurationComplete_ = null;
  this.testIndex_ = 0;
  this.testCase_ = null;
  this.testName_ = null;
  this.start_ = null;
  this.errors_ = null;
};

/**
 * Runs a test case.
 *
 * @param testRunConfiguration the test case configuration
 * @param onTestDone the function to call to report a test is complete
 * @param onTestRunConfigurationComplete the function to call to report a test
 * case is complete 
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.runTestConfiguration = function(
    testRunConfiguration, onTestDone, onTestRunConfigurationComplete) {
    //console.log('runTestConfiguration');
  if (testRunConfiguration.getTestCaseInfo().getType() == jstestdriver.TestCaseInfo.ASYNC_TYPE) {
    this.testRunConfiguration_ = testRunConfiguration;
    this.testCaseInfo_ = testRunConfiguration.getTestCaseInfo();
    this.onTestDone_ = onTestDone;
    this.onTestRunConfigurationComplete_ = onTestRunConfigurationComplete;
    this.testIndex_ = 0;
    this.nextTest();
    return true;
  }

  return false;
};

/**
 * Runs the next test in the current test case.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.nextTest = function() {
  //console.log('nextTest');
  this.start_ = new this.dateObj_().getTime();
  if (this.testIndex_ < this.testRunConfiguration_.getTests().length) {
    this.testCase_ = new (this.testCaseInfo_.getTemplate());
    this.testName_ = this.testRunConfiguration_.getTests()[this.testIndex_];
    this.errors_ = [];
    //console.log('next test: ' + this.testName_);
    this.startSetUp();
  } else {
    this.testRunConfiguration_ = null;
    this.testCaseInfo_ = null;
    this.onTestDone_ = null;
    this.testIndex_ = 0;
    this.testCase_ = null;
    this.testName_ = null;
    this.start_ = null;
    this.errors_ = null;
    this.onTestRunConfigurationComplete_();
    this.onTestRunConfigurationComplete_ = null;
  }
};


/**
 * Starts the next phase of the current test in the current test case. Creates a
 * DeferredQueue to manage the steps of this phase, executes the phase
 * catching any exceptions, and then hands the control over to the queue to
 * call onQueueComplete when it empties.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.execute_ = function(
    onQueueComplete, invokeMethod) {
  //console.log('start');

  // Create a new DeferredQueue that will call invokeMethod() once all steps complete.
  var q = new (this.queueConstructor_)(this.setTimeout_, this.testCase_, onQueueComplete);
  var armor = new (this.armorConstructor_)(q);

  // Attempt to invoke the method. The method will add zero or more steps
  // to the queue. If the method throws an error, add that error to the list.
  if (invokeMethod) {
    try {
      invokeMethod(armor);
    } catch (e) {
      this.errors_.push(e);
    }
  }
  
  q.startStep();
};


/**
 * Starts the setUp phase.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.startSetUp = function() {
  //console.log('startSetUp');
  var runner = this;
  this.execute_(function(errors) {
    runner.finishSetUp(errors);
  }, this.testCase_.setUp);
};

/**
 * Finishes the setUp phase and reports any errors. If there are errors it
 * initiates the tearDown phase, otherwise initiates the testMethod phase.
 *
 * @param errors errors caught during the current asynchronous phase.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.finishSetUp = function(errors) {
  //console.log('finishSetUp');
  this.errors_ = this.errors_.concat(errors);
  if (this.errors_.length) {
    this.startTearDown();
  } else {
    this.startTestMethod();
  }
};

/**
 * Starts the testMethod phase.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.startTestMethod = function() {
  //console.log('startTestMethod');
  var runner = this;
  this.execute_(function(errors) {
    runner.finishTestMethod(errors);
  }, this.testCase_[this.testName_]);
};

/**
 * Finishes the testMethod phase and reports any errors. Continues with the
 * tearDown phase.
 *
 * @param errors errors caught during the current asynchronous phase.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.finishTestMethod = function(errors) {
  //console.log('finishTestMethod');
  this.errors_ = this.errors_.concat(errors);
  this.startTearDown();
};


/**
 * Start the tearDown phase.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.startTearDown = function() {
  //console.log('startTearDown');
  var runner = this;
  this.execute_(function(errors){
    runner.finishTearDown(errors);
  }, this.testCase_.tearDown);
};


/**
 * Finishes the tearDown phase and reports any errors. Submits the test results
 * to the test runner. Continues with the next test.
 *
 * @param errors errors caught during the current asynchronous phase.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.finishTearDown = function(errors) {
  //console.log('finishTearDown');
  this.errors_ = this.errors_.concat(errors);
  this.clearBody_();
  this.onTestDone_(this.buildResult());
  this.testIndex_ += 1;
  this.nextTest();
};

/**
 * Builds a test result.
 */
jstestdriver.plugins.async.AsyncTestRunnerPlugin.prototype.buildResult = function() {
  //console.log('buildResult');
  var end = new this.dateObj_().getTime();
  var result = 'passed';
  var message = '';
  if (this.errors_.length) {
    result = 'failed';
    message = JSON.stringify(this.errors_);
  }
  return new jstestdriver.TestResult(
      this.testCaseInfo_.getTestCaseName(), this.testName_, result, message,
      jstestdriver.console.getAndResetLog(), end - this.start_);
};
