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


RunTestsCommandTest = TestCase('RunTestsCommandTest');


RunTestsCommandTest.prototype.testCallPluginOnTestResultAdded = function() {
  function getBrowserInfo() {
    return new jstestdriver.BrowserInfo(1);
  }

  var expected = new jstestdriver.TestResult("testsuite", "foo", "passed", "", [], 1, {
    foo : 1
  });

  var tmpPlugin = {
    name : "tmpPlugin"
  };
  tmpPlugin[jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT] = function(result) {
    result.data = expected.data;
  }
  
  var registrar = new jstestdriver.PluginRegistrar();
  registrar.register(tmpPlugin);

  var testResponse = null;
  var executor = new jstestdriver.RunTestsCommand(null,
                                                  null,
                                                  registrar,
                                                  getBrowserInfo,
                                                  jstestdriver.now,
                                                  null,
                                                  function(response){
    testResponse = response;
  },
                                                  null);
  
  var result = new jstestdriver.TestResult(expected.testCaseName,
                                           expected.testName,
                                           expected.result,
                                           expected.message,
                                           expected.log,
                                           expected.time,
                                           {});
  executor.addTestResult(result);
  executor.sendTestResults();
  assertNotNull(testResponse);
  assertEquals(JSON.stringify([expected]), testResponse.response);
};