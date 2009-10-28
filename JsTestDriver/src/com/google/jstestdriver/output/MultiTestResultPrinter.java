package com.google.jstestdriver.output;

import com.google.jstestdriver.TestResult;

import java.util.List;

/**
 * Print results to multiple result printers simultaneously.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MultiTestResultPrinter implements TestResultPrinter {
  private final List<TestResultPrinter> delegates;

  public MultiTestResultPrinter(List<TestResultPrinter> delegates) {
    this.delegates = delegates;
  }

  public void open(String name) {
    for (TestResultPrinter delegate : delegates) {
      delegate.open(name);
    }
  }

  public void print(TestResult testResult) {
    for (TestResultPrinter delegate : delegates) {
      delegate.print(testResult);
    }
  }

  public void close() {
    for (TestResultPrinter delegate : delegates) {
      delegate.close();
    }
  }
}