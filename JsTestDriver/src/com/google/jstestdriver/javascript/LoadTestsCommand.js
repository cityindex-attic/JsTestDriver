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

jstestdriver.LoadTestsCommand = function(jsonParse, pluginRegistrar, getBrowserInfo, onLoadComplete) {
  this.jsonParse_ = jsonParse;
  this.pluginRegistrar_ = pluginRegistrar;
  this.boundOnFileLoaded_ = jstestdriver.bind(this, this.onFileLoaded);
  this.boundOnFileLoadedRunnerMode_ =
      jstestdriver.bind(this, this.onFileLoadedRunnerMode);
  this.getBrowserInfo = getBrowserInfo;
  this.onLoadComplete_ = onLoadComplete
}


jstestdriver.LoadTestsCommand.prototype.loadTest = function(args) {
  var files = args[0];
  var runnerMode = args[1] == "true" ? true : false;
  var fileSrcs = this.jsonParse_('{"f":' + files + '}').f;

  this.removeScripts(document, fileSrcs);
  var fileLoader = new jstestdriver.FileLoader(this.pluginRegistrar_,
    !runnerMode ? this.boundOnFileLoaded_ : this.boundOnFileLoadedRunnerMode_);

  fileLoader.load(fileSrcs);
};


jstestdriver.LoadTestsCommand.prototype.onFileLoaded = function(status) {
  var response = new jstestdriver.Response(
      jstestdriver.RESPONSE_TYPES.FILE_LOAD_RESULT,
      JSON.stringify(status),
      this.getBrowserInfo());
  this.onLoadComplete_(response);
};

jstestdriver.LoadTestsCommand.prototype.onFileLoadedRunnerMode = function(status) {
  if (!parent.G_testRunner) {
    parent.G_testRunner = {

      finished_: false,
      success_: 1,
      report_: '',
      filesLoaded_: 0,

      isFinished: function() {
        return this.finished_;
      },

      isSuccess: function() {
        return this.success_;
      },

      getReport: function() {
        return this.report_;
      },

      getNumFilesLoaded: function() {
        return this.filesLoaded_;
      },

      setIsFinished: function(finished) {
        this.finished_ = finished;
      },

      setIsSuccess: function(success) {
        this.success_ = success;
        chromeos  },

      setReport: function(report) {
        this.report_ = report;
      },

      setNumFilesLoaded: function(filesLoaded) {
        this.filesLoaded_ = filesLoaded;
      }
    };
  }
  var testRunner = parent.G_testRunner;

  testRunner.setNumFilesLoaded(status.loadedFiles.length);
  this.streamingService_.close(null, this.__boundExecuteCommand);
};

jstestdriver.LoadTestsCommand.prototype.findScriptTagsToRemove_ = function(dom, fileSrcs) {
  var scripts = dom.getElementsByTagName('script');
  var filesSize = fileSrcs.length;
  var scriptsSize = scripts.length;
  var scriptTagsToRemove = [];

  for (var i = 0; i < filesSize; i++) {
    var f = fileSrcs[i].fileSrc;

    for (var j = 0; j < scriptsSize; j++) {
      var s = scripts[j];

      if (s.src.indexOf(f) != -1) {
        scriptTagsToRemove.push(s);
        break;
      }
    }
  }
  return scriptTagsToRemove;
};


jstestdriver.LoadTestsCommand.prototype.removeScriptTags_ = function(dom,
                                                                    scriptTagsToRemove) {
  var head = dom.getElementsByTagName('head')[0];
  var size = scriptTagsToRemove.length;

  for (var i = 0; i < size; i++) {
    var script = scriptTagsToRemove[i];

    head.removeChild(script);
  }
};


jstestdriver.LoadTestsCommand.prototype.removeScripts = function(dom, fileSrcs) {
  this.removeScriptTags_(dom, this.findScriptTagsToRemove_(dom, fileSrcs));
};
