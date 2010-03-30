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
var StylesheetLoaderTest = jstestdriver.testCaseManager.TestCase('StylesheetLoaderTest');

if (!jstestdriver.jQuery.browser.opera) {
  StylesheetLoaderTest.prototype.testOnLoad = function() {
    var mockDOM = new jstestdriver.MockDOM();
    var head = mockDOM.createElement('head');
    var write = function() {};

    mockDOM.write = write;
    var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
    var file = new jstestdriver.FileSource('file.css', 24);
    var callbackCalled = false;
    var callbackFileResult = null;
    var callback = function(fileResult) {
      callbackCalled = true;
      callbackFileResult = fileResult;
    };

    stylesheetLoader.load(file, callback);
    assertSame(write, mockDOM.write); // write is not overriden for CSS files
    assertEquals(1, head.childNodes.length);
    var link = head.childNodes[0];

    link.onload();
    assertEquals('link', link.nodeName);
    assertEquals('text/css', link.type);
    assertEquals('stylesheet', link.rel);
    assertEquals('file.css', link.href);
    assertTrue(callbackCalled);
    assertSame(write, mockDOM.write);
    assertNotNull(callbackFileResult);
    assertTrue(callbackFileResult.success);
    assertEquals('', callbackFileResult.message);
    assertNotNull(callbackFileResult.file);
    assertEquals('file.css', callbackFileResult.file.fileSrc);
    assertEquals(24, callbackFileResult.file.timestamp);
  };

  StylesheetLoaderTest.prototype.testOnLoadError = function() {
    var mockDOM = new jstestdriver.MockDOM();
    var head = mockDOM.createElement('head');
    var write = function() {};

    mockDOM.write = write;
    var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
    var file = new jstestdriver.FileSource('file.css', 84);
    var callbackCalled = false;
    var callbackFileResult = null;
    var callback = function(fileResult) {
      callbackCalled = true;
      callbackFileResult =  fileResult;
    };

    stylesheetLoader.load(file, callback);
    assertSame(write, mockDOM.write); // write is not overriden for CSS files
    assertEquals(1, head.childNodes.length);
    var link = head.childNodes[0];

    link.onerror('it sucks', 'url', 90);
    link.onload();
    assertEquals('link', link.nodeName);
    assertEquals('text/css', link.type);
    assertEquals('stylesheet', link.rel);
    assertEquals('file.css', link.href);
    assertTrue(callbackCalled);
    assertSame(write, mockDOM.write);
    assertNotNull(callbackFileResult);
    assertFalse(callbackFileResult.success);
    assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
    assertNotNull(callbackFileResult.file);
    assertEquals('file.css', callbackFileResult.file.fileSrc);
    assertEquals(84, callbackFileResult.file.timestamp);
  };


  StylesheetLoaderTest.prototype.testOnLoadWindowError = function() {
    var mockDOM = new jstestdriver.MockDOM();
    var head = mockDOM.createElement('head');
    var write = function() {};

    mockDOM.write = write;
    var win = {};
    var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader(win, mockDOM, false);
    var file = new jstestdriver.FileSource('file.css', 84);
    var callbackCalled = false;
    var callbackFileResult = null;
    var callback = function(fileResult) {
      callbackCalled = true;
      callbackFileResult =  fileResult;
    };

    stylesheetLoader.load(file, callback);
    assertNotNull(win.onerror);
    assertSame(write, mockDOM.write); // write is not overriden for CSS files
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
    assertSame(write, mockDOM.write);
    assertNotNull(callbackFileResult);
    assertFalse(callbackFileResult.success);
    assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
    assertNotNull(callbackFileResult.file);
    assertEquals('file.css', callbackFileResult.file.fileSrc);
    assertEquals(84, callbackFileResult.file.timestamp);
  };
}


StylesheetLoaderTest.prototype.testOnReadyStateChange = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var write = function() {};

  mockDOM.write = write;
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var file = new jstestdriver.FileSource('file.css', 24);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult = fileResult;
  };

  stylesheetLoader.load(file, callback);
  assertSame(write, mockDOM.write); // write is not overriden for CSS files
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  link.readyState = 'loaded';
  link.onreadystatechange();
  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertTrue(callbackCalled);
  assertSame(write, mockDOM.write);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(24, callbackFileResult.file.timestamp);
};


StylesheetLoaderTest.prototype.testOnSynchronousLoad = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var write = function() {};

  mockDOM.write = write;
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, true);
  var file = new jstestdriver.FileSource('file.css', 24);
  var callbackCalled = 0;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled++;
    callbackFileResult = fileResult;
  };

  stylesheetLoader.load(file, callback);
  assertEquals(1, head.childNodes.length);
  var link = head.childNodes[0];

  assertEquals('link', link.nodeName);
  assertEquals('text/css', link.type);
  assertEquals('stylesheet', link.rel);
  assertEquals('file.css', link.href);
  assertEquals(1, callbackCalled);
  assertSame(write, mockDOM.write);
  assertNotNull(callbackFileResult);
  assertTrue(callbackFileResult.success);
  assertEquals('', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(24, callbackFileResult.file.timestamp);
};


StylesheetLoaderTest.prototype.testOnReadyStateChangeError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var write = function() {};

  mockDOM.write = write;
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader({}, mockDOM, false);
  var file = new jstestdriver.FileSource('file.css', 84);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  stylesheetLoader.load(file, callback);
  assertSame(write, mockDOM.write); // write is not overriden for CSS files
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
  assertSame(write, mockDOM.write);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(84, callbackFileResult.file.timestamp);
};


StylesheetLoaderTest.prototype.testOnReadyStateChangeWindowError = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var write = function() {};

  mockDOM.write = write;
  var win = {};
  var stylesheetLoader =  new jstestdriver.plugins.StylesheetLoader(win, mockDOM, false);
  var file = new jstestdriver.FileSource('file.css', 84);
  var callbackCalled = false;
  var callbackFileResult = null;
  var callback = function(fileResult) {
    callbackCalled = true;
    callbackFileResult =  fileResult;
  };

  stylesheetLoader.load(file, callback);
  assertNotNull(win.onerror);
  assertSame(write, mockDOM.write); // write is not overriden for CSS files
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
  assertSame(write, mockDOM.write);
  assertNotNull(callbackFileResult);
  assertFalse(callbackFileResult.success);
  assertEquals('error loading file: file.css:90: it sucks', callbackFileResult.message);
  assertNotNull(callbackFileResult.file);
  assertEquals('file.css', callbackFileResult.file.fileSrc);
  assertEquals(84, callbackFileResult.file.timestamp);
};
