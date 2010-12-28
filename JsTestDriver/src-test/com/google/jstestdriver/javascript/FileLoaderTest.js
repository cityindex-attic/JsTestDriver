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
var FileLoaderTest = jstestdriver.testCaseManager.TestCase('FileLoaderTest');

FileLoaderTest.prototype.testNoFilesToLoad = function() {
  var fileLoader = new jstestdriver.FileLoader({}, function(res) {
    assertNotNull(res.loadedFiles);
    assertEquals(0, res.loadedFiles.length);
  });

  fileLoader.load([]);
};


FileLoaderTest.prototype.createScriptLoader = function(win, dom) {
  return new jstestdriver.plugins.ScriptLoader(win, dom, {
    updateLatestTestCase: function() {},
    removeTestCaseForFilename: function() {}
  }, jstestdriver.now);
};


FileLoaderTest.prototype.testFilesToLoad = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var head = mockDOM.createElement('head');
  var win = {};
  var scriptLoader = this.createScriptLoader(win, mockDOM);
  var stylesheetLoader = new jstestdriver.plugins.StylesheetLoader(win, mockDOM);
  var fileLoaderPlugin = new jstestdriver.plugins.FileLoaderPlugin(
      scriptLoader,
      stylesheetLoader,
      jstestdriver.now);
  var defaultPlugin = new jstestdriver.plugins.DefaultPlugin(fileLoaderPlugin);
  var pluginRegistrar = new jstestdriver.PluginRegistrar();
  pluginRegistrar.register(defaultPlugin);
  var fileLoader = new jstestdriver.FileLoader(pluginRegistrar, function(res) {
    var loadedFiles = res.loadedFiles;
    jstestdriver.console.debug(loadedFiles);

    assertNotNull(loadedFiles);
    assertEquals(6, loadedFiles.length);
    var fileResult1 = loadedFiles[0];
    var fileResult2 = loadedFiles[1];
    var fileResult3 = loadedFiles[2];
    var fileResult4 = loadedFiles[3];
    var fileResult5 = loadedFiles[4];
    var fileResult6 = loadedFiles[5];

    assertNotNull(fileResult1.file);
    assertNotNull(fileResult2.file);
    assertNotNull(fileResult3.file);
    assertNotNull(fileResult4.file);
    assertNotNull(fileResult5.file);
    assertNotNull(fileResult6.file);

    assertEquals('file1.js', fileResult1.file.fileSrc);
    assertEquals(42, fileResult1.file.timestamp);
    assertTrue(fileResult1.success);
    assertEquals('', fileResult1.message);

    assertEquals('file2.css', fileResult2.file.fileSrc);
    assertEquals(98, fileResult2.file.timestamp);
    assertTrue(fileResult2.success);
    assertEquals('', fileResult2.message);

    assertEquals('file3.js', fileResult3.file.fileSrc);
    assertEquals(22, fileResult3.file.timestamp);
    assertFalse(fileResult3.success);
    assertEquals('error loading file: file3.js:78: mooh', fileResult3.message);

    assertEquals('file4.js', fileResult4.file.fileSrc);
    assertEquals(674, fileResult4.file.timestamp);
    assertTrue(fileResult4.success);
    assertEquals('', fileResult4.message);
    
    assertEquals('file5.css', fileResult5.file.fileSrc);
    assertEquals(24, fileResult5.file.timestamp);
    assertFalse(fileResult5.success);
    assertEquals('error loading file: file5.css:82: meuh', fileResult5.message);
    
    assertEquals('file6.css', fileResult6.file.fileSrc);
    assertEquals(1675, fileResult6.file.timestamp);
    assertTrue(fileResult6.success);
    assertEquals('', fileResult6.message);
  }, jstestdriver.now);
  var files = [];

  files.push(new jstestdriver.FileSource('file1.js', 42));
  files.push(new jstestdriver.FileSource('file2.css', 98));
  files.push(new jstestdriver.FileSource('file3.js', 22));
  files.push(new jstestdriver.FileSource('file4.js', 674));
  files.push(new jstestdriver.FileSource('file5.css', 24));
  files.push(new jstestdriver.FileSource('file6.css', 1675));
  fileLoader.load(files);
  if (jstestdriver.jQuery.browser.opera) {
    head.childNodes[0].readyState = 'loaded';
    head.childNodes[0].onreadystatechange();
    head.childNodes[1].readyState = 'loaded';
    head.childNodes[1].onreadystatechange();
  } else {
    head.childNodes[0].onload();
    head.childNodes[1].onload();
  }
  head.childNodes[2].onerror('mooh', 'url', 78);
  if (jstestdriver.jQuery.browser.opera) {
    head.childNodes[2].readyState = 'loaded';
    head.childNodes[2].onreadystatechange();
  } else {
    head.childNodes[2].onload();
  }
  head.childNodes[3].readyState = 'loaded';
  head.childNodes[3].onreadystatechange();
  head.childNodes[4].onerror('meuh', 'url', 82);
  if (jstestdriver.jQuery.browser.opera) {
    head.childNodes[4].readyState = 'loaded';
    head.childNodes[4].onreadystatechange();
  } else {
    head.childNodes[4].onload();
  }
  head.childNodes[5].readyState = 'completed';
  head.childNodes[5].onreadystatechange();
};
