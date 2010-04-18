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
jstestdriver.listen = function() {
  var id = jstestdriver.extractId(window.location.toString());
  var url = jstestdriver.SERVER_URL + id;

  jstestdriver.pluginRegistrar = new jstestdriver.PluginRegistrar();
  jstestdriver.testCaseManager =
      new jstestdriver.TestCaseManager(jstestdriver.pluginRegistrar);

  var testRunner = new jstestdriver.TestRunner(
      jstestdriver.pluginRegistrar,
      jstestdriver.testBreather(jstestdriver.setTimeout,
                                jstestdriver.TIMEOUT)
  );

  jstestdriver.testCaseBuilder =
      new jstestdriver.TestCaseBuilder(jstestdriver.testCaseManager);

  jstestdriver.global.TestCase = jstestdriver.bind(jstestdriver.testCaseBuilder,
      jstestdriver.testCaseBuilder.TestCase);

  // default plugin
  var scriptLoader = new jstestdriver.plugins.ScriptLoader(window, document,
        jstestdriver.testCaseManager);
  var stylesheetLoader =
      new jstestdriver.plugins.StylesheetLoader(window, document,
            jstestdriver.jQuery.browser.mozilla || jstestdriver.jQuery.browser.safari);
  var fileLoaderPlugin =
      new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var testRunnerPlugin =
        new jstestdriver.plugins.TestRunnerPlugin(Date, function() {
      jstestdriver.jQuery('body').children().remove();
      jstestdriver.jQuery(document).unbind();
      jstestdriver.jQuery(document).die();
  });

  jstestdriver.pluginRegistrar.register(
      new jstestdriver.plugins.DefaultPlugin(
          fileLoaderPlugin,
          testRunnerPlugin,
          new jstestdriver.plugins.AssertsPlugin(),
          new jstestdriver.plugins.TestCaseManagerPlugin()));

  // legacy
  jstestdriver.testCaseManager.TestCase = jstestdriver.global.TestCase;

  var streamingService = new jstestdriver.StreamingService(
    url,
    function() { return new Date().getTime();},
    jstestdriver.convertToJson(jstestdriver.jQuery.post));
  
  var executor = jstestdriver.executor =
      new jstestdriver.CommandExecutor(parseInt(id),
                                       streamingService,
                                       jstestdriver.testCaseManager,
                                       testRunner,
                                       jstestdriver.pluginRegistrar);

  executor.registerCommand('execute', executor, executor.execute);
  executor.registerCommand('runAllTests', executor, executor.runAllTests);
  executor.registerCommand('runTests', executor, executor.runTests);
  executor.registerCommand('loadTest', executor, executor.loadTest);
  executor.registerCommand('reset', executor, executor.reset);
  executor.registerCommand('dryRun', executor, executor.dryRun);
  executor.registerCommand('dryRunFor', executor, executor.dryRunFor);
  executor.registerCommand('streamAcknowledged',
                           streamingService,
                           streamingService.streamAcknowledged);

  executor.listen();
};


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
 * @param {Number} id The server Id for this browser.
 * @param {jstestdriver.StreamingService} streamingService The service for
 *     streaming {@link jstestdriver.Reponse}s to the server.
 * @param {jstestdriver.TestCaseManager} testCaseManager Used to access the TestCaseInfo's for running.
 * @param {jstestdriver.TestRunner} testRunner Runs the tests...
 * @param {jstestdriver.PluginRegistrar} pluginRegistrar The plugin service,
 *     for post processing test results.
 */
jstestdriver.CommandExecutor = function(id,
                                        streamingService,
                                        testCaseManager,
                                        testRunner,
                                        pluginRegistrar) {
  this.__id = id;
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
};


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


jstestdriver.CommandExecutor.prototype.findScriptTagsToRemove_ = function(dom, fileSrcs) {
  var scripts = dom.getElementsByTagName('script');
  var filesSize = fileSrcs.length;
  var scriptsSize = scripts.length;
  var scriptTagsToRemove = [];

  for (var i = 0; i < filesSize; i++) {
    var f = fileSrcs[i].fileSrc;

    for (var j = 0; j < scriptsSize; j++) {
      var s = scripts[j];

      if (s.src.indexOf(f) != -1) {
        scriptTagsToRemove.push(s);
        break;
      }
    }
  }
  return scriptTagsToRemove;
};


jstestdriver.CommandExecutor.prototype.removeScriptTags_ = function(dom,
                                                                    scriptTagsToRemove) {
  var head = dom.getElementsByTagName('head')[0];
  var size = scriptTagsToRemove.length;

  for (var i = 0; i < size; i++) {
    var script = scriptTagsToRemove[i];

    head.removeChild(script);
  }
};


jstestdriver.CommandExecutor.prototype.removeScripts = function(dom, fileSrcs) {
  this.removeScriptTags_(dom, this.findScriptTagsToRemove_(dom, fileSrcs));
};


