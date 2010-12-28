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
var FileLoaderPluginCallbackTests = ConditionalTestCase(
    'FileLoaderPluginCallbackTests',
    function() {
  return !jstestdriver.jQuery.browser.opera;
});

FileLoaderPluginCallbackTests.prototype.createScriptLoader = function(win, dom) {
  return new jstestdriver.plugins.ScriptLoader(win, dom, {
    updateLatestTestCase: function() {},
    removeTestCaseForFilename: function() {}
  }, jstestdriver.now);
};


FileLoaderPluginCallbackTests.prototype.testFileOnLoadJs = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader,
      stylesheetLoader,
      jstestdriver.now);
  var file = new jstestdriver.FileSource('file.js', 12);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
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

FileLoaderPluginCallbackTests.prototype.testFileOnLoadJsError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader,
      stylesheetLoader);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
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


FileLoaderPluginCallbackTests.prototype.testFileOnLoadJsWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var win = {};
  var scriptLoader = this.createScriptLoader(win, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader(win, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader,
      stylesheetLoader);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
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

FileLoaderPluginCallbackTests.prototype.testFileOnLoadCss = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader,
      stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 24);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult = fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  link.onload();
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(24, callbackFileResult.file.timestamp);
};

FileLoaderPluginCallbackTests.prototype.testFileOnLoadCssError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader,
      stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 84);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  link.onerror('it sucks', 'url', 90);
  link.onload();
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(84, callbackFileResult.file.timestamp);
};

FileLoaderPluginCallbackTests.prototype.testFileOnLoadCssWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var win = {};
  var scriptLoader = this.createScriptLoader(win, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader(win, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader,
      stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 84);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertNotNull(win.onerror);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  win.onerror('it sucks', 'url', 90);
  link.onload();
  assertSame(jstestdriver.EMPTY_FUNC, win.onerror);
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(84, callbackFileResult.file.timestamp);
};

var FileLoaderPluginTest = TestCase('FileLoaderPluginTest');


FileLoaderPluginTest.prototype.createScriptLoader = function(win, dom) {
  return new jstestdriver.plugins.ScriptLoader(win, dom, {
    updateLatestTestCase: function() {},
    removeTestCaseForFilename: function() {}
  }, jstestdriver.now);
};


FileLoaderPluginTest.prototype.testFileOnReadyStateChangeJs = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.js', 12);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
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


FileLoaderPluginTest.prototype.testFileOnReadyStateChangeJsError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
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


FileLoaderPluginTest.prototype.testFileOnReadyStateChangeJsWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var win = {};
  var scriptLoader = this.createScriptLoader(win, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader(win, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.js', 42);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
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


FileLoaderPluginTest.prototype.testFileOnReadyStateChangeCss = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 24);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult = fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  link.readyState = 'loaded';
  link.onreadystatechange();
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(24, callbackFileResult.file.timestamp);
};


FileLoaderPluginTest.prototype.testFileLoadCssOnLoadHack = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, true);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 24);
  var callbackCalled = 0;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled++;
    callbackFileResult = fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertEquals(1, callbackCalled);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(24, callbackFileResult.file.timestamp);
};


FileLoaderPluginTest.prototype.testFileOnReadyStateChangeCssError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var scriptLoader = this.createScriptLoader({}, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 84);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  link.onerror('it sucks', 'url', 90);
  link.readyState = 'loaded';
  link.onreadystatechange();
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(84, callbackFileResult.file.timestamp);
};


FileLoaderPluginTest.prototype.testFileOnReadyStateChangeCssWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');

  var win = {};
  var scriptLoader = this.createScriptLoader(win, mockDOM);
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader(win, mockDOM, false);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(scriptLoader, stylesheetLoader);
  var file = new jstestdriver.FileSource('file.css', 84);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  fileLoaderPlugin.loadSource(file, callback);
  assertNotNull(win.onerror);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  win.onerror('it sucks', 'url', 90);
  link.readyState = 'loaded';
  link.onreadystatechange();
  assertSame(jstestdriver.EMPTY_FUNC, win.onerror);
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(84, callbackFileResult.file.timestamp);
};
