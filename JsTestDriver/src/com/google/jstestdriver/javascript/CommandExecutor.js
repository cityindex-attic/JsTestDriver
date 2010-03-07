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

  var testRunner = new jstestdriver.TestRunner(jstestdriver.pluginRegistrar,
                                               function (callback){
    callback();
  });
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

  
  new jstestdriver.CommandExecutor(parseInt(id),
      url,
      jstestdriver.convertToJson(jstestdriver.jQuery.post),
      jstestdriver.testCaseManager,
      testRunner,
      jstestdriver.pluginRegistrar).listen();
};


jstestdriver.TIMEOUT = 500;


jstestdriver.CommandExecutor = function(id, url, sendRequest, testCaseManager, testRunner,
    pluginRegistrar) {
  this.__id = id;
  this.__url = url;
  this.__sendRequest = sendRequest;
  this.__testCaseManager = testCaseManager;
  this.__testRunner = testRunner;
  this.__pluginRegistrar = pluginRegistrar;
  this.__boundExecuteCommand = jstestdriver.bind(this, this.executeCommand);
  this.__boundExecute = jstestdriver.bind(this, this.execute);
  this.__boundEvaluateCommand = jstestdriver.bind(this, this.evaluateCommand);
  this.__boundSendData = jstestdriver.bind(this, this.sendData);
  this.boundCleanTestManager = jstestdriver.bind(this, this.cleanTestManager);
  this.boundOnFileLoaded_ = jstestdriver.bind(this, this.onFileLoaded);
  this.boundOnFileLoadedRunnerMode_ = jstestdriver.bind(this, this.onFileLoadedRunnerMode);
  this.commandMap_ = {
      execute: jstestdriver.bind(this, this.execute),
      runAllTests: jstestdriver.bind(this, this.runAllTests),
      runTests: jstestdriver.bind(this, this.runTests),
      loadTest: jstestdriver.bind(this, this.loadTest),
      reset: jstestdriver.bind(this, this.reset),
      registerCommand: jstestdriver.bind(this, this.registerCommand),
      dryRun: jstestdriver.bind(this, this.dryRun),
      dryRunFor: jstestdriver.bind(this, this.dryRunFor)
  };
  this.boundOnTestDone = jstestdriver.bind(this, this.onTestDone_);
  this.boundOnComplete = jstestdriver.bind(this, this.onComplete_);
  this.boundOnTestDoneRunnerMode = jstestdriver.bind(this, this.onTestDoneRunnerMode_);
  this.boundOnCompleteRunnerMode = jstestdriver.bind(this, this.onCompleteRunnerMode_);
  this.boundSendTestResults = jstestdriver.bind(this, this.sendTestResults_);
  this.boundOnDataSent = jstestdriver.bind(this, this.onDataSent_);
  this.testsDone_ = [];
  this.sentOn_ = -1;
  this.done_ = false;
};


jstestdriver.CommandExecutor.prototype.executeCommand = function(jsonCommand) {
  if (jsonCommand == 'noop') {
    this.__sendRequest(this.__url, null, this.__boundExecuteCommand);
  } else {
    var command = jsonParse(jsonCommand);

    this.commandMap_[command.command](command.parameters);
  }
};


