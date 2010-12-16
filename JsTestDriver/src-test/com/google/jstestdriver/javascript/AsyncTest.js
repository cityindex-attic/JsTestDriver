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

  q.defer(function(pool) {
    // Execute the callback 1 second from now.
    window.setTimeout(pool.add(passingCallback), 250 /* ms */);
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
  // Notice that the deferred operation takes 'pool' as an argument.
  q.defer('A', function(pool) {
    assertEquals(0, state);

    window.setTimeout(pool.add(function() {
      state = 1;
    }), 250);
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
  q.defer('B', function(pool) {
    assertEquals(1, state);
    assertEquals('a', someMoreState);

    window.setTimeout(pool.add(function() {
      state = 2;
    }), 250);
    window.setTimeout(pool.add(function() {
      someMoreState = 'b';
    }), 500);
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


asyncTest.prototype.testNewNames = function(driver) {
  driver.call(function(callbacks) {
    window.setTimeout(callbacks.noop(), 250);
  });
  driver.call(function() {
    assertTrue(true);
  });
};


asyncTest.prototype.testRequest = function(queue) {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', jstestdriver.createPath(window.location.toString(),
      '/hello'));
  queue.defer(function(pool) {
    var headersReceived = pool.add(function(status) {
      assertEquals(200, status);
    });
    var bodyReceived = pool.add(function(body) {
      assertEquals('hello', body);
    });
    xhr.onreadystatechange = function() {
      if (xhr.readyState == 2) {
        headersReceived(xhr.status);
      } else if (xhr.readyState == 4) {
        bodyReceived(xhr.responseText);
      }
    };
    xhr.send(null);
  });
};


asyncTest.prototype.testWithNestedDeferredQueues = function(queue) {
  var state = 0;

  queue.defer(function(pool) {
    assertEquals(0, state);
    window.setTimeout(pool.add(function() {state = 1;}), 250);

    queue.defer(function(pool) {
      assertEquals(1, state);
      window.setTimeout(pool.add(function() {state = 2;}), 250);
    });
  });

  queue.defer(function() {
    assertEquals(2, state);
  });
};
