/*
 * Copyright 2010 Google Inc.
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

var xhrTest = AsyncTestCase('xhrTest');


xhrTest.prototype.testWindowSetTimeout = function(herd) {
  var state = false;

  var passingCallback = function() {
    // This code executes later.
    assertTrue(state);
  };

  var unusedFailingCallback = function() {
    // This code executes later.
    assertFalse(state);
  };

  // Execute the callback 1 second from now.
  window.setTimeout(herd.add(passingCallback), 1000 /* ms */);

  // This code executes instantly.
  assertFalse(state);
  state = true;
};