jstestdriver.CommandExecutor.prototype.sendData = function(data) {
  this.__sendRequest(this.__url, data, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.execute = function(cmd) {
  var data = {
    done: '',
    type: jstestdriver.RESPONSE_TYPES.COMMAND_RESULT,
    response: {
      response: this.__boundEvaluateCommand(cmd),
      browser: {
        "id": this.__id
      }
    }
  };
  this.sendData(data);
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


jstestdriver.CommandExecutor.prototype.removeScriptTags_ = function(dom, scriptTagsToRemove) {
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
  var data = new jstestdriver.StreamMessage('', response);
  this.sendData(data);
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
  this.__sendRequest(this.__url, null, this.__boundExecuteCommand);
};


jstestdriver.CommandExecutor.prototype.runAllTests = function(args) {
  var captureConsole = args[0];
  var runnerMode = args[1] == "true" ? true : false;

  this.runTestCases_(this.__testCaseManager.getDefaultTestRunsConfiguration(),
      captureConsole == "true" ? true : false, runnerMode);
};


jstestdriver.CommandExecutor.prototype.runTests = function(args) {
  var expressions = jsonParse('{"expressions":' + args[0] + '}').expressions;
  var captureConsole = args[1];

  this.runTestCases_(this.__testCaseManager.getTestRunsConfigurationFor(expressions),
      captureConsole == "true" ? true : false, false);
};


jstestdriver.CommandExecutor.prototype.runTestCases_ = function(testRunsConfiguration,
    captureConsole, runnerMode) {
  if (!runnerMode) {
    this.startTestInterval_(jstestdriver.TIMEOUT);
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


jstestdriver.CommandExecutor.prototype.startTestInterval_ = function(interval) {
  this.timeout_ = jstestdriver.setTimeout(this.boundSendTestResults, interval);
};


jstestdriver.CommandExecutor.prototype.stopTestInterval_ = function() {
  jstestdriver.clearTimeout(this.timeout_);
};


jstestdriver.CommandExecutor.prototype.onDataSent_ = function() {
  if (this.done_) {
    this.sendTestResultsOnComplete_();
  } else if (this.sentOn_ != -1) {
    var elapsed = new Date().getTime() - this.sentOn_;
    this.sentOn_ = -1;

    if (elapsed < jstestdriver.TIMEOUT) {
      this.startTestInterval_(jstestdriver.TIMEOUT - elapsed);
    } else {
      this.sendTestResults_();
    }
  }
};


jstestdriver.CommandExecutor.prototype.sendTestResults_ = function() {
  this.stopTestInterval_();
  if (this.testsDone_.length > 0) {
    var response = new jstestdriver.Response(
            jstestdriver.RESPONSE_TYPES.TEST_RESULT,
            JSON.stringify(this.testsDone_),
            this.getBrowserInfo());
    var data = new jstestdriver.StreamMessage('', response);

    this.testsDone_ = [];
    this.sentOn_ = new Date().getTime();
    this.__sendRequest(this.__url, data, this.boundOnDataSent);
  }
};


jstestdriver.CommandExecutor.prototype.onTestDone_ = function(result) {
  this.addTestResult(result);
  if ((result.result == 'error' || result.log != '') && this.sentOn_ == -1) {
    this.sendTestResults_();
  }
};


// TODO(corysmith): refactor the testsDone collection into a separate object.
jstestdriver.CommandExecutor.prototype.addTestResult = function(testResult) {
  this.__pluginRegistrar.processTestResult(testResult);
  this.testsDone_.push(testResult);
};


jstestdriver.CommandExecutor.prototype.sendTestResultsOnComplete_ = function() {
  this.stopTestInterval_();
  this.done_ = false;
  this.sentOn_ = -1;
  var response = new jstestdriver.Response(
      jstestdriver.RESPONSE_TYPES.TEST_RESULT,
      JSON.stringify(this.testsDone_),
      this.getBrowserInfo());
  var data = new jstestdriver.StreamMessage('', response);

  this.testsDone_ = [];
  this.__sendRequest(this.__url, data, this.__boundExecuteCommand);  
};

jstestdriver.CommandExecutor.prototype.onComplete_ = function() {
  this.done_ = true;
  if (this.sentOn_ == -1) {
    this.sendTestResultsOnComplete_();
  }
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


jstestdriver.CommandExecutor.prototype.registerCommand = function(name, func) {
  this[name] = jstestdriver.bind(this, func);
  var response =
      new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.REGISTER_RESULT,
          'Command ' + name + ' registered.',
          this.getBrowserInfo());

  this.sendData(new jstestdriver.StreamMessage('', response));
};


jstestdriver.CommandExecutor.prototype.dryRun = function() {
  var response =
      new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_QUERY_RESULT,
          JSON.stringify(this.__testCaseManager.getCurrentlyLoadedTest()),
          this.getBrowserInfo());
  
  this.sendData(new jstestdriver.StreamMessage('', response));
};


jstestdriver.CommandExecutor.prototype.dryRunFor = function(args) {
  var expressions = jsonParse('{"expressions":' + args[0] + '}').expressions;
  var tests = JSON.stringify(
      this.__testCaseManager.getCurrentlyLoadedTestFor(expressions))
  var data = new jstestdriver.StreamMessage('',
      new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.TEST_QUERY_RESULT,
          tests,
          this.getBrowserInfo()));
  this.sendData(data);
};


jstestdriver.CommandExecutor.prototype.listen = function() {
  if (window.location.href.search('\\?refresh') != -1) {
    var data = new jstestdriver.StreamMessage('',
        new jstestdriver.Response(jstestdriver.RESPONSE_TYPES.RESET_RESULT,
                                  'Runner reset.',
                                  this.getBrowserInfo())
    );
    this.__sendRequest(this.__url + '?start', data, this.__boundExecuteCommand);
  } else {
    this.__sendRequest(this.__url + '?start', null, this.__boundExecuteCommand);
  }
};
