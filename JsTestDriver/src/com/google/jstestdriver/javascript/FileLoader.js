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
jstestdriver.FileLoader = function(pluginRegistrar, onAllFilesLoaded) {
  this.pluginRegistrar_ = pluginRegistrar;
  this.onAllFilesLoaded_ = onAllFilesLoaded;
  this.boundOnFileLoaded = jstestdriver.bind(this, this.onFileLoaded_);
  this.boundLoadFile_ = jstestdriver.bind(this, this.onLoadFile_);
  this.loadedFiles_ = [];
};


/**
 * Load files.
 * 
 * files is an array containing jstestdriver.FileSource objects.
 */
jstestdriver.FileLoader.prototype.load = function(files) {
  this.files_ = files;
  if (this.files_.length > 0) {
    this.loadFile_(this.files_.shift());
  } else {
    this.onAllFilesLoaded_({ loadedFiles: [] });
  }
};


jstestdriver.FileLoader.prototype.loadFile_ = function(file) {
  this.pluginRegistrar_.loadSource(file, this.boundOnFileLoaded);
};

/**
 * This method is called once a file has been loaded. It then either load the next file or if none
 * are left sends back the list of loaded files to the server.
 * 
 * @param {Object} file A jstestdriver.FileResult object
 */
jstestdriver.FileLoader.prototype.onFileLoaded_ = function(fileLoaded) {
  this.loadedFiles_.push(fileLoaded);
  if (this.files_.length == 0) {
    this.onAllFilesLoaded_({
      loadedFiles: this.loadedFiles_
    });
  } else {
    this.loadFile_(this.files_.shift());
  }
};
