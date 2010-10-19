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
            jstestdriver.convertToJson(jstestdriver.jQuery.post));
    
    function getBrowserInfo() {
      return new jstestdriver.BrowserInfo(id);
    }

    var executor = new jstestdriver.CommandExecutor(streamingService,
                                                    testCaseManager,
                                                    testRunner,
                                                    pluginRegistrar,
                                                    now,
                                                    getBrowserInfo);

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
    
    var runTestsCommand = new jstestdriver.RunTestsCommand(testCaseManager,
                                                           testRunner,
                                                           pluginRegistrar,
                                                           getBrowserInfo,
                                                           jstestdriver.now,
                                                           jsonParse,
                                                           streamContinue,
                                                           streamStop);

    executor.registerCommand('execute', executor, executor.execute);
    executor.registerCommand('noop', null, streamStop);
    executor.registerCommand('runAllTests', runTestsCommand, runTestsCommand.runAllTests);
    executor.registerCommand('runTests', runTestsCommand, runTestsCommand.runTests);
    executor.registerCommand('loadTest', loadTestsCommand, loadTestsCommand.loadTest);
    executor.registerCommand('reset', executor, executor.reset);
    executor.registerCommand('dryRun', executor, executor.dryRun);
    executor.registerCommand('dryRunFor', executor, executor.dryRunFor);
    executor.registerCommand('streamAcknowledged',
                             streamingService,
                             streamingService.streamAcknowledged);

    return executor;
  }

  // TODO(corysmith): Factor out the duplicated code.
  /**
   * Creates a stand alone CommandExecutor.
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
  config.createStandAloneExecutor = function(testCaseManager,
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
            jstestdriver.convertToJson(jstestdriver.jQuery.post));
    
    function getBrowserInfo() {
      return new jstestdriver.BrowserInfo(id);
    }

    var reporter = new jstestdriver.StandAloneTestReporter();
    window.top.G_testRunner = reporter;

    var executor = new jstestdriver.CommandExecutor(streamingService,
            testCaseManager,
            testRunner,
            pluginRegistrar,
            now,
            getBrowserInfo);

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
            reporter);
    
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

    executor.registerCommand('execute', executor, executor.execute);
    executor.registerCommand('noop', null, streamStop);
    executor.registerCommand('runAllTests', runTestsCommand, runTestsCommand.runAllTests);
    executor.registerCommand('runTests', runTestsCommand, runTestsCommand.runTests);
    executor.registerCommand('loadTest', loadTestsCommand, loadTestsCommand.loadTest);
    executor.registerCommand('reset', executor, executor.reset);
    executor.registerCommand('dryRun', executor, executor.dryRun);
    executor.registerCommand('dryRunFor', executor, executor.dryRunFor);
    executor.registerCommand('streamAcknowledged',
            streamingService,
            streamingService.streamAcknowledged);

    return executor;
  }

  return config;
})(jstestdriver.config);

