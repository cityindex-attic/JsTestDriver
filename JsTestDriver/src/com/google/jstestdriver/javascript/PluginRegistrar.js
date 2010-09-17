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
 * The PluginRegistrar allows developers to load their own plugins to perform certain actions.
 * A plugin must define methods with specific names in order for it to be used.
 * A plugin must define a name, by defining a property name.
 * 
 * A simple plugin supporting the loadSource method for example could look as follow:
 * 
 * var myPlugin = {
 *   name: 'myPlugin',
 *   loadSource: function(file, onSourceLoad) {
 *     // do some cool stuff
 *   }
 * };
 * 
 * To then register it one needs to call:
 * 
 * jstestdriver.pluginRegistrar.register(myPlugin);
 * 
 * The list of supported methods is:
 * - loadSource
 * - runTestConfiguration
 * 
 * For more information regarding the supported method just read the documentation for the method
 * in this class.
 */
jstestdriver.PluginRegistrar = function() {
  this.plugins_ = [];
};


jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT = 'processTestResult';


jstestdriver.PluginRegistrar.LOAD_SOURCE = 'loadSource';


jstestdriver.PluginRegistrar.RUN_TEST = 'runTestConfiguration';


jstestdriver.PluginRegistrar.IS_FAILURE = 'isFailure';


jstestdriver.PluginRegistrar.GET_TEST_RUN_CONFIGURATIONS = 'getTestRunsConfigurationFor';


jstestdriver.PluginRegistrar.ON_TESTS_START = 'onTestsStart';

jstestdriver.PluginRegistrar.ON_TESTS_FINISH = 'onTestsFinish';


jstestdriver.PluginRegistrar.prototype.register = function(plugin) {
  if (!plugin.name) {
    throw new Error("Plugins must define a name.");
  }
  var index = this.getIndexOfPlugin_(plugin.name);
  var howMany = 1;

  if (index == -1) {
    index = this.plugins_.length - 1;
    howMany = 0;
  }
  this.plugins_.splice(index, howMany, plugin);
};


jstestdriver.PluginRegistrar.prototype.unregister = function(plugin) {
  var index = this.getIndexOfPlugin_(plugin.name);

  if (index != -1) {
    this.plugins_.splice(index, 1);
  }
};


jstestdriver.PluginRegistrar.prototype.getPlugin = function(name) {
  var index = this.getIndexOfPlugin_(name);

  return index != -1 ? this.plugins_[index] : null;
};


jstestdriver.PluginRegistrar.prototype.getNumberOfRegisteredPlugins = function() {
  return this.plugins_.length;
};


jstestdriver.PluginRegistrar.prototype.dispatch_ = function(method, parameters) {
  var size = this.plugins_.length;

  for (var i = 0; i < size; i++) {
    var plugin = this.plugins_[i];

    if (plugin[method]) {
      if (plugin[method].apply(plugin, parameters)) {
        return true;
      }
    }
  }
  return false;
};


jstestdriver.PluginRegistrar.prototype.getIndexOfPlugin_ = function(name) {
  var size = this.plugins_.length;

  for (var i = 0; i < size; i++) {
    var plugin = this.plugins_[i];

    if (plugin.name == name) {
      return i;
    }
  }
  return -1;
};


/**
 * loadSource
 * 
 * By defining the method loadSource a plugin can implement its own way of loading certain types of
 * files.
 * 
 * loadSource takes 2 parameters:
 *  - file: A file object defined as -> { fileSrc: string, timestamp: number, basePath: string }
 *    fileSrc is the name of the file
 *    timestamp is the last modified date of the file
 *    basePath is defined if the file is a URL and the URL has been rewritten
 *  - onSourceLoad: A callback that must be called once the file has been loaded the callback takes
 *    1 parameter defined as -> { file: file object, success: boolean, message: string }
 *    file: A file object
 *    success: a boolean, true if the file was loaded successfully, false otherwise
 *    message: an error message if the file wasn't loaded properly
 *  
 *  loadSource must return a boolean:
 *  - true if the plugin knows how to and loaded the file
 *  - false if the plugin doesn't know how to load the file
 *  
 *  A simple loadSource plugin would look like:
 *  
 *  var myPlugin = {
 *    name: 'myPlugin',
 *    loadSource: function(file, onSourceLoad) {
 *      // load the file
 *      return true;
 *    }
 *  }
 */
