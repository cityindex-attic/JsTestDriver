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
var DefaultPluginTest = jstestdriver.testCaseManager.TestCase('DefaultPluginTest');

DefaultPluginTest.prototype.testCheckThatRightPluginIsCalled = function() {
  var fakeFileLoaderPlugin = new TestPlugin('defaultPlugin', true);
  var defaultPlugin = new jstestdriver.plugins.DefaultPlugin(fakeFileLoaderPlugin);
  var file = 'file.js';
  var callback = function() {};

  defaultPlugin.loadSource(file, callback);
  assertTrue(fakeFileLoaderPlugin.called);
  assertNotNull(fakeFileLoaderPlugin.file);
  assertEquals(file, fakeFileLoaderPlugin.file);
  assertNotNull(fakeFileLoaderPlugin.callback);
  assertEquals(callback, fakeFileLoaderPlugin.callback);
};
