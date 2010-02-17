// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.core.model;

import com.google.jstestdriver.TestResult;

/**
 * Represents the results of a test case.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseJstdTestCaseResult extends BaseResultModel {

  public EclipseJstdTestCaseResult(EclipseJstdBrowserRunResult parent, String testcaseName) {
    super(parent, testcaseName);
  }

  @Override
  public ResultModel addTestResult(TestResult result) {
    EclipseJstdTestResult testResult =
      (EclipseJstdTestResult) getResultModel(result.getTestName());
    if (testResult == null) {
      testResult = new EclipseJstdTestResult(this, result.getTestName());
    }
    addChildResult(result.getTestName(), testResult);
    return testResult.addTestResult(result);
  }
}
