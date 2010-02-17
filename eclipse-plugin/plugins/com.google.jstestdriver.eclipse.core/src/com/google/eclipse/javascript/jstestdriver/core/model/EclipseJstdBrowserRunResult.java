// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.core.model;

import com.google.jstestdriver.TestResult;

/**
 * Represents the results for a particular browser.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseJstdBrowserRunResult extends BaseResultModel {

  public EclipseJstdBrowserRunResult(EclipseJstdTestRunResult parent,
      String browser) {
    super(parent, browser);
  }

  @Override
  public ResultModel addTestResult(TestResult result) {
    EclipseJstdTestCaseResult testCaseResult =
        (EclipseJstdTestCaseResult) getResultModel(result.getTestCaseName());
    if (testCaseResult == null) {
      testCaseResult = new EclipseJstdTestCaseResult(this, result
          .getTestCaseName());
    }
    addChildResult(result.getTestCaseName(), testCaseResult);
    return testCaseResult.addTestResult(result);
  }
}