/*
 * Copyright 2011 Google Inc.
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
 * The first half of the angular json wrapper.
 * @author corbinrsmith@gmail.com (Cory Smith)
 */

jstestdriver.angular = (function (angular, JSON, jQuery) {
  angular = angular || {};
  var _null = null;
  var $null = 'null';
  var _undefined;
  var $undefined = 'undefined';
  var $function = 'function';
  
  // library functions for angular.
  var isNumber = function (obj) {
    return (typeof obj).toLowerCase() == 'number' || obj instanceof Number;
  };
  
  var isObject = function (obj) {
    return obj != null && (typeof obj).toLowerCase() == 'object';
  };

  var isString = function (obj) {
    return (typeof obj).toLowerCase() == 'string' || obj instanceof String;
  };

  var isArray = function (obj) {
    return obj instanceof Array;
  };

  var isFunction = function (obj) {
    return (typeof obj).toLowerCase() == 'function';
  }

  var isBoolean = function (obj) {
    return (typeof obj).toLowerCase() == 'boolean' || obj instanceof Boolean;
  };

  var isUndefined = function (obj) {
    return (typeof obj).toLowerCase() == 'undefined';
  };

  var isDate = function (obj) {
    return obj instanceof Date;
  };

  var forEach = function (coll, callback) {
    jQuery.each(coll, function (index, value){
      return callback(value, index);
    });
  }

  function includes(arr, obj) {
    for (var i = 0; i < arr.length; i++) {
      if (arr[i] === obj) {
        return true;
      }
    }
    return false;
  }

  // extracted from https://github.com/angular/angular.js/blob/master/src/filters.js
  // Lines 106..129, 
  function padNumber(num, digits, trim) {
    var neg = '';
    if (num < 0) {
      neg =  '-';
      num = -num;
    }
    num = '' + num;
    while(num.length < digits) num = '0' + num;
    if (trim)
      num = num.substr(num.length - digits);
    return neg + num;
  }
  
  // extracted from https://github.com/angular/angular.js/blob/master/src/apis.js
  // Lines 721..782, 
  var R_ISO8061_STR = /^(\d{4})-(\d\d)-(\d\d)(?:T(\d\d)(?:\:(\d\d)(?:\:(\d\d)(?:\.(\d{3}))?)?)?Z)?$/;

  angular['String'] = {
    'quote':function(string) {
      return '"' + string.replace(/\\/g, '\\\\').
                          replace(/"/g, '\\"').
                          replace(/\n/g, '\\n').
                          replace(/\f/g, '\\f').
                          replace(/\r/g, '\\r').
                          replace(/\t/g, '\\t').
                          replace(/\v/g, '\\v') +
               '"';
    },
    'quoteUnicode':function(string) {
      var str = angular['String']['quote'](string);
      var chars = [];
      for ( var i = 0; i < str.length; i++) {
        var ch = str.charCodeAt(i);
        if (ch < 128) {
          chars.push(str.charAt(i));
        } else {
          var encode = "000" + ch.toString(16);
          chars.push("\\u" + encode.substring(encode.length - 4));
        }
      }
      return chars.join('');
    },

    /**
     * Tries to convert input to date and if successful returns the date, otherwise returns the input.
     * @param {string} string
     * @return {(Date|string)}
     */
    'toDate':function(string){
      var match;
      if (isString(string) && (match = string.match(R_ISO8061_STR))){
        var date = new Date(0);
        date.setUTCFullYear(match[1], match[2] - 1, match[3]);
        date.setUTCHours(match[4]||0, match[5]||0, match[6]||0, match[7]||0);
        return date;
      }
      return string;
    }
  };

  angular['Date'] = {
      'toString':function(date){
        return !date ?
                  date :
                  date.toISOString ?
                    date.toISOString() :
                    padNumber(date.getUTCFullYear(), 4) + '-' +
                    padNumber(date.getUTCMonth() + 1, 2) + '-' +
                    padNumber(date.getUTCDate(), 2) + 'T' +
                    padNumber(date.getUTCHours(), 2) + ':' +
                    padNumber(date.getUTCMinutes(), 2) + ':' +
                    padNumber(date.getUTCSeconds(), 2) + '.' +
                    padNumber(date.getUTCMilliseconds(), 3) + 'Z';
      }
    };
  