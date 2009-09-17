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


jstestdriver.Heartbeat = function(id,
                                  url,
                                  mode,
                                  sendRequest,
                                  interval,
                                  setTimeout,
                                  getTime,
                                  getBody,
                                  navigateToPath) {
  this.id_ = id;
  this.url_ = url;
  this.mode_ = mode;
  this.retries_ = 0;
  this.sendRequest_ = sendRequest;
  this.interval_ = interval;
  this.boundHeartbeatCallback_ = jstestdriver.bind(this, this.heartbeatCallback);
  this.boundSendHeartBeat_ = jstestdriver.bind(this, this.sendHeartbeat);
  this.boundErrorCallback_ = jstestdriver.bind(this, this.errorCallback);
  this.sent_ = 0;
  this.timeoutId_ = -1;
  this.setTimeout_ = setTimeout;
  this.getTime_ = getTime;
  this.getBody_ = getBody;
  this.navigateToPath_ = navigateToPath;
  this.capturePath_ = jstestdriver.Heartbeat.CAPTURE_PATH + "?" + mode;
};


jstestdriver.Heartbeat.ERROR_CLASS = 'error';

jstestdriver.Heartbeat.CAPTURE_PATH = '/capture';

jstestdriver.Heartbeat.RETRY_LIMIT = 50;


jstestdriver.Heartbeat.prototype.start = function() {
  this.sendHeartbeat();
};


jstestdriver.Heartbeat.prototype.stop = function() {
  jstestdriver.clearTimeout(this.timeoutId_);
};


jstestdriver.Heartbeat.prototype.sendHeartbeat = function() {
  this.sent_ = this.getTime_();
  this.sendRequest_(this.url_, { id: this.id_ },
                    this.boundHeartbeatCallback_,
                    this.boundErrorCallback_);
};


jstestdriver.Heartbeat.prototype.errorCallback = function() {
  this.getBody_().className = jstestdriver.Heartbeat.ERROR_CLASS;
  if (this.retries_ > jstestdriver.Heartbeat.RETRY_LIMIT) {
    this.stop();
    return;
  }
  this.retries_++;
  this.timeoutId_ = this.setTimeout_(this.boundSendHeartBeat_, this.interval_);
}


jstestdriver.Heartbeat.prototype.heartbeatCallback = function(response) {
  if (response == 'UNKNOWN') {
    this.navigateToPath_(this.capturePath_);
    return;
  }
  var elapsed = this.getTime_() - this.sent_;
  this.sent_ = 0;

  if (elapsed < this.interval_) {
    this.timeoutId_ = this.setTimeout_(this.boundSendHeartBeat_,
        this.interval_ - elapsed);
  } else {
    this.sendHeartbeat();
  }
};
