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

import junit.framework.TestCase;

import com.google.jstestdriver.TestResult;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class ResultModelTest extends TestCase {
  TestResult passing1 = new TestResult(null, "passed", "", "", "testcase", "test1", 0f);
  TestResult passing2 = new TestResult(null, "passed", "", "", "testcase", "test2", 0f);
  TestResult failed1 = new TestResult(null, "failed", "", "", "testcase", "test3", 0f);
  TestResult failed2 = new TestResult(null, "error", "", "", "testcase", "test4", 0f);

  public void testDidPass() throws Exception {
    ResultModel model = new EclipseJstdTestCaseResult(null, "testcase");
    
    assertTrue(model.didPass());
    model.addTestResult(passing1);
    assertTrue(model.didPass());
    model.addTestResult(passing2);
    assertTrue(model.didPass());
    model.addTestResult(failed1);
    assertFalse(model.didPass());
    model.addTestResult(failed2);
    assertFalse(model.didPass());
  }
  
  public void testHasChildren() throws Exception {
    ResultModel model = new EclipseJstdTestCaseResult(null, "testcase");
    
    assertFalse(model.hasChildren());
    model.addTestResult(passing1);
    assertTrue(model.hasChildren());
    model.addTestResult(failed1);
    assertTrue(model.hasChildren());
  }
  
  public void testAddTestResultTestCase() throws Exception {
    
  }
}
