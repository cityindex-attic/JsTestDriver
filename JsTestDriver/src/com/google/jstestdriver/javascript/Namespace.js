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
jstestdriver = {};

if (typeof console == 'undefined') console = {};
if (typeof console.log == 'undefined') console.log = function() {};
if (typeof console.debug == 'undefined') console.debug = function() {};
if (typeof console.info == 'undefined') console.info = function() {};
if (typeof console.warn == 'undefined') console.warn = function() {};
if (typeof console.error == 'undefined') console.error = function() {};

jstestdriver.globalSetTimeout = setTimeout;
jstestdriver.setTimeout = function() {
  return jstestdriver.globalSetTimeout.apply(window, arguments);
};

jstestdriver.globalClearTimeout = clearTimeout;
jstestdriver.clearTimeout = function() {
  return jstestdriver.globalClearTimeout.apply(window, arguments);
};

jstestdriver.globalSetInterval = setInterval;
jstestdriver.setInterval = function() {
  return jstestdriver.globalSetInterval.apply(window, arguments);
};

jstestdriver.globalClearInterval = clearInterval;
jstestdriver.clearInterval = function() {
  return jstestdriver.globalClearInterval.apply(window, arguments);
};
