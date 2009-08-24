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
  null);

  heartbeat.start();
  assertEquals("/heartbeat", url);
  assertEquals("1", data.id);
  assertNotNull(sendCallback);
  
  // test callback
  time = 30;
  url = null;
  data = null;
  sendCallback();
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
  assertTrue(Boolean(ele.className));
  assertEquals(jstestdriver.Heartbeat.ERROR_CLASS, ele.className);
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
  }, null);
  
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
