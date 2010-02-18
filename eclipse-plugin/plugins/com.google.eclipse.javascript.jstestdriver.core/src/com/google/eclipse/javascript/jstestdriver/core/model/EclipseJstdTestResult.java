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
 * Represents the base Result Model. The leaf in the results tree.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class EclipseJstdTestResult extends BaseResultModel {
  private static final String TEST_PASS_ICON = "/icons/testok.gif";
  private static final String TEST_FAIL_ICON = "/icons/testfail.gif";
  private static final String TEST_ERROR_ICON = "/icons/testerr.gif";

  private TestResult result;

  public EclipseJstdTestResult(EclipseJstdTestCaseResult parent, String testName) {
    super(parent, testName);
  }

  @Override
  public BaseResultModel addTestResult(TestResult result) {
    this.result = result;
    return this;
  }

  public TestResult getResult() {
    return result;
  }

  @Override
  public void clear() {
    result = null;
  }

  @Override
  public boolean passed() {
    return result != null ? (result.getResult() == TestResult.Result.passed) : false;
  }

  @Override
  public int getNumberOfTests() {
    return result != null ? 1 : 0;
  }

  @Override
  public int getNumberOfErrors() {
    return result != null ? (result.getResult() == TestResult.Result.error ? 1 : 0) : 0;
  }

  @Override
  public int getNumberOfFailures() {
    return result != null ? (result.getResult() == TestResult.Result.failed ? 1 : 0) : 0;
  }

  @Override
  public float getTotalTimeTaken() {
    return result != null ? result.getTime() : 0f;
  }

  @Override
  public String getDisplayImagePath() {
    if (passed()) {
      return TEST_PASS_ICON;
    } else if (getNumberOfErrors() > 0) {
      return TEST_ERROR_ICON;
    } else {
      return TEST_FAIL_ICON;
    }
  }
}