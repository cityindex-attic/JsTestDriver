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
                                  capturePath,
                                  sendRequest,
                                  interval,
                                  setTimeout,
                                  getTime,
                                  view,
                                  navigateToPath) {
  this.id_ = id;
  this.url_ = url;
  this.capturePath_ = capturePath;
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
};


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
  this.view_.updateLastBeat(this.sent_);
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
    var interval = this.interval_ - (elapsed > 0 ? elapsed : 0);
    this.view_.updateNextBeat(interval);
    this.timeoutId_ = this.setTimeout_(this.boundSendHeartBeat_,
        interval);
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
  this.root_ = null;
  this.status_ = null;
  this.lastBeat_ = null;
  this.nextBeat_ = null;
};


jstestdriver.HeartbeatView.prototype.ensureView = function() {
  if (!this.root_) {
    var body = this.getBody_();
    var document = body.ownerDocument;
    this.root_ = body.appendChild(document.createElement('p'));
    this.root_.innerHTML = 'JsTestDriver<br/>';
    this.lastBeat_ = this.root_.appendChild(document.createElement('span'));
    this.root_.appendChild(document.createTextNode(" | "));
    this.nextBeat_ = this.root_.appendChild(document.createElement('span'));
    this.root_.appendChild(document.createTextNode(" | "));
    this.status_ = this.root_.appendChild(document.createElement('span'));
  }
};


/**
 * Sets the status message for the last heartbeat.
 * @param {String}
 */
jstestdriver.HeartbeatView.prototype.updateLastBeat = function(msg) {
  this.ensureView();
  this.lastBeat_.innerHTML = ' Last:' + msg;
};


/**
 * Sets the status message for the next heartbeat.
 * @param {String}
 */
jstestdriver.HeartbeatView.prototype.updateNextBeat = function(msg) {
  this.ensureView();
  this.nextBeat_.innerHTML = ' Next:' + msg;
};


/**
 * Sets the status message for the heartbeat.
 * @param {String}
 */
jstestdriver.HeartbeatView.prototype.updateStatus = function(msg) {
  this.ensureView();
  this.status_.innerHTML = ' Server:' + msg;
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
