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
jstestdriver.Heartbeat = function(id,
                                  url,
                                  mode,
                                  sendRequest,
                                  interval,
                                  setTimeout,
                                  getTime,
                                  view,
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
  this.view_ = view;
  this.navigateToPath_ = navigateToPath;
  this.capturePath_ = jstestdriver.Heartbeat.CAPTURE_PATH + "?" + mode;
};


jstestdriver.Heartbeat.CAPTURE_PATH = '/capture';

jstestdriver.Heartbeat.RETRY_LIMIT = 50;


jstestdriver.Heartbeat.prototype.start = function() {
  this.view_.updateConnected(true);
  this.sendHeartbeat();
};


jstestdriver.Heartbeat.prototype.stop = function() {
  jstestdriver.clearTimeout(this.timeoutId_);
  this.view_.updateStatus('Dead.');
};


jstestdriver.Heartbeat.prototype.sendHeartbeat = function() {
  this.sent_ = this.getTime_();
  this.sendRequest_(this.url_, { id: this.id_ },
                    this.boundHeartbeatCallback_,
                    this.boundErrorCallback_);
};


jstestdriver.Heartbeat.prototype.errorCallback = function() {
  this.view_.updateConnected(false);
  if (this.retries_ > jstestdriver.Heartbeat.RETRY_LIMIT) {
    this.stop();
    return;
  }
  this.retries_++;
  this.view_.updateStatus('Retrying ' +
      this.retries_ + ' out of ' + jstestdriver.Heartbeat.RETRY_LIMIT);
  this.timeoutId_ = this.setTimeout_(this.boundSendHeartBeat_, this.interval_);
}

// TODO(corysmith): convert response to json.
jstestdriver.Heartbeat.prototype.heartbeatCallback = function(response) {
  if (response == 'UNKNOWN') {
    this.navigateToPath_(this.capturePath_);
    return;
  }
  var elapsed = this.getTime_() - this.sent_;
  this.sent_ = 0;

  this.view_.updateStatus(response);

  if (elapsed < this.interval_) {
    this.timeoutId_ = this.setTimeout_(this.boundSendHeartBeat_,
        this.interval_ - elapsed);
  } else {
    this.sendHeartbeat();
  }
};



/**
 * Handles the rendering of the heartbeat monitor.
 * @param {function():HTMLBodyElement} Accessor for the body element of the page.
 * @param {function(String):HTMLElement} Element creator factory.
 */
jstestdriver.HeartbeatView = function(getBody, createElement) {
  this.getBody_ = getBody;
  this.createElement_ = createElement;
  this.status_ = null;
};


/**
 * Sets the status message for the heartbeat.
 * @param {String}
 */
jstestdriver.HeartbeatView.prototype.updateStatus = function(msg) {
  if (!this.status_) {
    var body = this.getBody_();
    var document = body.ownerDocument;
    var title = body.appendChild(document.createElement('p'));
    title.innerHTML = 'JsTestDriver:<br/>';
    this.status_ = title.appendChild(document.createElement('span'));
  }
  this.status_.innerHTML = msg;
};


/**
 * Renders the heartbeat connection status.
 * @param {boolean} The connection status.
 */
jstestdriver.HeartbeatView.prototype.updateConnected = function(connected) {
  if (!connected) {
    this.getBody_().className = jstestdriver.HeartbeatView.ERROR_CLASS;
    this.updateStatus('Lost server...');
  } else {
    this.getBody_().className = '';
    this.updateStatus('Waiting...');
  }
};


jstestdriver.HeartbeatView.ERROR_CLASS = 'error';
