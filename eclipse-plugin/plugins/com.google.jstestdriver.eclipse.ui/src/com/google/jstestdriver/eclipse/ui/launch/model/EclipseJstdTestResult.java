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
package com.google.jstestdriver.eclipse.ui.launch.model;

import org.eclipse.swt.graphics.Image;

import com.google.jstestdriver.TestResult;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 * 
 */
public class EclipseJstdTestResult extends ResultModel {

  private TestResult result;

  public EclipseJstdTestResult(EclipseJstdTestCaseResult parent, String testName) {
    super(parent, testName);
  }

  @Override
  public ResultModel addTestResult(TestResult result) {
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
  public boolean didPass() {
    return result.getResult() == TestResult.Result.passed;
  }

  @Override
  public int getNumberOfTests() {
    return result != null ? 1 : 0;
  }

  @Override
  public int getNumberOfErrors() {
    return result.getResult() == TestResult.Result.error ? 1 : 0;
  }

  @Override
  public int getNumberOfFailures() {
    return result.getResult() == TestResult.Result.failed ? 1 : 0;
  }
  
  @Override
  public float getTotalTimeTaken() {
    return result.getTime();
  }

  @Override
  public Image getDisplayImage() {
    if (didPass()) {
      return icon.getImage(TEST_PASS_ICON);
    } else if (getNumberOfErrors() > 0) {
      return icon.getImage(TEST_ERROR_ICON);
    } else {
      return icon.getImage(TEST_FAIL_ICON);
    }
  }
}
