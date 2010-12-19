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
 * Watches for unauthorized unload events, such as a script calling reload,
 * or navigating to another page.
 *
 * @constructor
 * @param {jstestdriver.StreamingService} streamingService
 *     Used for server communication.
 * @param {function():jstestdriver.BrowserInfo} getBrowserInfo A function that 
 *     returns the browser information.
 * @param {function():String} getCommand Provides current command.
 * @param {jstestdriver.Signal} unloadSignal Signals if the unload command is expected.
 */
jstestdriver.PageUnloadHandler =
    function(streamingService, getBrowserInfo, getCommand, unloadSignal) {
  this.streamingService_ = streamingService;
  this.getBrowserInfo_ = getBrowserInfo;
  this.getCommand_ = getCommand
  this.unloadSignal_ = unloadSignal;
};


jstestdriver.PageUnloadHandler.prototype.onUnload = function(e) {
  if (!this.unloadSignal_.get()) {
    var type;
    try {
      type = e.type;
    } catch (e) {
      type = '[error while trying to get event type: ' + e + ']';
    }
    this.streamingService_.synchClose(
        new jstestdriver.Response(
            jstestdriver.RESPONSE_TYPES.BROWSER_PANIC,
            "Page reloaded unexpectedly during or after " + this.getCommand_() +
            " triggered by " + type,
            this.getBrowserInfo_(),
            false));
  }
};
