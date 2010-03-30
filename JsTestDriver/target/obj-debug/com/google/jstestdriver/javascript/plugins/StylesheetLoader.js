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
jstestdriver.plugins.StylesheetLoader = function(win, dom, synchronousCallback) {
  this.win_ = win;
  this.dom_ = dom;
  this.synchronousCallback_ = synchronousCallback;
};


jstestdriver.plugins.StylesheetLoader.prototype.load = function(file, callback) {
  this.fileResult_ = null;
  var head = this.dom_.getElementsByTagName('head')[0];
  var link = this.dom_.createElement('link');
  var handleError = jstestdriver.bind(this, function(msg, url, line) {
    var loadMsg = 'error loading file: ' + file.fileSrc;

    if (line != undefined && line != null) {
      loadMsg += ':' + line;
    }
    if (msg != undefined && msg != null) {
      loadMsg += ': ' + msg;
    }
    this.updateResult_(new jstestdriver.FileResult(file, false, loadMsg));
  });

  this.win_.onerror = handleError;
  link.onerror = handleError;
  if (!jstestdriver.jQuery.browser.opera) {
    link.onload = jstestdriver.bind(this, function() {
      this.onLoad_(file, callback);
    });
  }
  link.onreadystatechange = jstestdriver.bind(this, function() {
    if (link.readyState == 'loaded') {
      this.onLoad_(file, callback);
    }
  });
  link.type = "text/css";
  link.rel = "stylesheet";
  link.href = file.fileSrc;
  head.appendChild(link);

  // Firefox and Safari don't seem to support onload or onreadystatechange for link
  if (this.synchronousCallback_) {
    this.onLoad_(file, callback);
  }
};


jstestdriver.plugins.StylesheetLoader.prototype.onLoad_ = function(file, callback) {
  this.updateResult_(new jstestdriver.FileResult(file, true, ''));
  this.win_.onerror = jstestdriver.EMPTY_FUNC;
  callback(this.fileResult_);  
};


jstestdriver.plugins.StylesheetLoader.prototype.updateResult_ = function(fileResult) {
  if (this.fileResult_ == null) {
    this.fileResult_ = fileResult;
  }
};
