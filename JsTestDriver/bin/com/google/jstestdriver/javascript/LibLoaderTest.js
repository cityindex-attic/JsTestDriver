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
var libLoaderTest = jstestdriver.testCaseManager.TestCase('libLoaderTest');

libLoaderTest.prototype.testAllScriptLoadedExecuteCallback = function() {
  var numberCalled = 0;
  var dom = new jstestdriver.MockDOM();
  var libLoader = new jstestdriver.LibLoader(new Array('lib1', 'lib2'), dom,
      function(dom_, url_, callback_) {
        assertNotNull(dom_);
        assertNotNull(url_);
        assertNotNull(callback_);
        numberCalled++;
        callback_();
      });
  var called = false;
  var data;

  libLoader.load(function(data_) {
    data = data_;
    called = true;
  }, "data");
  assertEquals(2, numberCalled);
  assertTrue(called);
  assertEquals("data", data);
};


