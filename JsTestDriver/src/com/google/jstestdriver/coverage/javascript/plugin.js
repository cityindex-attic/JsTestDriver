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
(function() {
  var reporter = new coverage.Reporter();
  jstestdriver.global.LCOV = reporter;

  var testRunnerPlugin =
      new jstestdriver.plugins.TestRunnerPlugin(Date, function() {
    jstestdriver.jQuery('body').children().remove();
    jstestdriver.jQuery(document).unbind();
    jstestdriver.jQuery(document).die();
  });

  var plugin =
      new coverage.InstrumentedTestCaseRunnerPlugin(testRunnerPlugin,
                                                    reporter,
                                                    jstestdriver.setTimeout);
  jstestdriver.pluginRegistrar.register(plugin);

  var resultsPlugin = {name : 'coverage'};
  resultsPlugin[jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT] = function(testResult) {
    if (!testResult.data[coverage.COVERAGE_DATA_KEY]) {
      testResult.data[coverage.COVERAGE_DATA_KEY] =
          reporter.summarizeCoverage().toProtoBuffer();
    }
  }
  jstestdriver.pluginRegistrar.register(resultsPlugin);

})();
