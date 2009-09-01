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
/**
 * The coverage plugin namespace.
 * @author corysmith@google.com (Cory Smith)
 */
var coverage = (function() {
  /**
   * Represents a collection object for recording the executed lines in a given file.
   * @class
   */
  function FileCoverageReport(fileName, executablesLength, totalLines) {
    this.length = totalLines;
    this.fileName_ = fileName;
    this.executablesLength_ = executablesLength;
  }

  /**
   * Converts a coverage array into a list of covered lines.
   * @param {Array} coverage  post processing.
   * @return {Array}
   */
  FileCoverageReport.prototype.toCoveredLines = function() {
    var lines = [];
    var first = true;
    for (var i = 0; i < this.length; i++) {
      if (this[i] != null) {
        lines.push(new CoveredLine(this[i], i));
        this[i] = 0;
      }
    }
    return new CoveredLines(this.fileName_, lines);
  };


  /**
   * Represents a reporter for the executed lines.
   * @class
   */
  function Reporter() {
    this.coverages = [];
  };

  /**
   * Initializes a FileCoverageReport.
   * @param {String} fileName The file name that coverages is being initialized for.
   * @param {Number} totalLines The total lines found in the file.
   * @param {Array.<Number>} An Array of all executable line numbers.
   * @return {Array.<Number>} An array for accumulating coverage information.
   */
  Reporter.prototype.initNoop = function(fileName, totalLines, executableLines){
    var coverage = new  FileCoverageReport(fileName, executableLines.length, totalLines);
    for (var i = 0; i < executableLines.length; i++) {
      coverage[executableLines[i]] = 0;
    }
    this.coverages.push(coverage);
    return coverage;
  };

  /**
   * Initializes a coverage array and marks the first line as executed
   * @param {String} fileName The file name that coverages is being initialized for.
   * @param {Number} totalLines The total lines found in the file.
   * @param {Array.<Number>} An Array of all executable line numbers.
   * @return {Array.<Number>} An array for accumulating coverage information.
   */
  Reporter.prototype.init = function(fileName, totalLines, executableLines){
    var coverage = this.initNoop(fileName, totalLines, executableLines);
    coverage[executableLines[0]]++;
    return coverage;
  };

  /**
   * @return {Summary} A summary object containing all the current coverage information.
   */
  Reporter.prototype.summarizeCoverage = function() {
    var summary = [];
    for (var i = 0; i < this.coverages.length; i++) {
      summary.push(this.coverages[i].toCoveredLines());
    }
    return new Summary(summary);
  }

  function Summary(coveredLines) {
    this.coveredLines = coveredLines;
  }
  
  Summary.prototype.toJson = function() {
    var lines = [];
    for (var i = 0; i < this.coveredLines.length; i++) {
      lines.push(this.coveredLines[i].toJson());
    }
    return "[" + lines.join(",") + "]";
  };
  
  /**
   * A serializable class that represents the current state of coverage for a file.
   * @param {String} qualifiedFile The name of the file.
   * @param {Array.<CoveredLine>} coveredLines The lines of the file.
   */
  function CoveredLines(qualifiedFile, lines) {
    this.qualifiedFile = qualifiedFile;
    this.lines = lines;
  }
  
  /**
   * @return A Json representation of the CoveredLines.
   */
  CoveredLines.prototype.toJson = function() {
    var json = ["{\"qualifiedFile\":\"" , this.qualifiedFile , "\",\"lines\":["];
    var sep = "";
    for (var i = 0; i < this.lines.length; i++) {
      json.push(sep, this.lines[i].toJson());
      sep = ",";
    }
    json.push("]}");
    return json.join("");
  };

  /**
   * Represents a line that could have been executed during the test.
   * @class
   */
  function CoveredLine(executedNumber, lineNumber, totalExecutableLines) {
    this.executedNumber = executedNumber;
    this.lineNumber = lineNumber;
  }
  
  CoveredLine.prototype.toJson = function() {
    return "{\"executedNumber\":" + this.executedNumber + ",\"lineNumber\":" + this.lineNumber + "}";
  };


  return {
    Reporter : Reporter,
    FileCoverageReport : FileCoverageReport,
    CoveredLine : CoveredLine,
    COVERAGE_DATA_KEY : 'linesCovered'
  };
})();
