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
  var COVERAGE_DATA_KEY = 'linesCovered';

  /**
   * Represents a collection object for recording the executed lines in a given file.
   * @class
   */
  function FileCoverageReport(fileId, executablesLength, totalLines) {
    this.length = totalLines;
    this.fileId_ = fileId;
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
    return new CoveredLines(this.fileId_, lines);
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
   * @param {String} fileId The file name that coverages is being initialized for.
   * @param {Number} totalLines The total lines found in the file.
   * @param {Array.<Number>} An Array of all executable line numbers.
   * @return {Array.<Number>} An array for accumulating coverage information.
   */
  Reporter.prototype.initNoop = function(fileId, totalLines, executableLines){
    var coverage = new  FileCoverageReport(fileId,
                                           executableLines.length,
                                           executableLines[executableLines.length-1] + 1);
    var length = executableLines.length;
    for (var i = 0; i < length; i++) {
      coverage[executableLines[i]] = 0;
    }
    this.coverages.push(coverage);
    return coverage;
  };

  /**
   * Initializes a coverage array and marks the first line as executed
   * @param {String} fileId The file name that coverages is being initialized for.
   * @param {Number} totalLines The total lines found in the file.
   * @param {Array.<Number>} An Array of all executable line numbers.
   * @return {Array.<Number>} An array for accumulating coverage information.
   */
  Reporter.prototype.init = function(fileId, totalLines, executableLines){
    var coverage = this.initNoop(fileId, totalLines, executableLines);
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
  
  Summary.prototype.toProtoBuffer = function() {
    var buffer = ["["];
    var sep = "";
    for (var i = 0; i < this.coveredLines.length; i++) {
      buffer.push(sep);
      this.coveredLines[i].writeProtoBuffer(buffer);
      sep = ",";
    }
    buffer.push("]");
    return buffer.join("");
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
  
  CoveredLines.prototype.writeProtoBuffer = function(buffer) {
    buffer.push("[" , this.qualifiedFile , ",[");
    var sep = "";
    for (var i = 0; i < this.lines.length; i++) {
      buffer.push(sep);
      this.lines[i].writeProtoBuffer(buffer)
      sep = ",";
    }
    buffer.push("]]");
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
    return "{\"executedNumber\":" + this.executedNumber +
            ",\"lineNumber\":" + this.lineNumber + "}";
  };
  
  CoveredLine.prototype.writeProtoBuffer = function(buffer) {
    buffer.push("[", this.lineNumber, ",", this.executedNumber, "]");
  };
  
  /** */
  function InstrumentedTestCaseRunnerPlugin(testRunner, coverageReporter, setTimeout) {
    this.testRunner = testRunner;
    this.coverageReporter = coverageReporter;
    this.setTimeout = setTimeout;
  }


  InstrumentedTestCaseRunnerPlugin.prototype.name = "coverage_runner_plugin";


  /** */
  InstrumentedTestCaseRunnerPlugin.prototype.runTestConfiguration = function(
          testRunConfiguration, onTestDone, onTestRunConfigurationComplete) {

    var testCaseInfo = testRunConfiguration.getTestCaseInfo();
    if (testCaseInfo.getType() != jstestdriver.TestCaseInfo.DEFAULT_TYPE) {
      return false;
    }
    
    var runner = this.testRunner;
    function runTest(testName, onTestRunDone) {
      onTestRunDone(runner.runTest(testCaseInfo.getTestCaseName(),
                                   testCaseInfo.getTemplate(),
                                   testName));
    }
    
    var iterator = new TestResultIterator(testRunConfiguration.getTests(),
                                          runTest);
    
    var reporter = this.coverageReporter;
    function summarizeCoverage() {
      return reporter.summarizeCoverage();
    }

    var testCaseRunner =
        new InstrumentedTestCaseRunner(iterator,
                                       onTestDone,
                                       onTestRunConfigurationComplete,
                                       summarizeCoverage,
                                       this.setTimeout);
    // replace this with a series of generic TestRunSteps: each Step just call done, which runs the next step.
    testCaseRunner.run();
    return true;
  };



  /**
   * Runs tests in an instrumented fashion
   */
  function InstrumentedTestCaseRunner(resultIterator,
                                      onTestDone,
                                      onAllTestsDone,
                                      processCoverage,
                                      setTimeout) {
    this.resultIterator = resultIterator;
    this.setTimeout = setTimeout;
    this.onTestDone = onTestDone;
    this.onAllTestsDone = onAllTestsDone;
    this.processCoverage = processCoverage;
    this.boundRun = jstestdriver.bind(this, this.run);
    this.boundProcessResults = jstestdriver.bind(this,
                                                 this.processResults);
  }


  InstrumentedTestCaseRunner.prototype.run = function () {
    if (!this.resultIterator.hasNext()) {
      this.onAllTestsDone();
    } else {
      this.resultIterator.runNext(this.boundProcessResults);
    }
  }


  InstrumentedTestCaseRunner.prototype.processResults = function(result) {
    var self = this;
    // only process coverage at the end of the line.
    if (this.resultIterator.hasNext()) {
      result.data[COVERAGE_DATA_KEY] = '[]';
      this.setTimeout(function() {
        self.onTestDone(result);
        self.setTimeout(self.boundRun, 1);
      }, 1);
    } else {
      this.setTimeout(function(){
        var summary = self.processCoverage();
        self.setTimeout(function() {
          result.data[COVERAGE_DATA_KEY] = summary.toProtoBuffer();
          self.setTimeout(function() {
            self.onTestDone(result);
            self.setTimeout(self.boundRun, 1);
          }, 1);
        }, 1);
      }, 1);
    }
  }

  /** */
  function TestResultIterator(tests, runTest) {
    this.idx = -1;
    this.tests = tests;
    this.runTest = runTest;
  }


  TestResultIterator.prototype.hasNext = function() {
    return this.tests[this.idx + 1];
  };


  TestResultIterator.prototype.runNext = function(onDone) {
    this.runTest(this.tests[++this.idx], onDone);
  }



  return {
    InstrumentedTestCaseRunnerPlugin : InstrumentedTestCaseRunnerPlugin,
    InstrumentedTestCaseRunner : InstrumentedTestCaseRunner,
    TestResultIterator : TestResultIterator,
    Reporter : Reporter,
    FileCoverageReport : FileCoverageReport,
    CoveredLine : CoveredLine,
    COVERAGE_DATA_KEY : COVERAGE_DATA_KEY
  };
})();
