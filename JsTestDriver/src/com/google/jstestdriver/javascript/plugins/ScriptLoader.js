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
jstestdriver.plugins.ScriptLoader = function(win, dom) {
  this.win_ = win;
  this.dom_ = dom;
  this.originalDocumentWrite_ = this.dom_.write;
};


jstestdriver.plugins.ScriptLoader.prototype.load = function(file, callback) {
  this.fileResult_ = null;
  var head = this.dom_.getElementsByTagName('head')[0];
  var script = this.dom_.createElement('script');
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
  script.onerror = handleError;
  if (!jstestdriver.jQuery.browser.opera) {
    script.onload = jstestdriver.bind(this, function() {
      this.onLoad_(file, callback);
    });
  }
  script.onreadystatechange = jstestdriver.bind(this, function() {
    if (script.readyState == 'loaded') {
      this.onLoad_(file, callback);
    }
  });
  script.type = "text/javascript";
  script.src = file.fileSrc;
  this.disableDocumentWrite();
  head.appendChild(script);
};


jstestdriver.plugins.ScriptLoader.prototype.onLoad_ = function(file, callback) {
  this.updateResult_(new jstestdriver.FileResult(file, true, ''));
  this.win_.onerror = jstestdriver.EMPTY_FUNC;
  this.restoreDocumentWrite();
  callback(this.fileResult_);
};


jstestdriver.plugins.ScriptLoader.prototype.updateResult_ = function(fileResult) {
  if (this.fileResult_ == null) {
    this.fileResult_ = fileResult;
  }
};


jstestdriver.plugins.ScriptLoader.prototype.disableDocumentWrite = function() {
  this.dom_.write = jstestdriver.EMPTY_FUNC;
};


jstestdriver.plugins.ScriptLoader.prototype.restoreDocumentWrite = function() {
  this.dom_.write = this.originalDocumentWrite_;
};