jstestdriver.CommandExecutor.prototype.loadTest = function(args) {
  var files = args[0];
  var runnerMode = args[1] == "true" ? true : false;
  var fileSrcs = jsonParse('{"f":' + files + '}').f;

  this.removeScripts(document, fileSrcs);
  var fileLoader = new jstestdriver.FileLoader(this.__pluginRegistrar,
    !runnerMode ? this.boundOnFileLoaded_ : this.boundOnFileLoadedRunnerMode_);

  fileLoader.load(fileSrcs);
};


jstestdriver.CommandExecutor.prototype.getBrowserInfo = function() {
  return new jstestdriver.BrowserInfo(this.__id);
};


jstestdriver.CommandExecutor.prototype.onFileLoaded = function(status) {
  var response = new jstestdriver.Response(
      jstestdriver.RESPONSE_TYPES.FILE_LOAD_RESULT,
      JSON.stringify(status),
      this.getBrowserInfo());
  this.streamingService_.close(response, this.__boundExecuteCommand);
};

jstestdriver.CommandExecutor.prototype.onFileLoadedRunnerMode = function(status) {
  if (!parent.G_testRunner) {
    parent.G_testRunner = {

      finished_: false,
      success_: 1,
      report_: '',
      filesLoaded_: 0,

      isFinished: function() {
        return this.finished_;
      },

      isSuccess: function() {
        return this.success_;
      },

      getReport: function() {
        return this.report_;
      },

      getNumFilesLoaded: function() {
        return this.filesLoaded_;
      },

      setIsFinished: function(finished) {
        this.finished_ = finished;
      },

      setIsSuccess: function(success) {
        this.success_ = success;
      },

      setReport: function(report) {
        this.report_ = report;
      },

      setNumFilesLoaded: function(filesLoaded) {
        this.filesLoaded_ = filesLoaded;
      }
    };
  }
  var testRunner = parent.G_testRunner;

  testRunner.setNumFilesLoaded(status.loadedFiles.length);
  this.streamingService_.close(null, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.runAllTests = function(args) {
  var captureConsole = args[0];
  var runnerMode = args[1] == "true" ? true : false;
  this.debug_ = Boolean(args[2]);

  this.runTestCases_(this.__testCaseManager.getDefaultTestRunsConfiguration(),
      captureConsole == "true" ? true : false, runnerMode);
};


jstestdriver.CommandExecutor.prototype.runTests = function(args) {
  var expressions = jsonParse('{"expressions":' + args[0] + '}').expressions;
  var captureConsole = args[1];
  this.debug_ = Boolean(args[2]);

  this.runTestCases_(this.__testCaseManager.getTestRunsConfigurationFor(expressions),
      captureConsole == "true" ? true : false, false);
};


jstestdriver.CommandExecutor.prototype.runTestCases_ = function(testRunsConfiguration,
    captureConsole, runnerMode) {
  if (!runnerMode) {
    this.__testRunner.runTests(testRunsConfiguration,
                               this.boundOnTestDone,
                               this.boundOnComplete,
                               captureConsole);
  } else {
    this.__testRunner.runTests(testRunsConfiguration, this.boundOnTestDoneRunnerMode,
        this.boundOnCompleteRunnerMode, captureConsole);
  }
};


jstestdriver.CommandExecutor.prototype.onTestDoneRunnerMode_ = function(result) {
  var testRunner = parent.G_testRunner;

  testRunner.setIsSuccess(testRunner.isSuccess() & (result.result == 'passed'));
  this.addTestResult(result);
};


jstestdriver.CommandExecutor.prototype.onCompleteRunnerMode_ = function() {
  var testRunner = parent.G_testRunner;

  testRunner.setReport(JSON.stringify(this.testsDone_));
  this.testsDone_ = [];
  testRunner.setIsSuccess(testRunner.isSuccess() == 1 ? true : false);
  testRunner.setIsFinished(true);
};


jstestdriver.CommandExecutor.prototype.sendTestResults = function() {
  if (this.testsDone_.length > 0) {
    var response = new jstestdriver.Response(
            jstestdriver.RESPONSE_TYPES.TEST_RESULT,
            JSON.stringify(this.testsDone_),
            this.getBrowserInfo());

    this.testsDone_ = [];
    this.streamingService_.stream(response, this.__boundExecuteCommand);
  }
};


jstestdriver.CommandExecutor.prototype.onTestDone_ = function(result) {
  this.addTestResult(result);
  if ((result.result == 'error' || result.log != '' || this.debug_)) {
    this.sendTestResults();
  }
};


// TODO(corysmith): refactor the testsDone collection into a separate object.
jstestdriver.CommandExecutor.prototype.addTestResult = function(testResult) {
  this.__pluginRegistrar.processTestResult(testResult);
  this.testsDone_.push(testResult);
};


jstestdriver.CommandExecutor.prototype.sendTestResultsOnComplete_ = function() {
  var response = new jstestdriver.Response(
      jstestdriver.RESPONSE_TYPES.TEST_RESULT,
      JSON.stringify(this.testsDone_),
      this.getBrowserInfo());
  this.testsDone_ = [];
  this.streamingService_.close(response, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.onComplete_ = function() {
  this.sendTestResultsOnComplete_();
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
