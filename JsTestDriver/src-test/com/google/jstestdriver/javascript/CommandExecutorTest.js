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
var commandExecutorTest = jstestdriver.testCaseManager.TestCase('commandExecutorTest'); 

commandExecutorTest.prototype.testExtractIdFromUrl =  function() {
  assertEquals(1, jstestdriver.extractId("http://server:123/slave/1/RemoteConsoleRunner.html"));
};


commandExecutorTest.prototype.testFetchCommandNoLibsSendResponseLoop = function() {
  var data;
  var callback;
  var url;
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback){
    url = _url;
    callback = _callback;
    data = _data;
  }, null);

  executor.listen();
  assertNotNull(callback);
  assertEquals("/Q1?start", url);

  var lastCallback = callback;
  callback(JSON.stringify({ 'command': 'execute', 'parameters': [ '1' ] }));
  assertEquals(1, data.response.response);
  assertEquals(lastCallback, callback);

  callback(JSON.stringify({ 'command': 'execute', 'parameters': [ '2' ] }));
  assertEquals(2, data.response.response);
  assertEquals(lastCallback, callback);
};


commandExecutorTest.prototype.testConvertJsToJson = function() {
  var callback = {};
  var jquery =  function(_url, _data, _callback) {
    assertEquals("url", _url);
    assertEquals('{"b":"c"}', _data.a);
    assertSame(callback, _callback);
  };
  jstestdriver.convertToJson(jquery)("url", {a: {b:"c"}}, callback);
};


commandExecutorTest.prototype.testHandleDisconnectionByServer = function() {
  var url;
  var data;
  var callback;
  var called = 0;
  var executor = new jstestdriver.CommandExecutor(1, "/query/1", function(_url, _data, _callback) {
    url = _url;
    data = _data;
    callback = _callback;
    called++;
  }, null);
  executor.listen();
  callback('noop');
  assertEquals("/query/1", url);
  assertSame(null, data);
  assertNotNull(callback);
  assertEquals(2, called);
};


//commandExecutorTest.prototype.testLoadLibsSendResponse = function() {
//  var data;
//  var callback;
//  var url;
//  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback){
//    url = _url;
//    callback = _callback;
//    data = _data;
//  }, null);
//  var dom = new jstestdriver.MockDOM();
//
//  executor.listen();
//  assertNotNull(callback);
//  executor.loadLibraries(new Array('1.js', '2.js'), dom, function(_dom, _file, _callback) {
//    _callback();
//  });
//  assertNotNull(data);
//  assertEquals("Libraries loaded.", data.response.response);
//};


commandExecutorTest.prototype.testEvaluateGoodAndBadCommand = function() {
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback){}, null);
  var res = executor.evaluateCommand('1+2');

  assertEquals(res, 3);
  res = executor.evaluateCommand('"boom"');
  assertNotNull(res);
};


/*commandExecutorTest.prototype.testRunAllTestsForTestCase = function() {
  var called = 0;
  var testCalled = false;
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner({}, fakeDate));
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
    called++;
  }, testCaseManager);
  var arr = [];
  var testCase = testCaseManager.TestCase();

  testCase.prototype.testFoo = function() { testCalled = true; };
  arr.push({ 'name': 'testCase' });
  executor.listen();
  executor.runTests(arr);
  assertEquals(2, called);
  assertTrue(testCalled);
};*/


/*commandExecutorTest.prototype.testRunOneTest = function() {
  var fakeDate = function() {};

  fakeDate.prototype.getTime = function() {
    return 0
  };
  var testCaseManager = new jstestdriver.TestCaseManager(new jstestdriver.TestRunner({}, fakeDate));
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
  }, testCaseManager);
  var arr = [];
  var testCase = testCaseManager.TestCase();
  var testFooCalled = false;
  var testBarCalled = false;

  testCase.prototype.testFoo = function() { testFooCalled = true; };
  testCase.prototype.testBar = function() { testBarCalled = true; };
  arr.push({ 'name': 'testCase', 'tests': [ 'testFoo' ] });
  executor.listen();
  executor.runTests(arr);
  assertTrue(testFooCalled);
  assertFalse(testBarCalled);
};
*/


/*commandExecutorTest.prototype.testResetPage = function() {
  var data;
  var callback;
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
    data = _data;
    callback = _callback;
  }, null);
  var called = false;

  executor.reset();
  assertEquals('Runner reset.', data.response.response);
  assertSame(callback, executor.refresh);
};
*/

commandExecutorTest.prototype.testRemoveScriptTags = function() {
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
  }, null, null, null);
  var files = [];
  var dom = new jstestdriver.MockDOM();
  var head = dom.createElement('head');
  var script = dom.createElement('script');

  script.src = "file1";
  head.appendChild(script);
  files.push({fileSrc: "file1", timestamp: 42});
  executor.removeScripts(dom, files);
  assertEquals(0, head.childNodes.length);
};


commandExecutorTest.prototype.testRegisterCommandAndUseIt = function() {
  var data;
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
    data = _data;
  }, null);
  var called = false;
  var arg = '';

  executor.registerCommand('coolFunction', function(arg_) { called = true; arg = arg_; });
  assertEquals('Command coolFunction registered.', data.response.response);
  assertNotNull(executor.coolFunction);
  executor.coolFunction('cool');
  assertEquals('cool', arg);
  assertTrue(called);
};


commandExecutorTest.prototype.testCallPluginOnTestResultAdded = function() {
  var expected = new jstestdriver.TestResult("testsuite", "foo", "passed", "", [], 1, {
    foo : 1
  });

  var tmpPlugin = {};
  tmpPlugin[jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT] = function(result) {
    result.data = expected.data;
  }
  
  var registrar = new jstestdriver.PluginRegistrar();
  registrar.register(tmpPlugin);
  
  var dataSent = null;
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
    dataSent = _data;
  }, null, null, registrar);
  
  var result = new jstestdriver.TestResult(expected.testCaseName,
                                           expected.testName,
                                           expected.result,
                                           expected.message,
                                           expected.log,
                                           expected.time,
                                           {});
  executor.addTestResult(result);
  executor.boundSendTestResults();
  assertNotNull(dataSent);
  assertEquals(JSON.stringify([expected]), eval(dataSent.response).response);
};

/*commandExecutorTest.prototype.testParseJsonAndRunTheRightMethod = function() {
  var data;
  var executor = new jstestdriver.CommandExecutor(1, "/Q1", function (_url, _data, _callback) {
    data = _data;
  }, null);

  executor.executeCommand(JSON.stringify({ 'command': 'reset', 'parameters': [] }));
  assertEquals('Runner reset.', data.response.response);
};
*/

commandExecutorTest.prototype.testLastRunPacketIsAlwaysSentLast = function() {
  var pluginRegistrar = {
    processTestResult: function(_testResult) {
    }
  };
  var finalData = [];
  var executor = new jstestdriver.CommandExecutor(1, "/url", function(_url, _data, _callback)  {
    finalData.push(_data);
  }, null, null, pluginRegistrar);

  executor.startTestInterval_ = function() {};
  executor.stopTestInterval_ = function() {};
  executor.boundOnTestDone({ result: 'success', log: '' });
  executor.boundSendTestResults();
  executor.boundOnDataSent();
  executor.boundOnTestDone({ result: 'success', log: '' });
  executor.boundSendTestResults();
  executor.boundOnComplete();
  assertEquals(2, finalData.length);
  assertFalse(finalData[0].done);
  assertFalse(finalData[1].done);
  executor.boundOnDataSent();
  assertEquals(3, finalData.length);
  assertTrue(finalData[2].done);
};
