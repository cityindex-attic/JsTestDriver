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
/** @author corysmith@google.com (Cory Smith) */
var CoverageTest = TestCase('CoverageTest');

CoverageTest.prototype.testInit = function() {
  console.debug("testInit");
  var executableLines = [1,2,5];
  var totalLines = 20;
  var fileName = 'FILE';
  var fileCoverage = new coverage.Reporter().init(fileName, totalLines, executableLines);
  assertEquals('The length of fileCoverage is invalid.',totalLines, fileCoverage.length);
  for (var i = 1; i < executableLines.length; i++) {
    assertSame('Should have been zero.', 0, fileCoverage[executableLines[i]]);
  }
  assertEquals('First line should have been processed', 1, fileCoverage[executableLines[0]]);
};

CoverageTest.prototype.testInitNoop = function() {
  console.debug("testInitNoop");
  var executableLines = [1,2,5];
  var totalLines = 20;
  var fileName = 'FILE';
  var fileCoverage = new coverage.Reporter().initNoop(fileName, totalLines, executableLines);
  assertEquals('The length of fileCoverage is invalid.',totalLines, fileCoverage.length);
  for (var i = 0; i < executableLines.length; i++) {
    assertSame('Should have been zero.', 0, fileCoverage[executableLines[i]]);
  }
};

CoverageTest.prototype.testToCoveredLines = function() {
  var executableLines = [1,2,5];
  var totalLines = 20;
  var fileName = 'FILE';
  var fileCoverage = new coverage.Reporter().init(fileName, totalLines, executableLines);
  
  fileCoverage[executableLines[1]]++;
  
  var report = fileCoverage.toCoveredLines();
  assertEquals(executableLines.length, report.lines.length);
  for(var i = 0; i < report.lines.length; i++) {
    assertEquals(executableLines[i], report.lines[i].lineNumber);
    assertEquals(fileCoverage[executableLines[i]], report.lines[i].executedNumber);
  }
}

CoverageTest.prototype.testSummarizeCoverage = function() {
  var reporter = new coverage.Reporter();
  var fileOne = reporter.init('boo.js', 10, [1,3,5]);
  fileOne[5]++;
  var fileTwo = reporter.init('foo.js', 5, [2,3,4]);
  fileTwo[3]++;
  fileTwo[4]++;
  var reports = reporter.summarizeCoverage();
  assertEquals(3, reports[0].lines.length);
  assertEquals(3, reports[1].lines.length);
};
