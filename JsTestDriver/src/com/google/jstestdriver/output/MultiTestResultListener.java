package com.google.jstestdriver.output;

import com.google.inject.Inject;
import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.TestResult;

import java.util.Set;

/**
 * Print results to multiple result printers simultaneously.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MultiTestResultListener implements TestResultListener {
  private final Set<TestResultListener> delegates;

  @Inject
  public MultiTestResultListener(Set<TestResultListener> delegates) {
    this.delegates = delegates;
  }

  public void onTestComplete(TestResult testResult) {
    for (TestResultListener delegate : delegates) {
      delegate.onTestComplete(testResult);
    }
  }

  public void finish() {
    for (TestResultListener delegate : delegates) {
      delegate.finish();
    }
  }

  public void onFileLoad(String browser, FileResult fileResult) {
    for (TestResultListener delegate : delegates) {
      delegate.onFileLoad(browser, fileResult);
    }
  }
}