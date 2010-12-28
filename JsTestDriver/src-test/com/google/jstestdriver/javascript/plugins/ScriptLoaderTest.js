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
var NoOperaScriptLoaderTest = ConditionalTestCase('NoOperaScriptLoaderTest',
    function(){
  return !jstestdriver.jQuery.browser.opera;
});


NoOperaScriptLoaderTest.prototype.now = function() {
  return 1;
};


NoOperaScriptLoaderTest.prototype.testOnLoad = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = new jstestdriver.plugins.ScriptLoader({}, mockDOM, {
    updateLatestTestCase: function() {},
    removeTestCaseForFilename: function() {}
  }, this.now);
  var file = new jstestdriver.FileSource('file.js', 12);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  scriptLoader.load(file, callback);
  assertEquals(1, head.childNodes.length);
  var script = head.childNodes[0];

  script.onload();
  assertEquals('script', script.nodeName);
  assertEquals('text/javascript', script.type);
  assertEquals('file.js', script.src);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.js', callbackFileResult.file.fileSrc);
  assertEquals(12, callbackFileResult.file.timestamp);
};

NoOperaScriptLoaderTest.prototype.testOnLoadError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = new jstestdriver.plugins.ScriptLoader({}, mockDOM, {
    testCaseAdded: function() {
      return false
    },
    removeTestCaseForFilename: function() {}
  }, this.now);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  scriptLoader.load(file, callback);
  assertEquals(1, head.childNodes.length);
  var script = head.childNodes[0];

  script.onerror('msg', 'url', 42);
  script.onload();
  assertEquals('script', script.nodeName);
  assertEquals('text/javascript', script.type);
  assertEquals('file.js', script.src);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.js:42: msg', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.js', callbackFileResult.file.fileSrc);
  assertEquals(42, callbackFileResult.file.timestamp);
};


NoOperaScriptLoaderTest.prototype.testOnLoadWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var win = {};
  var scriptLoader = new jstestdriver.plugins.ScriptLoader(win, mockDOM, {
    testCaseAdded: function() {
      return false
    },
    removeTestCaseForFilename: function() {}
  }, this.now);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  scriptLoader.load(file, callback);
  assertNotNull(win.onerror);
  assertEquals(1, head.childNodes.length);
  var script = head.childNodes[0];

  win.onerror('msg', 'url', 42);
  script.onload();
  assertSame(jstestdriver.EMPTY_FUNC, win.onerror);
  assertEquals('script', script.nodeName);
  assertEquals('text/javascript', script.type);
  assertEquals('file.js', script.src);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.js:42: msg', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.js', callbackFileResult.file.fileSrc);
  assertEquals(42, callbackFileResult.file.timestamp);
};

var ScriptLoaderTest = TestCase('ScriptLoaderTest');

ScriptLoaderTest.prototype.now = function() {
  return 1;
};

ScriptLoaderTest.prototype.testOnReadyStateChange = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = new jstestdriver.plugins.ScriptLoader({}, mockDOM, {
    updateLatestTestCase: function() {},
    removeTestCaseForFilename: function() {}
  }, this.now);
  var file = new jstestdriver.FileSource('file.js', 12);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  scriptLoader.load(file, callback);
  assertEquals(1, head.childNodes.length);
  var script = head.childNodes[0];

  script.readyState = 'loaded';
  script.onreadystatechange();
  assertEquals('script', script.nodeName);
  assertEquals('text/javascript', script.type);
  assertEquals('file.js', script.src);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.js', callbackFileResult.file.fileSrc);
  assertEquals(12, callbackFileResult.file.timestamp);
};


ScriptLoaderTest.prototype.testOnReadyStateChangeError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = new jstestdriver.plugins.ScriptLoader({}, mockDOM, {
    testCaseAdded: function() {
      return false
    },
    removeTestCaseForFilename: function() {}
  }, this.now);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  scriptLoader.load(file, callback);
  assertEquals(1, head.childNodes.length);
  var script = head.childNodes[0];

  script.onerror('msg', 'url', 42);
  script.readyState = 'loaded';
  script.onreadystatechange();
  assertEquals('script', script.nodeName);
  assertEquals('text/javascript', script.type);
  assertEquals('file.js', script.src);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.js:42: msg', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.js', callbackFileResult.file.fileSrc);
  assertEquals(42, callbackFileResult.file.timestamp);
};


ScriptLoaderTest.prototype.testOnReadyStateChangeWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var win = {};
  var scriptLoader = new jstestdriver.plugins.ScriptLoader(win, mockDOM, {
    testCaseAdded: function() {
      return false
    },
    removeTestCaseForFilename: function() {}
  }, this.now);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  scriptLoader.load(file, callback);
  assertNotNull(win.onerror);
  assertEquals(1, head.childNodes.length);
  var script = head.childNodes[0];

  win.onerror('msg', 'url', 42);
  script.readyState = 'loaded';
  script.onreadystatechange();
  assertSame(jstestdriver.EMPTY_FUNC, win.onerror);
  assertEquals('script', script.nodeName);
  assertEquals('text/javascript', script.type);
  assertEquals('file.js', script.src);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.js:42: msg', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.js', callbackFileResult.file.fileSrc);
  assertEquals(42, callbackFileResult.file.timestamp);
};
