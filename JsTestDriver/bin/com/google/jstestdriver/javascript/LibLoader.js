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
jstestdriver.LibLoader = function(files, dom, getScript) {
  this.files_ = files;
  this.dom_ = dom;
  this.getScript_ = getScript;
  this.remainingLibToLoad_ = this.files_.length;
  this.boundOnLibLoaded_ = jstestdriver.bind(this, this.onLibLoaded);
  this.savedDocumentWrite_ = dom.write;
  this.currentFile_ = 0;
};


jstestdriver.LibLoader.prototype.load = function(onAllLibLoaded, data) {
  if (this.files_.length == 0) {
    onAllLibLoaded(data);
  } else {
    this.dom_.write = function() {};
    this.onAllLibLoaded_ = onAllLibLoaded;
    this.data_ = data;
    this.getScript_(this.dom_, this.files_[this.currentFile_++], this.boundOnLibLoaded_);
  }
};


jstestdriver.LibLoader.prototype.onLibLoaded = function() {
  if (--this.remainingLibToLoad_ == 0) {
    var onAllLibLoaded = this.onAllLibLoaded_;
    var data = this.data_;

    this.onAllLibLoaded_ = null;
    this.data_ = null;
    this.dom_.write = this.savedDocumentWrite_;
    onAllLibLoaded(data);
  } else {
    this.getScript_(this.dom_, this.files_[this.currentFile_++], this.boundOnLibLoaded_);
  }
};
