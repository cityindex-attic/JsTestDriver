// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.core.model;

import com.google.jstestdriver.TestResult;

/**
 * Represents a run.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseJstdTestRunResult extends BaseResultModel {

  public EclipseJstdTestRunResult() {
    super(null, "Tests");
  }

  @Override
  public ResultModel addTestResult(TestResult result) {
    EclipseJstdBrowserRunResult browserRunResult =
      (EclipseJstdBrowserRunResult) getResultModel(result.getBrowserInfo().toString());
    if (browserRunResult == null) {
      browserRunResult = new EclipseJstdBrowserRunResult(this, result
          .getBrowserInfo().toString());
    }
    addChildResult(result.getBrowserInfo().toString(), browserRunResult);
    return browserRunResult.addTestResult(result);
  }
}
