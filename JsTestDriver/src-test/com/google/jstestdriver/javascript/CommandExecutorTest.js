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
  assertEquals(1, jstestdriver.extractId("http://server:123/slave/id/1/page/RUNNER"));
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
  function getBrowserInfo() {
    return new jstestdriver.BrowserInfo(1);
  }

  var executor = new jstestdriver.CommandExecutor(this.streamingService,
          null, null, null, jstestdriver.now, getBrowserInfo);
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
  function getBrowserInfo() {
    return new jstestdriver.BrowserInfo(1);
  }

  var url;
  var data;
  var callback;
  var called = 0;
  var executor = new jstestdriver.CommandExecutor(this.streamingService,
                                                  null,
                                                  null,
                                                  null,
                                                  jstestdriver.now,
                                                  getBrowserInfo);
  executor.registerCommand('noop', this.streamingService, this.streamingService.close);
  executor.listen();
  var listenPost = this.posts.pop();
  listenPost.callback('{"command": "noop"}');
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
  function getBrowserInfo() {
    return new jstestdriver.BrowserInfo(1);
  }

  var executor = new jstestdriver.CommandExecutor(this.streamingService,
                                                  null,
                                                  null,
                                                  null,
                                                  jstestdriver.now,
                                                  getBrowserInfo);
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



CommandExecutorTest.prototype.testParseJsonAndRunTheRightMethod = function() {
  function getBrowserInfo() {
    return new jstestdriver.BrowserInfo(1);
  }

  var executor = new jstestdriver.CommandExecutor(this.streamingService,
          null,
          null,
          null,
          jstestdriver.now,
          getBrowserInfo);

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


