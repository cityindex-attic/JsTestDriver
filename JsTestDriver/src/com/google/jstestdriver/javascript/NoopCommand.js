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
 * A command that does nothing but contact the server and return a response.
 * Used to retrieve new commands from the server.
 * 
 * @constructor
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
jstestdriver.NoopCommand = function(streamStop, getBrowserInfo) {
  /**
   * Function used to contact the server.
   * @type {function(jstestdriver.Response):null}
   */
  this.streamStop_ = streamStop;
  /**
   * Function used to retrieve the jstestdriver.BrowserInfo.
   * @type {function():jstestdriver.BrowserInfo}
   */
  this.streamStop_ = streamStop;
  this.getBrowserInfo_ = getBrowserInfo;
};

jstestdriver.NoopCommand.prototype.sendNoop = function() {
  
  this.streamStop_(
      new jstestdriver.Response(
          jstestdriver.RESPONSE_TYPES.NOOP,
          '{}',
          this.getBrowserInfo_()));
}

