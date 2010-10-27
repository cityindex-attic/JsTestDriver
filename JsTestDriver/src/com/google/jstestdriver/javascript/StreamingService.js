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

/**
 * Service for streaming information to the server.
 * @param {String} url The server url.
 * @param {function():Number} now Returns the current time in ms.
 * @param {function(String, Object, function():null)} post Posts to the server.
 * @param {function(String, Object)} synchPost Posts synchronously to the server.
 */
// TODO(corysmith): Separate the state from the service.
jstestdriver.StreamingService = function(url, now, post, synchPost) {
  this.url_ = url;
  this.now_ = now;
  this.post_ = post;
  this.activeResponses_ = {};
  this.completeFinalResponse = null;
  this.synchPost_ = synchPost;
};


jstestdriver.StreamingService.prototype.synchClose = function(response) {
  var data = new jstestdriver.CommandResponse(true, response);
  console.info("synch call");
  this.synchPost_(this.url_, data);
};


jstestdriver.StreamingService.prototype.stream = function(response, callback) {
  this.streamResponse(response, false, callback);
};


jstestdriver.StreamingService.prototype.streamResponse = function(response,
                                                                  done,
                                                                  callback) {
  var data = new jstestdriver.CommandResponse(done, response);
  if (!done && response != null) {
    data.responseId = this.now_();
    // no ack expected after the final response, and no ack expected on no reponse
    this.activeResponses_[data.responseId] = data;
  }
  this.post_(this.url_, data, callback, 'text/plain');
};


/**
 * Callback command for the stream acknowledge to a streamed responses.
 * @param {Array.<String>} A list of received ids for the currently open stream.
 */
jstestdriver.StreamingService.prototype.streamAcknowledged = function(recieved) {
  for (var i = 0; recieved && recieved[i]; i++) {
    if (this.activeResponses_[recieved[i]]) {
      // cut down on memory goof ups....
      this.activeResponses_[recieved[i]] = null;
      delete this.activeResponses_[recieved[i]];
    }
  }

  // TODO(corysmith): This causes a extra traffic on close, as the service tries
  // to verify the received responses. Setup a timeout to reduce the queries to 
  // the server.
  if (this.completeFinalResponse) {
    this.completeFinalResponse()
  }
};


/**
 * Closes the current streaming session, sending the final response after all
 * other Responses are finished.
 * @param {!jstestdriver.Response} finalResponse The final response to send.
 * @param {!Function} The callback when the post is finished.
 */
jstestdriver.StreamingService.prototype.close =
    function(finalResponse, callback) {
  var context = this;
  this.completeFinalResponse = function() {
    if (context.hasOpenResponses()) {
      // have to query again, because these may be lost responses from a debug session.
      context.streamResponse(null, false, callback);
    } else {
      context.completeFinalResponse = null;
      context.activeResponses_ = {};
      context.streamResponse(finalResponse, true, callback);
    }
  };

  this.completeFinalResponse();
};


/**
 * Indicates if there are currently open streamed response.
 * @return {Boolean} True for open responses, otherwise false.
 */
jstestdriver.StreamingService.prototype.hasOpenResponses = function() {
  for (var responseId in this.activeResponses_) {
    if (this.activeResponses_.hasOwnProperty(responseId)) {
      return true;
    }
  }
  return false;
};