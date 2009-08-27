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
var heartbeatTest = jstestdriver.testCaseManager.TestCase('heartbeatTest');

heartbeatTest.prototype.testStartHeartbeat = function() {
  var callbackCalled = false;
  var url = null;
  var data = null;
  var sendCallback = null;
  var timeoutDuration = 0;
  var timeoutCallback = null;
  var time = 0;
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat", function(_url, _data, _callback) {
    url = _url;
    data = _data;
    sendCallback = _callback;
  },
  30,
  function(callback, duration){
    timeoutDuration = duration;
    timeoutCallback = callback;
  },
  function() {
    return time;
  },
  null, null);

  heartbeat.start();
  assertEquals("/heartbeat", url);
  assertEquals("1", data.id);
  assertNotNull(sendCallback);
  
  // test callback
  time = 30;
  url = null;
  data = null;
  sendCallback('OK');
  assertEquals("/heartbeat", url);
  assertEquals("1", data.id);
  assertNotNull(sendCallback);
};

heartbeatTest.prototype.testErrorCallback = function() {
  var callbackCalled = false;
  var errBack = null;
  var ele = document.createElement('div');
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat",
                                             function(_url, _data, _callback, _errback) {
    errBack = _errback;
  },
  30,
  function(callback, duration){},
  function() {
    return 0;
  }, function() {
    return ele;
  });
  
  heartbeat.start();
  assertNotNull(errBack);
  
  errBack();
  assertTrue(!!ele.className);
  assertEquals(jstestdriver.Heartbeat.ERROR_CLASS, ele.className);
};

heartbeatTest.prototype.testErrorStopRetryAfterLimit = function() {
  var errBack = null;
  var timeoutCallback = null
  var ele = document.createElement('div');
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat",
                                             function(_url, _data, _callback, _errback) {
    errBack = _errback;
  },
  30,
  function(callback, duration){
    timeoutDuration = duration;
    timeoutCallback = callback;
  },
  function() {
    return 0;
  }, function() {
    return ele;
  });
  
  heartbeat.start();
  assertNotNull(errBack);
  errBack();
  assertTrue(!!ele.className);
  assertEquals(jstestdriver.Heartbeat.ERROR_CLASS, ele.className);
  for (var i = 0; i < jstestdriver.Heartbeat.RETRY_LIMIT; i++) {
    errBack();
    timeoutCallback();
    assertNotNull(timeoutCallback);
  }
  timeoutCallback = null;
  errBack();
  assertNull(timeoutCallback);
};

heartbeatTest.prototype.testUnknownOnServer = function() {
  var callbackCalled = false;
  var sendCallback = null;
  var navigatePath = null;
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat",
                                             function(_url, _data, _callback, _errback) {
    sendCallback = _callback;
  },
  30,
  function(callback, duration){
    timeoutDuration = duration;
    timeoutCallback = callback;
  },
  function() {
    return 0;
  }, null,
  function (path){
    navigatePath = path;
  });
  
  heartbeat.start();
  assertNotNull(sendCallback);
  sendCallback('UNKNOWN');
  assertEquals(jstestdriver.Heartbeat.CAPTURE_PATH, navigatePath);
};

heartbeatTest.prototype.testHeartbeatCallbackFast = function() {
  var callbackCalled = false;
  var url = null;
  var data = null;
  var sendCallback = null;
  var timeoutDuration = 0;
  var timeoutCallback = null;
  var interval = 30;
  var time = 0;
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat", function(_url, _data, _callback) {
    url = _url;
    data = _data;
    sendCallback = _callback;
  },
  interval,
  function(callback, duration){
    timeoutDuration = duration;
    timeoutCallback = callback;
  },
  function() {
    return time;
  }, null, null);
  
  heartbeat.sendHeartbeat();
  assertEquals("/heartbeat", url);
  assertEquals("1", data.id);
  assertNotNull(sendCallback);
  
  // test callback
  time = 10; //comes back fast
  url = null;
  data = null;
  sendCallback();
  
  assertEquals(interval - time, timeoutDuration);
  assertNotNull(timeoutCallback);
  
  timeoutCallback();
  
  assertEquals("/heartbeat", url);
  assertEquals("1", data.id);
  assertNotNull(sendCallback);
};
