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



/**
 * Allows the browser to stop the test execution thread after a test when the
 * interval requires it to.
 * @param setTimeout {function(Function, number):null}
 * @param interval {number}
 * @return {function(Function):null}
 */
jstestdriver.testBreather = function(setTimeout, interval) {
  var lastBreath = new Date();
  function maybeBreathe(callback) {
    var now = new Date();
    if ((now - lastBreath) > interval) {
      setTimeout(callback, 1);
      lastBreath = now;
    } else {
      callback();
    }
  };
  return maybeBreathe;
};


jstestdriver.TIMEOUT = 500;



// TODO(corysmith): Extract the network streaming logic from the Executor logic.
/**
 * @param {jstestdriver.StreamingService} streamingService The service for
 *     streaming {@link jstestdriver.Reponse}s to the server.
 * @param {jstestdriver.TestCaseManager} testCaseManager Used to access the TestCaseInfo's for running.
 * @param {jstestdriver.TestRunner} testRunner Runs the tests...
 * @param {jstestdriver.PluginRegistrar} pluginRegistrar The plugin service,
 *     for post processing test results.
 */
jstestdriver.CommandExecutor = function(streamingService,
                                        testCaseManager,
                                        testRunner,
                                        pluginRegistrar,
                                        now,
                                        getBrowserInfo) {
  this.streamingService_ = streamingService;
  this.__testCaseManager = testCaseManager;
  this.__testRunner = testRunner;
  this.__pluginRegistrar = pluginRegistrar;
  this.__boundExecuteCommand = jstestdriver.bind(this, this.executeCommand);
  this.__boundExecute = jstestdriver.bind(this, this.execute);
  this.__boundEvaluateCommand = jstestdriver.bind(this, this.evaluateCommand);
  this.boundCleanTestManager = jstestdriver.bind(this, this.cleanTestManager);
  this.boundOnFileLoaded_ = jstestdriver.bind(this, this.onFileLoaded);
  this.boundOnFileLoadedRunnerMode_ = jstestdriver.bind(this, this.onFileLoadedRunnerMode);
  this.boundOnTestDone = jstestdriver.bind(this, this.onTestDone_);
  this.boundOnComplete = jstestdriver.bind(this, this.onComplete_);
  this.boundOnTestDoneRunnerMode = jstestdriver.bind(this, this.onTestDoneRunnerMode_);
  this.boundOnCompleteRunnerMode = jstestdriver.bind(this, this.onCompleteRunnerMode_);
  this.boundSendTestResults = jstestdriver.bind(this, this.sendTestResults);
  this.commandMap_ = {};
  this.testsDone_ = [];
  this.debug_ = false;
  this.now_ = now;
  this.lastTestResultsSent_ = 0;
  this.getBrowserInfo = getBrowserInfo;
};


/**
 * Executes a command form the server.
 * @param jsonCommand {String}
 */
jstestdriver.CommandExecutor.prototype.executeCommand = function(jsonCommand) {
  if (jsonCommand == 'noop') {
    this.streamingService_.close(null, this.__boundExecuteCommand);
  } else {
    var command = jsonParse(jsonCommand);
    this.commandMap_[command.command](command.parameters);
  }
};


jstestdriver.CommandExecutor.prototype.execute = function(cmd) {
  var response = new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.COMMAND_RESULT,
          JSON.stringify(this.__boundEvaluateCommand(cmd)),
          this.getBrowserInfo());

  this.streamingService_.close(response, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.evaluateCommand = function(cmd) {
  var res = '';

  try {
    var evaluatedCmd = eval('(' + cmd + ')');

    if (evaluatedCmd) {
      res = evaluatedCmd.toString();
    }
  } catch (e) {
    res = 'Exception ' + e.name + ': ' + e.message +
          '\n' + e.fileName + '(' + e.lineNumber +
          '):\n' + e.stack;
  }
  return res;
};


jstestdriver.CommandExecutor.prototype.reset = function() {
  if (window.location.href.search('\\?refresh') != -1) {
    window.location.reload();
  } else {
    window.location.replace(window.location.href + '?refresh');
  }
};


jstestdriver.CommandExecutor.prototype.registerCommand =
    function(name, context, func) {
  this.commandMap_[name] = jstestdriver.bind(context, func);
};


jstestdriver.CommandExecutor.prototype.dryRun = function() {
  var response =
      new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_QUERY_RESULT,
          JSON.stringify(this.__testCaseManager.getCurrentlyLoadedTest()),
          this.getBrowserInfo());
  
  this.streamingService_.close(response, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.dryRunFor = function(args) {
  var expressions = jsonParse('{"expressions":' + args[0] + '}').expressions;
  var tests = JSON.stringify(
      this.__testCaseManager.getCurrentlyLoadedTestFor(expressions))
  var response = new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.TEST_QUERY_RESULT,
          tests,
          this.getBrowserInfo());
  this.streamingService_.close(response, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.listen = function() {
  var response;
  if (window.location.href.search('\\?refresh') != -1) {
    response =
        new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.RESET_RESULT,
                                  'Runner reset.',
                                  this.getBrowserInfo(),
                                  true);
  } else {
    response =
        new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.BROWSER_READY,
                                  null,
                                  this.getBrowserInfo(),
                                  true);

  }
  this.streamingService_.close(response, this.__boundExecuteCommand);
};
