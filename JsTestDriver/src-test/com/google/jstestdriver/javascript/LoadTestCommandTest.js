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

var LoadTestsCommandTest = TestCase('LoadTestsCommandTest');

LoadTestsCommandTest.prototype.testRemoveScriptTags = function() {
  function getBrowserInfo() {
    return new jstestdriver.BrowserInfo(1);
  }

  var command =
      new jstestdriver.LoadTestsCommand(null, null, getBrowserInfo, null);
  var files = [];
  var dom = new jstestdriver.MockDOM();
  var head = dom.createElement('head');
  var script = dom.createElement('script');

  script.src = "file1";
  head.appendChild(script);
  files.push({fileSrc: "file1", timestamp: 42});
  command.removeScripts(dom, files);
  assertEquals(0, head.childNodes.length);
};