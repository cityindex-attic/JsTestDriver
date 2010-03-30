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
 * Plugin that handles the default behavior for the TestCaseManager.
 * @author corysmith@google.com (Cory Smith)
 */
jstestdriver.plugins.TestCaseManagerPlugin = function() {};


/**
 * Write testRunconfigurations retrieved from testCaseInfos defined by expressions.
 * @param {Array.<jstestdriver.TestCaseInfo>} testCaseInfos The loaded test case infos.
 * @param {Array.<String>} The expressions that define the TestRunConfigurations
 * @parma {Array.<jstestdriver.TestRunConfiguration>} The resultant array of configurations.
 */
jstestdriver.plugins.TestCaseManagerPlugin.prototype.getTestRunsConfigurationFor =
    function(testCaseInfos, expressions, testRunsConfiguration) {
  var size = testCaseInfos.length;
  for (var i = 0; i < size; i++) {
    var testCaseInfo = testCaseInfos[i];
    var testRunConfiguration = testCaseInfo.getTestRunConfigurationFor(expressions);

    if (testRunConfiguration != null) {
      testRunsConfiguration.push(testRunConfiguration);
    }
  }
  return true;
};
