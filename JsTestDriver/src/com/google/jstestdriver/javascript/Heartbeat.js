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
jstestdriver.HEARTBEAT_URL = "/heartbeat";


jstestdriver.Heartbeat = function(id, url, sendRequest, interval) {
  this.id_ = id;
  this.url_ = url;
  this.sendRequest_ = sendRequest;
  this.interval_ = interval;
  this.boundHeartbeatCallback_ = jstestdriver.bind(this, this.heartbeatCallback);
  this.sent_ = 0;
  this.timeoutId_ = -1;
  jstestdriver.heartbeat = this;
};


jstestdriver.Heartbeat.prototype.start = function() {
  this.sendHeartbeat();
};


jstestdriver.Heartbeat.prototype.stop = function() {
  jstestdriver.clearTimeout(this.timeoutId_);
};


jstestdriver.Heartbeat.prototype.sendHeartbeat = function() {
  this.sent_ = new Date().getTime();
  this.sendRequest_(this.url_, { id: this.id_ }, this.boundHeartbeatCallback_);
};


jstestdriver.Heartbeat.prototype.heartbeatCallback = function() {
  var elapsed = new Date().getTime() - this.sent_;
  this.sent_ = 0;

  if (elapsed < this.interval_) {
    this.timeoutId_ = jstestdriver.setTimeout('jstestdriver.heartbeat.sendHeartbeat()',
        this.interval_ - elapsed);
  } else {
    this.sendHeartbeat();
  }
};
