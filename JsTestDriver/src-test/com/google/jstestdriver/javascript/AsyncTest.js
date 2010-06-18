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

var asyncTest = AsyncTestCase('asyncTest');


asyncTest.prototype.testWindowSetTimeout = function(q) {
  var state = false;

  var passingCallback = function() {
    // This code executes later.
    assertTrue(state);
  };

  var unusedFailingCallback = function() {
    // This code executes later.
    assertFalse(state);
  };

  q.defer(function(herd) {
    // Execute the callback 1 second from now.
    window.setTimeout(herd.add(passingCallback), 1000 /* ms */);
  });

  // This code executes instantly.
  assertFalse(state);
  state = true;
};


asyncTest.prototype.testSeriesOfAsyncSteps = function(q) {
  // Some state variables to change and test
  var state = 0;
  var someMoreState = 'a';

  // Add the first deferred operation to the queue.
  // This operation checks that 'state' is zero, then it schedules
  // a callback to change 'state' to one after a one-second delay.
  // Notice that the deferred operation takes 'herd' as an argument.
  q.defer('A', function(herd) {
    assertEquals(0, state);

    window.setTimeout(herd.add(function() {
      state = 1;
    }), 1000);
  });

  // Add the second deferred operation to the queue.
  // This operation verifies that the previous asynchronous callback
  // set 'state' to one and 'someMoreState' to 'a'.
  // Then it schedules two asynchronous callbacks to run in
  // parallel.
  // The first callback executes after a one-second delay and sets
  // 'state' to two.
  // The second callback executes after a two-second delay and sets
  // 'someMoreState' to 'b'.
  q.defer('B', function(herd) {
    assertEquals(1, state);
    assertEquals('a', someMoreState);

    window.setTimeout(herd.add(function() {
      state = 2;
    }), 1000);
    window.setTimeout(herd.add(function() {
      someMoreState = 'b';
    }), 2000);
  });

  // Add the third deferred operation to the queue.
  // This operation verifies that the previous asynchronous
  // callbacks set 'state' to two and 'someMoreState' to 'b'.
  q.defer('C', function() {
    assertEquals(2, state);
    assertEquals('b', someMoreState);
    state = 3;
    someMoreState = 'c';
  });

  q.defer('D', function() {
    assertEquals(3, state);
    assertEquals('c', someMoreState);
  });
};
