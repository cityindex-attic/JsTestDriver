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
 */
var coverage = (function() {

  /**
   * Represents a collection object for recording the executed lines in a given file.
   * @class
   */
  function FileCoverage(fileName, executablesLength, totalLines) {
    this.length = totalLines;
    this.fileName_ = fileName;
    this.executablesLength_ = executablesLength;
  }
  
  /**
   * Converts a coverage array into a list of covered lines.
   * @param {Array} coverage  post processing.
   * @return {Array}
   */
  FileCoverage.prototype.toCoveredLines = function() {
    var lines = [];
    for (var i = 0; i < this.length; i++) {
      if (this[i] != null) {
        lines.push(new CoveredLine(this.fileName_, this[i], i, this.executablesLength_));
      }
    }
    return lines;
  };

  /**
   * Represents a reporter for the executed lines.
   * @class
   */
  function Reporter() {
    this.coverages = [];
  };
  /**
   * Initializes a FileCoverage.
   * @param {String} fileName The file name that coverages is being initialized for.
   * @param {Number} totalLines The total lines found in the file.
   * @param {Array.<Number>} An Array of all executable line numbers.
   * @return {Array.<Number>} An array for accumulating coverage information.
   */
  Reporter.prototype.initNoop = function(fileName, totalLines, executableLines){
    var coverage = new  FileCoverage(fileName, executableLines.length, totalLines);
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
   * Represents a line that could have been executed during the test.
   * @class
   */
  function CoveredLine(qualifiedFile, executedNumber, lineNumber, totalExecutableLines) {
    this.executedNumber = executedNumber;
    this.qualifiedFile = qualifiedFile;
    this.lineNumber = lineNumber;
    this.totalExecutableLines = totalExecutableLines;
  }
  
  Reporter.prototype.summarizeCoverage = function() {
    var allLines = [];
    for (var i = 0; i < this.coverages.length; i++) {
      allLines = allLines.concat(this.coverages[i].toCoveredLines());
    }
    return allLines;
  }

  return {
    Reporter : Reporter,
    FileCoverage : FileCoverage,
    CoveredLine : CoveredLine,
    COVERAGE_DATA_KEY : 'linesCovered'
  };
})();