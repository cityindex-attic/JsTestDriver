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


/**
 * Reporter for test results when running in stand alone mode.
 * @param {Function} createNode
 * @param {Function} appendToBody
 * @constructor
 */
jstestdriver.VisualTestReporter = function(createNode, appendToBody, jQuery, parseJson_) {
  this.finished_ = false;
  this.success_ = 1;
  this.report_ = '';
  this.filesLoaded_ = [];
  this.testResults_ = [];
  this.createNode_ = createNode;
  this.appendToBody_ = appendToBody;
  this.jQuery_ = jQuery;
  this.parseJson_ = parseJson_;
  this.passed_ = 0;
  this.testTime_ = new jstestdriver.VisualTestReporter.Interval();
  this.loadTime_ = new jstestdriver.VisualTestReporter.Interval();
};


jstestdriver.VisualTestReporter.prototype.isFinished = function() {
  return this.finished_;
};


/**
 * @return {String} A json representation of the test results.
 */
jstestdriver.VisualTestReporter.prototype.getReport = function() {
  return this.report_;
};


jstestdriver.VisualTestReporter.prototype.getNumFilesLoaded = function() {
  return this.filesLoaded_.length;
};


jstestdriver.VisualTestReporter.prototype.setIsFinished = function(finished) {
  this.finished_ = finished;
  if (finished) {
    var resultsHolder = this.createNode_('div');
    this.jQuery_(resultsHolder).attr('id', 'JsTDVisualResults');
    resultsHolder.className = 'results';
    var details = this.createNode_('div');
    details.className = 'details';
    var cases = {};
    for (var i = 0, result = this.testResults_[i];
         result = this.testResults_[i];
         i++) {
      if (!cases[result.testCaseName] ||
          (cases[result.testCaseName] == jstestdriver.TestResult.RESULT.PASSED)) {
        cases[result.testCaseName] = result.result;
      }
      this.renderResult(result, details);
    }
    var caseSummaryHtml = [];
    for (caseName in cases) {
      caseSummaryHtml.push(
          jstestdriver.formatString(
              '<div class="%s">%s [%s]</div>',
              cases[caseName],
              caseName,
              cases[caseName].toUpperCase()));
    }
    caseSummaryHtml.sort(); // order: error, failed, passed

    var summary = resultsHolder.appendChild(this.createNode_('div'));
    summary.className = 'summary';
    summary.innerHTML = jstestdriver.formatString(
        '<h2>Summary:</h2><div class="cases">%s</div><p>%s out of %s tests passed in %ds.</p><h2>Details:</h2>',
        caseSummaryHtml.join(''),
        this.passed_,
        this.testResults_.length,
        this.testTime_.toSeconds()
    );
    this.renderFilesLoaded(resultsHolder);
    resultsHolder.appendChild(details);
    this.appendToBody_(resultsHolder);
  }
};

jstestdriver.VisualTestReporter.prototype.renderFilesLoaded = function(parent) {
  var filesLoaded = parent.appendChild(this.createNode_('div'));
  filesLoaded.className = 'load';
  filesLoaded.innerHTML = jstestdriver.formatString(
      '%s files loaded in %ds',
      this.filesLoaded_.length,
      this.loadTime_.toSeconds());
};


jstestdriver.VisualTestReporter.prototype.renderResult = function(result, parent) {
  var node = this.createNode_('div');
  node.className = result.result;
  var html = [jstestdriver.formatString(
      '%s.%s [%s] in %d ms',
      result.testCaseName,
      result.testName,
      result.result.toUpperCase(),
      result.time)];
  if (result.message) {
    var message = this.parseJson_(result.message);
    html.push('<div class="message">' + message.message + '</div>');
    if (message.stack) {
      var frames = message.stack.split('\n');
      var stack = [];
      for (var i = 0; frames[i]; i++) {
        if (frames[i].indexOf('/test/') != -1) {
          stack.push(frames[i]);
        }
      }
      html.push('<div class="stack">' + stack.join("\n") + '</div>');
    }
  }
  if (result.result != jstestdriver.TestResult.RESULT.PASSED) {
    html.push('<div class="log">' + result.log + '</div>');
  }
  node.innerHTML = html.join('');
  parent.appendChild(node);
};


jstestdriver.VisualTestReporter.prototype.setIsSuccess = function(success) {
  this.success_ = success;
};


/**
 * Adds a test result to the current run.
 * @param {jstestdriver.TestResult}
 */
jstestdriver.VisualTestReporter.prototype.addTestResult = function(testResult) {
  this.testResults_.push(testResult);
};


jstestdriver.VisualTestReporter.prototype.isSuccess = function() {
  return !!this.success_;
};


jstestdriver.VisualTestReporter.prototype.updateIsSuccess = function(success) {
  this.passed_ += Number(success);
  this.success_ = success & this.success_;
};


jstestdriver.VisualTestReporter.prototype.setReport = function(report) {
  this.report_ = report;
};


jstestdriver.VisualTestReporter.prototype.addLoadedFileResults = function(filesLoaded) {
  this.filesLoaded_ = this.filesLoaded_.concat(filesLoaded);
};


jstestdriver.VisualTestReporter.prototype.startLoading = function(when) {
  this.loadTime_.start = when;
}


jstestdriver.VisualTestReporter.prototype.finishLoading = function(when) {
  this.loadTime_.stop = when;
}


jstestdriver.VisualTestReporter.prototype.finishTests = function(when) {
  this.testTime_.stop = when;
}


jstestdriver.VisualTestReporter.prototype.startTests = function(when) {
  this.testTime_.start = when;
}


jstestdriver.VisualTestReporter.prototype.toString = function() {
  return "VisualTestReporter(success=["
      + this.success_ + "], finished=["
      + this.finished_ + "], lastTestResult=["
      + this.lastTestResult_ + "], filesLoaded=["
      + this.filesLoaded_ + "])";
};



/**
 * A period of time defined by milliseconds.
 */
jstestdriver.VisualTestReporter.Interval = function() {
  this.start = 0;
  this.stop = 0;
};


/**
 * @return {Number} Representation of the interval is seconds.
 */
jstestdriver.VisualTestReporter.Interval.prototype.toSeconds = function() {
  return (this.stop - this.start)/1000.0;
};
