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
package com.google.jstestdriver.output;

import com.google.jstestdriver.TestResult;

import java.io.PrintStream;

/**
 * The impl of Problem for TestResults. Used by the default reporting outputstream 
 * to format TestResult failures and errors.
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
class TestResultProblem implements Problem {
  private final TestResult testResult;
  private static String NEW_LINE = System.getProperty("line.separator");

  public TestResultProblem(TestResult testResult) {
    this.testResult = testResult;
  }

  public void print(PrintStream out, boolean verbose) {
    if (testResult.getResult() != TestResult.Result.passed) {
      printProblem(out, testResult);
    } else {
      printPassed(out, testResult);
    }
    if (!verbose && testResult.getLog().length() > 0) {
      printTestLog(out, testResult);
    }
  }

  private void printPassed(PrintStream out, TestResult testResult) {
    out.println(String.format("    %s.%s %s (%.2f ms)",
                              testResult.getTestCaseName(),
                              testResult.getTestName(),
                              testResult.getResult(),
                              testResult.getTime()));
  }

  private void printProblem(PrintStream out, TestResult testResult) {
    final StringBuilder formattedStack = new StringBuilder(NEW_LINE);
    for (String line : testResult.getStack().split(NEW_LINE)) {
      formattedStack.append("      ").append(line).append(NEW_LINE);
    }
    out.println(String.format("    %s.%s %s (%.2f ms): %s%s",
                              testResult.getTestCaseName(),
                              testResult.getTestName(),
                              testResult.getResult(),
                              testResult.getTime(),
                              testResult.getParsedMessage(),
                              formattedStack.toString()));
  }

  private void printTestLog(PrintStream out, TestResult testResult) {
    String[] logLines = testResult.getLog().split(NEW_LINE);

    for (String line : logLines) {
      out.println("      " + line);
    }
  }
}