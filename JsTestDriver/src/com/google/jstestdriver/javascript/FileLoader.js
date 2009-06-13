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
  this.successFiles_ = [];
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
    this.createTag(this.files_.shift());
  } else {
    onAllFilesLoaded('no files to load');
  }
};


jstestdriver.FileLoader.prototype.onFileLoaded = function(fileLoaded) {
  if (fileLoaded && fileLoaded == this.fileLoading_) {
    this.successFiles_.push(fileLoaded);
    if (this.files_.length == 0) {
      var onAllFilesLoaded = this.onAllFilesLoaded_;

      this.onAllFilesLoaded_ = null;
      this.dom_.write = this.savedDocumentWrite_;
      onAllFilesLoaded({
        message : this.loadMsg_,
        successFiles : this.successFiles_,
        errorFiles : this.errorFiles_
      });
    } else {
      this.createTag(this.files_.shift());
    }
  }
};


jstestdriver.FileLoader.prototype.createTag = function(file) {
  if (file.fileSrc.match(/\.js$/)) {
    this.createScript(this.dom_, file, this.boundOnFileLoaded_);
  } else if (file.fileSrc.match(/\.css$/)) {
    this.createLink(this.dom_, file, this.boundOnFileLoaded_);
  }
};


jstestdriver.FileLoader.prototype.createScript = function(dom, file, callback) {
  var head = dom.getElementsByTagName('head')[0];
  var script = dom.createElement('script');

  script.onerror = this.boundOnError_;
  script.onload = function() { callback(file); };
  script.onreadystatechange = function() {
    if (script.readyState == 'loaded') {
      callback(file);
    }
  };
  script.type = "text/javascript";
  script.src = file.fileSrc;
  this.fileLoading_ = file;
  head.appendChild(script);
};


jstestdriver.FileLoader.prototype.createLink = function(dom, file, callback) {
  var head = dom.getElementsByTagName('head')[0];
  var link = dom.createElement('link');

  link.onerror = this.boundOnError_;
  link.onload = function() { callback(file); };
  link.onreadystatechange = function() {
    if (link.readyState == 'loaded') {
      callback(file);
    }
  };
  link.type = "text/css";
  link.rel = "stylesheet";
  link.href = file.fileSrc;
  this.fileLoading_ = file;
  head.appendChild(link);

  // Firefox and Safari don't seem to support onload or onreadystatechange for link
  if (jQuery.browser.mozilla || jQuery.browser.safari) {
    this.onFileLoaded(file);
  }
};
