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
jstestdriver.plugins.DefaultPlugin = function(fileLoaderPlugin,
                                              testRunnerPlugin,
                                              assertsPlugin,
                                              testCaseManagerPlugin) {
  this.fileLoaderPlugin_ = fileLoaderPlugin;
  this.testRunnerPlugin_ = testRunnerPlugin;
  this.assertsPlugin_ = assertsPlugin;
  this.testCaseManagerPlugin_ = testCaseManagerPlugin;
};


jstestdriver.plugins.DefaultPlugin.prototype.name = 'defaultPlugin';


jstestdriver.plugins.DefaultPlugin.prototype.loadSource = function(file, onSourceLoaded) {
  return this.fileLoaderPlugin_.loadSource(file, onSourceLoaded);
};


jstestdriver.plugins.DefaultPlugin.prototype.runTestConfiguration = function(testRunConfiguration,
    onTestDone, onTestRunConfigurationComplete) {
  return this.testRunnerPlugin_.runTestConfiguration(testRunConfiguration, onTestDone,
      onTestRunConfigurationComplete);
};


jstestdriver.plugins.DefaultPlugin.prototype.isFailure = function(exception) {
  return this.assertsPlugin_.isFailure(exception);
};


jstestdriver.plugins.DefaultPlugin.prototype.getTestRunsConfigurationFor =
    function(testCaseInfos, expressions, testRunsConfiguration) {
  return this.testCaseManagerPlugin_.getTestRunsConfigurationFor(testCaseInfos,
                                                                expressions,
                                                                testRunsConfiguration);
};


jstestdriver.plugins.DefaultPlugin.prototype.onTestsStart =
    jstestdriver.EMPTY_FUNC;


jstestdriver.plugins.DefaultPlugin.prototype.onTestsFinish =
  jstestdriver.EMPTY_FUNC;
