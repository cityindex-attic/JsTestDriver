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
jstestdriver.FileLoader = function(dom) {
  this.dom_ = dom;
  this.boundOnFileLoaded_ = jstestdriver.bind(this, this.onFileLoaded);
  this.savedDocumentWrite_ = dom.write;
  this.boundOnError_ = jstestdriver.bind(this, this.onError);
  this.loadMsg_ = '';
  this.errorFiles_ = [];
  window.onerror = this.boundOnError_;
};


jstestdriver.FileLoader.prototype.onError = function(msg, url, line) {
  var index = url.indexOf('/test/');

  if (index != -1) {
    var fileName = url.substr(index + 6);

    this.loadMsg_ += 'error loading file: ' + fileName + ':' + line + ' ' + msg + '\n';
    this.errorFiles_.push(fileName);
  } else if (!this.loadMsg_.length) {
    this.loadMsg_ = 'error loading file(s)';
  }
};


jstestdriver.FileLoader.prototype.load = function(files, onAllFilesLoaded) {
  this.files_ = files;
  if (this.files_.length > 0) {
    this.onAllFilesLoaded_ = onAllFilesLoaded;
    this.dom_.write = function() {};
    this.createScript(this.dom_, this.files_.shift(), this.boundOnFileLoaded_);
  } else {
    onAllFilesLoaded('no files to load');
  }
};


jstestdriver.FileLoader.prototype.onFileLoaded = function() {
  if (this.files_.length == 0) {
    var onAllFilesLoaded = this.onAllFilesLoaded_;

    this.onAllFilesLoaded_ = null;
    this.dom_.write = this.savedDocumentWrite_;
    onAllFilesLoaded(JSON.stringify({ message: this.loadMsg_, files: this.errorFiles_ }));
  } else {
    this.createScript(this.dom_, this.files_.shift(), this.boundOnFileLoaded_);
  }
};


jstestdriver.FileLoader.prototype.createScript = function(dom, file, callback) {
  var head = dom.getElementsByTagName('head')[0];
  var script = dom.createElement('script');

  script.onerror = this.boundOnError_;
  script.onload = callback;
  script.onreadystatechange = function() {
    if (script.readyState == 'loaded') {
      callback();
    }
  };
  script.type = "text/javascript";
  script.src = file;
  head.appendChild(script);
};
