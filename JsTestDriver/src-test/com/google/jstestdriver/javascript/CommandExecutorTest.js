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
var CommandExecutorTest = jstestdriver.testCaseManager.TestCase('CommandExecutorTest'); 

CommandExecutorTest.prototype.testExtractIdFromUrl =  function() {
  assertEquals(1, jstestdriver.extractId("http://server:123/slave/1/RemoteConsoleRunner.html"));
};


CommandExecutorTest.prototype.setUp = function() {
  var posts = this.posts = [];
  this.streamingService = new jstestdriver.StreamingService("/Q1",
          function(){ return 1; },
          function (url, data, callback, type){
    posts.push({
      url : url,
      data : data,
      callback : callback,
      type : type
    });
  });
};


CommandExecutorTest.prototype.testFetchCommandNoLibsSendResponseLoop = function() {

  var executor = new jstestdriver.CommandExecutor(1,
          this.streamingService, null, null, null);
  executor.registerCommand('execute', executor, executor.execute);

  executor.listen();
  var listenPost = this.posts.pop();
  assertNotNull(listenPost.callback);
  assertEquals("/Q1", listenPost.url);
  assertTrue(listenPost.data.response.start);

  listenPost.callback(JSON.stringify({ 'command': 'execute', 'parameters': [ '1' ] }));
  var executePostOne = this.posts.pop();
  assertEquals("\"1\"", executePostOne.data.response.response);
  assertEquals(listenPost.callback, executePostOne.callback);

  executePostOne.callback(JSON.stringify({ 'command': 'execute', 'parameters': [ '2' ] }));
  var executePostTwo = this.posts.pop();
  assertEquals("\"2\"", executePostTwo.data.response.response);
  assertEquals(listenPost.callback, executePostTwo.callback);
};


CommandExecutorTest.prototype.testConvertJsToJson = function() {
  var callback = {};
  var jquery =  function(_url, _data, _callback) {
    assertEquals("url", _url);
    assertEquals('{"b":"c"}', _data.a);
    assertSame(callback, _callback);
  };
  jstestdriver.convertToJson(jquery)("url", {a: {b:"c"}}, callback);
};


CommandExecutorTest.prototype.testHandleDisconnectionByServer = function() {
  var url;
  var data;
  var callback;
  var called = 0;
  var executor = new jstestdriver.CommandExecutor(1, 
                                                  this.streamingService,
                                                  null,
                                                  null,
                                                  null);
  executor.listen();
  var listenPost = this.posts.pop();
  listenPost.callback('noop');
  var noopPost = this.posts.pop();
  assertEquals("/Q1", noopPost.url);
  assertEquals({"done":true,"response":null}, noopPost.data);
  assertNotNull(noopPost.callback);
};


//CommandExecutorTest.prototype.testLoadLibsSendResponse = function() {
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


CommandExecutorTest.prototype.testEvaluateGoodAndBadCommand = function() {
  var executor = new jstestdriver.CommandExecutor(1, 
                                                  this.streamingService,
                                                  null,
                                                  null,
                                                  null);
  var res = executor.evaluateCommand('1+2');

  assertEquals(res, 3);
  res = executor.evaluateCommand('"boom"');
  assertNotNull(res);
};


/*CommandExecutorTest.prototype.testRunAllTestsForTestCase = function() {
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


/*CommandExecutorTest.prototype.testRunOneTest = function() {
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

CommandExecutorTest.prototype.testRemoveScriptTags = function() {
  var executor = new jstestdriver.CommandExecutor(1, 
                                                  this.streamingService,
                                                  null,
                                                  null,
                                                  null);
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


CommandExecutorTest.prototype.testCallPluginOnTestResultAdded = function() {
  var expected = new jstestdriver.TestResult("testsuite", "foo", "passed", "", [], 1, {
    foo : 1
  });

  var tmpPlugin = {};
  tmpPlugin[jstestdriver.PluginRegistrar.PROCESS_TEST_RESULT] = function(result) {
    result.data = expected.data;
  }
  
  var registrar = new jstestdriver.PluginRegistrar();
  registrar.register(tmpPlugin);

  var executor = new jstestdriver.CommandExecutor(1, 
          this.streamingService,
          null,
          null,
          registrar);
  
  var result = new jstestdriver.TestResult(expected.testCaseName,
                                           expected.testName,
                                           expected.result,
                                           expected.message,
                                           expected.log,
                                           expected.time,
                                           {});
  executor.addTestResult(result);
  executor.sendTestResults();
  var resultPost = this.posts.pop();
  assertNotNull(resultPost);
  assertEquals(JSON.stringify([expected]), eval(resultPost.data.response).response);
};

CommandExecutorTest.prototype.testParseJsonAndRunTheRightMethod = function() {
  var executor = new jstestdriver.CommandExecutor(1, 
          this.streamingService,
          null,
          null,
          null);

  var command = {
    fooRang : false,
    'foo' : function(params) {
    this.fooRang = params;
    }
  };
  executor.registerCommand("foo", command, command.foo);
  executor.executeCommand(JSON.stringify({ 'command': 'foo', 'parameters': ['bar'] }));
  assertEquals('bar', command.fooRang[0]);
};


