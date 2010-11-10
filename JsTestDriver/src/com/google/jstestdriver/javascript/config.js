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
 * @fileDesc Configuration namespace for setting up JsTD runners.
 */
jstestdriver.config = (function(module) {
  var config = module || {};

  /**
   * Start a new runner.
   */
  config.startRunner = function(createCommandExecutor, testBreather, opt_runTestLoop) {
    
    var runTestLoop = opt_runTestLoop || jstestdriver.plugins.defaultRunTestLoop;
    
    jstestdriver.pluginRegistrar = new jstestdriver.PluginRegistrar();
    jstestdriver.testCaseManager =
        new jstestdriver.TestCaseManager(jstestdriver.pluginRegistrar);

    jstestdriver.testRunner = new jstestdriver.TestRunner(
        jstestdriver.pluginRegistrar,
        jstestdriver.testBreather(jstestdriver.setTimeout,
                                  jstestdriver.TIMEOUT)
    );

    jstestdriver.testCaseBuilder =
        new jstestdriver.TestCaseBuilder(jstestdriver.testCaseManager);

    jstestdriver.global.TestCase = jstestdriver.bind(jstestdriver.testCaseBuilder,
        jstestdriver.testCaseBuilder.TestCase);

    jstestdriver.global.AsyncTestCase = jstestdriver.bind(jstestdriver.testCaseBuilder,
        jstestdriver.testCaseBuilder.AsyncTestCase);
    
    jstestdriver.global.ConditionalTestCase = jstestdriver.bind(jstestdriver.testCaseBuilder,
        jstestdriver.testCaseBuilder.ConditionalTestCase);

    // default plugin
    var scriptLoader = new jstestdriver.plugins.ScriptLoader(window, document,
          jstestdriver.testCaseManager, jstestdriver.now);
    var stylesheetLoader =
        new jstestdriver.plugins.StylesheetLoader(window, document,
              jstestdriver.jQuery.browser.mozilla || jstestdriver.jQuery.browser.safari);
    var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(
            scriptLoader,
            stylesheetLoader,
            jstestdriver.now);
    var testRunnerPlugin =
          new jstestdriver.plugins.TestRunnerPlugin(Date, function() {
        jstestdriver.jQuery('body').children().remove();
        jstestdriver.jQuery(document).unbind();
        jstestdriver.jQuery(document).die();
    }, runTestLoop);

    jstestdriver.pluginRegistrar.register(
        new jstestdriver.plugins.DefaultPlugin(
            fileLoaderPlugin,
            testRunnerPlugin,
            new jstestdriver.plugins.AssertsPlugin(),
            new jstestdriver.plugins.TestCaseManagerPlugin()));

    jstestdriver.pluginRegistrar.register(
        new jstestdriver.plugins.async.AsyncTestRunnerPlugin(Date, function() {
          jstestdriver.jQuery('body').children().remove();
          jstestdriver.jQuery(document).unbind();
          jstestdriver.jQuery(document).die();
        }));

    // legacy
    jstestdriver.testCaseManager.TestCase = jstestdriver.global.TestCase;

    jstestdriver.executor = createCommandExecutor(jstestdriver.testCaseManager,
                                                  jstestdriver.testRunner,
                                                  jstestdriver.pluginRegistrar,
                                                  jstestdriver.now,
                                                  window.location.toString());

    jstestdriver.executor.listen();
  };
  
  /**
   * Creates a CommandExecutor.
   * @static
   * 
   * @param {jstestdriver.TestCaseManager} testCaseManager
   * @param {jstestdriver.TestRunner} streamingService
   * @param {jstestdriver.PluginRegistrar} pluginRegistrar
   * @param {function():Number} now
   * @param {String} location The current window location
   * 
   * @return {jstestdriver.CommandExecutor}
   */
  config.createExecutor = function(testCaseManager,
                                   testRunner,
                                   pluginRegistrar,
                                   now,
                                   location) {
    var id = parseInt(jstestdriver.extractId(location));
    var url =jstestdriver.createPath(top.location.toString(),
                                     jstestdriver.SERVER_URL + id);

    var streamingService = new jstestdriver.StreamingService(
            url,
            now,
            jstestdriver.convertToJson(jstestdriver.jQuery.post),
            jstestdriver.createSynchPost(jstestdriver.jQuery));
    
    function getBrowserInfo() {
      return new jstestdriver.BrowserInfo(id);
    }

    var executor = new jstestdriver.CommandExecutor(streamingService,
                                                    testCaseManager,
                                                    testRunner,
                                                    pluginRegistrar,
                                                    now,
                                                    getBrowserInfo);

    var currentActionSignal = new jstestdriver.Signal(null);

    var boundExecuteCommand = jstestdriver.bind(executor, executor.executeCommand);

    function streamStop(response) {
      streamingService.close(response, boundExecuteCommand)
    }

    function streamContinue(response) {
      streamingService.stream(response, boundExecuteCommand);
    }

    var loadTestsCommand = new jstestdriver.LoadTestsCommand(jsonParse,
            pluginRegistrar,
            getBrowserInfo,
            streamStop);

    var runTestsCommand = new jstestdriver.RunTestsCommand(
        testCaseManager,
        testRunner,
        pluginRegistrar,
        getBrowserInfo,
        jstestdriver.now,
        jsonParse,
        streamContinue,
        streamStop);
    var unloadSignal = new jstestdriver.Signal(false);
    var resetCommand = new jstestdriver.ResetCommand(
        window.location,
        unloadSignal);

    executor.registerCommand('execute', executor, executor.execute);
    executor.registerCommand('noop', null, streamStop);
    executor.registerCommand('runAllTests', runTestsCommand, runTestsCommand.runAllTests);
    executor.registerCommand('runTests', runTestsCommand, runTestsCommand.runTests);
    executor.registerCommand('loadTest', loadTestsCommand, loadTestsCommand.loadTest);
    executor.registerCommand('reset', resetCommand, resetCommand.reset);
    executor.registerCommand('dryRun', executor, executor.dryRun);
    executor.registerCommand('dryRunFor', executor, executor.dryRunFor);
    executor.registerCommand('streamAcknowledged',
                             streamingService,
                             streamingService.streamAcknowledged);

    function getCommand() {
      return currentActionSignal.get();
    }

    var unloadHandler = new jstestdriver.PageUnloadHandler(
        streamingService,
        getBrowserInfo,
        getCommand,
        unloadSignal);
    
    jstestdriver.jQuery(window).bind('unload', jstestdriver.bind(unloadHandler, unloadHandler.onUnload));
    jstestdriver.jQuery(window).bind('beforeunload', jstestdriver.bind(unloadHandler, unloadHandler.onUnload));
    window.onbeforeunload = jstestdriver.bind(unloadHandler, unloadHandler.onUnload);

    return executor;
  }
  
  /**
   * Creates a visual stand alone CommandExecutor.
   * @static
   * 
   * @param {jstestdriver.TestCaseManager} testCaseManager
   * @param {jstestdriver.TestRunner} streamingService
   * @param {jstestdriver.PluginRegistrar} pluginRegistrar
   * @param {function():Number} now
   * @param {String} location The current window location
   * @param {jstestdriver.TestReporter} reporter Reports test results.
   * 
   * @return {jstestdriver.CommandExecutor}
   */
  config.createVisualExecutor = function(testCaseManager,
      testRunner,
      pluginRegistrar,
      now,
      location) {
    return config.createStandAloneExecutorWithReporter(testCaseManager,
      testRunner,
      pluginRegistrar,
      now,
      location,
      new jstestdriver.VisualTestReporter(function(tagName) {
        return document.createElement(tagName);
      },
      function(node) {
        return document.body.appendChild(node);
      },
      jstestdriver.jQuery,
      JSON.parse));
  };

  /**
   * Creates a stand alone CommandExecutor.
   * @static
   * 
   * @param {jstestdriver.TestCaseManager} testCaseManager
   * @param {jstestdriver.TestRunner} streamingService
   * @param {jstestdriver.PluginRegistrar} pluginRegistrar
   * @param {function():Number} now
   * @param {String} location The current window location
   * @param {jstestdriver.TestReporter} reporter Reports test results.
   * 
   * @return {jstestdriver.CommandExecutor}
   */
  config.createStandAloneExecutor =  function(testCaseManager,
      testRunner,
      pluginRegistrar,
      now,
      location) {
    return config.createStandAloneExecutorWithReporter(testCaseManager,
        testRunner,
        pluginRegistrar,
        now,
        location,
        new jstestdriver.StandAloneTestReporter())
  };


  // TODO(corysmith): Factor out the duplicated code.
  /**
   * Creates a stand alone CommandExecutor configured with a reporter.
   * @static
   * 
   * @param {jstestdriver.TestCaseManager} testCaseManager
   * @param {jstestdriver.TestRunner} streamingService
   * @param {jstestdriver.PluginRegistrar} pluginRegistrar
   * @param {function():Number} now
   * @param {String} location The current window location
   * @param {jstestdriver.TestReporter} reporter Reports test results.
   * 
   * @return {jstestdriver.CommandExecutor}
   */
  config.createStandAloneExecutorWithReporter = function(testCaseManager,
          testRunner,
          pluginRegistrar,
          now,
          location,
          reporter) {
    var id = parseInt(jstestdriver.extractId(location));
    var url =jstestdriver.createPath(top.location.toString(),
        jstestdriver.SERVER_URL + id);
    
    var streamingService = new jstestdriver.StreamingService(
            url,
            now,
            jstestdriver.convertToJson(jstestdriver.jQuery.post));

    function getBrowserInfo() {
      return new jstestdriver.BrowserInfo(id);
    }
    window.top.G_testRunner = reporter;

    var currentActionSignal = new jstestdriver.Signal(null);
    
    var executor = new jstestdriver.CommandExecutor(streamingService,
            testCaseManager,
            testRunner,
            pluginRegistrar,
            now,
            getBrowserInfo,
            currentActionSignal);

    var boundExecuteCommand = jstestdriver.bind(executor, executor.executeCommand);
    
    function streamStop(response) {
      streamingService.close(response, boundExecuteCommand)
    }

    function streamContinue(response) {
      streamingService.stream(response, boundExecuteCommand);
    }

    var loadTestsCommand = new jstestdriver.StandAloneLoadTestsCommand(jsonParse,
            pluginRegistrar,
            getBrowserInfo,
            streamStop,
            reporter,
            jstestdriver.now);
    
    var runTestsCommand =
        new jstestdriver.StandAloneRunTestsCommand(testCaseManager,
                testRunner,
                pluginRegistrar,
                getBrowserInfo,
                reporter,
                now,
                jsonParse,
                streamContinue,
                streamStop);

    executor.registerTracedCommand('execute', executor, executor.execute);
    executor.registerTracedCommand('noop', null, streamStop);
    executor.registerTracedCommand('runAllTests', runTestsCommand, runTestsCommand.runAllTests);
    executor.registerTracedCommand('runTests', runTestsCommand, runTestsCommand.runTests);
    executor.registerTracedCommand('loadTest', loadTestsCommand, loadTestsCommand.loadTest);
    executor.registerTracedCommand('reset', executor, executor.reset);
    executor.registerTracedCommand('dryRun', executor, executor.dryRun);
    executor.registerTracedCommand('dryRunFor', executor, executor.dryRunFor);
    executor.registerCommand('streamAcknowledged',
            streamingService,
            streamingService.streamAcknowledged);

    return executor;
  }

  return config;
})(jstestdriver.config);

