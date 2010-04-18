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
 * This is a simple logger implementation, that posts messages to the server.
 * Will most likely be expanded later.
 * @param {jstestdriver.NetService} netService Service for communicating with
 *     the server.
 * @param {jstestdriver.BrowserInfo} 
 */
jstestdriver.BrowserLogger = function(netService, browser) {
  this.netService_ = netService;
  this.browser_ = browser;
};


/**
 * Logs a message to the server.
 * @param {String} source The source of the log event.
 * @param {jstestdriver.BrowserLogger.LEVEL} level The level of the message.
 * @param {String} message The log message.
 */
jstestdriver.BrowserLogger.prototype.log = function(source, level, message) {
  this.netService_.stream('logs',
      new jstestdriver.BrowserLog(source, level, message, this.browser_));
};


/**
 * Acceptable logging levels.
 * @enum
 */
jstestdriver.BrowserLogger.LEVEL = {
  TRACE : 1,
  DEBUG : 2,
  INFO : 3,
  WARN : 4,
  ERROR : 5
};


/**
 * A log message.
 * Corresponds with the com.google.jstestdriver.protocol.BrowserLog.
 * @param {String} source
 * @param 
 */
jstestdriver.BrowserLog = function(source, level, message) {
  this.source = source;
  this.level = level;
  this.message = message;
};
