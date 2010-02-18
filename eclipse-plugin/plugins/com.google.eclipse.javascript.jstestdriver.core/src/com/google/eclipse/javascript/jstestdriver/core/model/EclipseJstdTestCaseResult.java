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
package com.google.eclipse.javascript.jstestdriver.core.model;

import com.google.jstestdriver.TestResult;

/**
 * Represents the results of a test case.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
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
