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
var fileLoaderTest = jstestdriver.testCaseManager.TestCase('fileLoaderTest');

fileLoaderTest.prototype.testNoFilesToLoad = function() {
  var mockDOM = new jstestdriver.MockDOM();
  var fileLoader = new jstestdriver.FileLoader(mockDOM);

  fileLoader.load([], function(res) {
    assertEquals('no files to load', res.message);
    assertEquals(0, res.successFiles.length);
    assertEquals(0, res.errorFiles.length);
  });
};
