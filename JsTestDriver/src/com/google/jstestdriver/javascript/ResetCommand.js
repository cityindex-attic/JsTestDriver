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
 * Resets the javascript state by reloading or replacing current window.
 * @param {window.location} location The location object.
 * @param {jstestdriver.Signal} signal Signals that the window will be reloaded.
 */
jstestdriver.ResetCommand = function(location, signal) {
  /**
   * @type {window.location}
   * @private
   */
  this.location_ = location;

  /**
   * @type {window.location}
   * @private
   */
  this.signal_ = signal;
};

jstestdriver.ResetCommand.prototype.reset = function() {
  this.signal_.set(true);
  if (this.location_.href.search('\\?refresh') != -1) {
    this.location_.reload();
  } else {
    this.location_.replace(this.location_.href + '?refresh');
  }
};