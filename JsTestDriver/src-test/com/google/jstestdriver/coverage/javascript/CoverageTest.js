var CoverageTest  = TestCase('CoverageTest ');

CoverageTest.prototype.testInit = function() {
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
  
  var lines = fileCoverage.toCoveredLines();
  assertEquals(executableLines.length, lines.length);
  for(var i = 0; i < lines.length; i++) {
    assertEquals(fileName, lines[i].qualifiedFile);
    assertEquals(executableLines[i], lines[i].lineNumber);
    assertEquals(fileCoverage[executableLines[i]], lines[i].executedNumber);
    assertEquals(lines.length, lines[i].totalExecutableLines);
  }
}

CoverageTest.prototype.testSummarizeCoverage = function() {
  var reporter = new coverage.Reporter();
  var fileOne = reporter.init('boo.js', 10, [1,3,5]);
  fileOne[5]++;
  var fileTwo = reporter.init('foo.js', 5, [2,3,4]);
  fileTwo[3]++;
  fileTwo[4]++;
  var coveredLines = reporter.summarizeCoverage();
  assertEquals(6, coveredLines.length);
};
