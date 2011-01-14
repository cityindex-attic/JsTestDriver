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


heartbeatTest.prototype.getView = function(connected) {
  return {
    status : '',
    connected : connected,
    lastBeat: -1,
    updateStatus : function(status) {
      this.status = status;
    },
    updateConnected : function(connected) {
      this.connected = connected; 
    },
    updateLastBeat : function(lastBeat) {
      this.lastBeat = lastBeat;
    },
    updateNextBeat : function(nextBeat) {
      this.nextBeat = nextBeat;
    }
  };
};


heartbeatTest.prototype.testStartHeartbeat = function() {
  var callbackCalled = false;
  var url = null;
  var data = null;
  var sendCallback = null;
  var timeoutDuration = 0;
  var timeoutCallback = null;
  var time = 0;
  var view = this.getView(false);
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat", "quirks",
    function(_url, _data, _callback) {
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
  view, null);

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
  var view = this.getView(true);
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat", "quirks",
                                             function(_url, _data, _callback, _errback) {
    errBack = _errback;
  },
  30,
  function(callback, duration){},
  function() {
    return 0;
  },view, null);

  heartbeat.start();
  assertNotNull(errBack);
  
  errBack();
  assertFalse(view.connected);
};

heartbeatTest.prototype.testErrorStopRetryAfterLimit = function() {
  var errBack = null;
  var timeoutCallback = null
  var view = this.getView(true);
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat", "quirks",
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
  }, view, null);
  
  heartbeat.start();
  assertNotNull(errBack);
  errBack();
  assertFalse(view.connected);
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
  var capturePath = "/capture";
  var view = this.getView(true);
  var heartbeat = new jstestdriver.Heartbeat(
      "1",
      "/heartbeat",
      capturePath, 
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
  },
  view,
  function (path){
    navigatePath = path;
  });
  
  heartbeat.start();
  assertNotNull(sendCallback);
  sendCallback('UNKNOWN');
  assertEquals(capturePath, navigatePath);
};

heartbeatTest.prototype.testUnknownOnServerStrict = function() {
  var callbackCalled = false;
  var sendCallback = null;
  var navigatePath = null;
  var capturePath = "/capture";
  var view = this.getView();
  var heartbeat = new jstestdriver.Heartbeat(
      "1",
      "/heartbeat",
      capturePath, 
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
  }, view,
  function (path){
    navigatePath = path;
  });
  
  heartbeat.start();
  assertNotNull(sendCallback);
  sendCallback('UNKNOWN');
  assertEquals(capturePath, navigatePath);
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
  var view = this.getView(true);
  var heartbeat = new jstestdriver.Heartbeat("1", "/heartbeat", "quirks",
    function(_url, _data, _callback) {
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
  }, view, null);
  
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
