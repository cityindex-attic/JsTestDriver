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

jstestdriver.NOOP_COMMAND = {
  command : 'noop',
  parameters : []
};

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
                                        getBrowserInfo,
                                        currentActionSignal) {
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
  this.currentActionSignal_ = currentActionSignal;
};


/**
 * Executes a command form the server.
 * @param jsonCommand {String}
 */
jstestdriver.CommandExecutor.prototype.executeCommand = function(jsonCommand) {
  var command;
  if (jsonCommand && jsonCommand.length) { //handling some odd IE errors.
    command = jsonParse(jsonCommand);
  } else {
    command = jstestdriver.NOOP_COMMAND;
  }
  try {
    this.commandMap_[command.command](command.parameters);
  } catch (e) {
    var response = new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.LOG,
    var message =  'Exception ' + e.name + ': ' + e.message +
        '\n' + e.fileName + '(' + e.lineNumber +
        '):\n' + e.stack;
    jstestdriver.JSON.stringify({
        'source' : 'jstestdriver.CommandExecutor',
        'level' : 1000,
        'message' : message}),
    this.getBrowserInfo());
    if (top.console && top.console.log) {
      top.console.log(message);
    }
    this.streamingService_.close(response, this.__boundExecuteCommand);
    // Propagate the exception.
    throw e;
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


/**
 * Registers a command to the executor to handle incoming command requests.
 * @param {String} name The name of the command
 * @param {Object} context The context to call the command in.
 * @param {function(Array):null} func the command.
 */
jstestdriver.CommandExecutor.prototype.registerCommand =
    function(name, context, func) {
  this.commandMap_[name] = jstestdriver.bind(context, func);
};


/**
 * Registers a command to the executor to handle incoming command requests
 * @param {String} name The name of the command
 * @param {Object} context The context to call the command in.
 * @param {function(Array):null} func the command.
 */
jstestdriver.CommandExecutor.prototype.registerTracedCommand =
    function(name, context, func) {
  var bound = jstestdriver.bind(context, func);
  var signal = this.currentActionSignal_;
  this.commandMap_[name] = function() {
    signal.set(name);
    return bound.apply(null, arguments);
  };
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