jstestdriver.PluginRegistrar.prototype.loadSource = function(file, onSourceLoad) {
  this.dispatch_(jstestdriver.PluginRegistrar.LOAD_SOURCE, arguments);
};


/**
 * runTestConfiguration
 * 
 * By defining the method runTestConfiguration a plugin can implement its own way of running
 * certain types of tests.
 * 
 * runTestConfiguration takes 3 parameters:
 * - testRunConfiguration: A jstestdriver.TestRunConfiguration object.
 * - onTestDone: A callback that needs to be call when a test ran so that the results are properly
 *   sent back to the client. It takes 1 parameter a jstestdriver.TestResult.
 * - onTestRunConfigurationComplete: A callback that needs to be call when everything ran. It takes
 *   no parameter.
 *   
 * runTestConfiguration must return a boolean:
 * - true if the plugin can run the tests
 * - false if the plugin can not run the tests
 * 
 * A simple runTestConfiguration plugin would look like:
 * 
 * var myPlugin = {
 *   name: 'myPlugin',
 *   runTestConfiguration: function(testRunConfiguration, onTestDone,
 *       onTestRunConfigurationComplete) {
 *     // run the tests
 *     return true;
 *   }
 * }
 * 
 */
jstestdriver.PluginRegistrar.prototype.runTestConfiguration = function(testRunConfiguration,
    onTestDone, onTestRunConfigurationComplete) {
  this.dispatch_(jstestdriver.PluginRegistrar.RUN_TEST, arguments);
};


/**
 * processTestResult
 * 
 * By defining the method processTestResult a plugin can pass extra meta data about a test back to
 * the server.
 * 
 * processTestResult takes 1 parameter:
 * - testResult: The TestResult of the most recently run test.
 *              
 *   
 * processTestResult must return a boolean:
 * - true to allow other plugins to process the test result
 * - false if not further processing should be allowed.
 * 
 * A simple processTestResult plugin would look like:
 * 
 * var myPlugin = {
 *   name: 'myPlugin',
 *   processTestResult: function(testResult) {
 *     testResult.data.foo = 'bar';
 *     return true;
 *   }
 * }
 * 
 */
jstestdriver.PluginRegistrar.prototype.processTestResult = function(testResult) {
  this.dispatch_(jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT, arguments);
};


/**
 * isFailure
 * 
 * By defining the method isFailure a plugin will determine if an exception thrown by an assertion
 * framework is considered a failure by the assertion framework.
 * 
 * isFailure takes 1 parameter:
 * - exception: The exception thrown by the test
 *              
 *   
 * processTestResult must return a boolean:
 * - true if the exception is considered a failure
 * - false if the exception is not considered a failure
 * 
 * A simple isFailure plugin would look like:
 * 
 * var myPlugin = {
 *   name: 'myPlugin',
 *   isFailure: function(exception) {
 *     return exception.name == 'AssertError';
 *   }
 * }
 * 
 */
jstestdriver.PluginRegistrar.prototype.isFailure = function(exception) {
  return this.dispatch_(jstestdriver.PluginRegistrar.IS_FAILURE, arguments);
};


/**
 * getTestRunsConfigurationFor
 * 
 * By defining the method getTestRunsConfigurationFor a plugin will be able to
 * modify the process in which a TestCaseInfo provides a TestRunConfiguration from an expression
 * such as fooCase.testBar.
 * 
 * getTestRunsConfigurationFor takes 3 parameters:
 * - testCaseInfos: The array of loaded TestCaseInfos.
 * - expressions: The array of expressions used to determine the TestRunConfiguration.
 * - testRunsConfiguration: An array to add test case TestRunConfigurations to.
 * 
 */
jstestdriver.PluginRegistrar.prototype.getTestRunsConfigurationFor =
    function(testCaseInfos, expressions, testRunsConfiguration) {
  return this.dispatch_(jstestdriver.PluginRegistrar.GET_TEST_RUN_CONFIGURATIONS, arguments);
};


/**
 * onTestsStart
 * 
 * A setup hook called before all tests are run.
 * 
 */
jstestdriver.PluginRegistrar.prototype.onTestsStart = function() {
  return this.dispatch_(jstestdriver.PluginRegistrar.ON_TESTS_START, []);
};


/**
 * onTestsFinish
 * 
 * A tear down hook called after all tests are run.
 * 
 */
jstestdriver.PluginRegistrar.prototype.onTestsFinish = function() {
  return this.dispatch_(jstestdriver.PluginRegistrar.ON_TESTS_FINISH, []);
};
