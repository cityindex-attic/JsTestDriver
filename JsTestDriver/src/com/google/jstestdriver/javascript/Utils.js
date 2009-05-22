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
jstestdriver.formatString = function() {
  var argsLength = arguments.length;
  var stringBuilder = [];
  var subPattern = new RegExp("%[sdifo]", "g");
  var i = 0;

  while (i < argsLength) {
    var arg = arguments[i];
    var match = subPattern.exec(arg);

    if (!match) {
      var currentArg = arg;

      if (typeof currentArg == 'object') {
        currentArg = JSON.stringify(currentArg);
      }
      if (stringBuilder.length > 0) {
        stringBuilder.push(" ");
      }
      stringBuilder.push(currentArg);
      i++;
    } else {
      var argsUsed = 1;
      var argIndex = i + 1;
      var finalStr = arg;

      do {
        // probably extremely inefficient :-/
        var currentArg = arguments[argIndex++];

        if (typeof currentArg == 'object') {
          currentArg = JSON.stringify(currentArg);
        }
        finalStr = finalStr.replace(match, currentArg);
        argsUsed++;
      } while ((match = subPattern.exec(finalStr)) != null);
      stringBuilder.push(finalStr);
      i += argsUsed;
    }
  }
  return stringBuilder.join('');
};


jstestdriver.convertToJson = function(delegate) {
  return function(url, data, callback) {
    var modifiedData = {};
    for (var key in data) {
      modifiedData[key] = JSON.stringify(data[key]);
    }
    delegate(url, modifiedData, callback);
  };
};


jstestdriver.bind = function(context, func) {
  return function() {
    return func.apply(context, arguments);
  };
};


jstestdriver.extractId = function(url) {
  return url.match(/\/slave\/(\d+)\//)[1];
};


jstestdriver.getBrowserFriendlyName = function() {
  if (jQuery.browser.safari) {
    if (navigator.userAgent.indexOf('Chrome') != -1) {
      return 'Chrome';
    }
    return 'Safari';
  } else if (jQuery.browser.opera) {
    return 'Opera';
  } else if (jQuery.browser.msie) {
    return 'Internet Explorer';
  } else if (jQuery.browser.mozilla) {
    if (navigator.userAgent.indexOf('Firefox') != -1) {
      return 'Firefox';
    }
    return 'Mozilla';
  }
};


jstestdriver.getBrowserFriendlyVersion = function() {
  if (jQuery.browser.msie) {
    if (typeof XDomainRequest != 'undefined') {
      return '8.0';
    } 
  } else if (jQuery.browser.safari) {
    if (navigator.appVersion.indexOf('Chrome/') != -1) {
      return navigator.appVersion.match(/Chrome\/(.*)\s/)[1];
    }
  }
  return jQuery.browser.version;
};
