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

import java.util.Iterator;

import junit.framework.TestCase;

import com.google.eclipse.javascript.jstestdriver.core.model.EclipseJstdBrowserRunResult;
import com.google.eclipse.javascript.jstestdriver.core.model.EclipseJstdTestCaseResult;
import com.google.eclipse.javascript.jstestdriver.core.model.ResultModel;
import com.google.jstestdriver.TestResult;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class ResultModelTest extends TestCase {
  TestResult passing1 = new TestResult(null, "passed", "", "", "testcase1", "test1", 1f);
  TestResult passing2 = new TestResult(null, "passed", "", "", "testcase", "test2", 2f);
  TestResult failed1 = new TestResult(null, "failed", "", "", "testcase1", "test3", 3f);
  TestResult failed2 = new TestResult(null, "failed", "", "", "testcase", "test4", 4f);
  TestResult failed3 = new TestResult(null, "failed", "", "", "testcase", "test5", 5f);
  TestResult error1 = new TestResult(null, "error", "", "", "testcase1", "test6", 6f);
  TestResult error2 = new TestResult(null, "error", "", "", "testcase", "test7", 7f);

  public void testDidPass() throws Exception {
    ResultModel model = new EclipseJstdTestCaseResult(null, "testcase");
    
    assertTrue(model.passed());
    model.addTestResult(passing1);
    assertTrue(model.passed());
    model.addTestResult(passing2);
    assertTrue(model.passed());
    model.addTestResult(failed1);
    assertFalse(model.passed());
    model.addTestResult(failed2);
    assertFalse(model.passed());
  }
  
  public void testHasChildren() throws Exception {
    ResultModel model = new EclipseJstdTestCaseResult(null, "testcase");
    
    assertFalse(model.hasChildren());
    model.addTestResult(passing1);
    assertTrue(model.hasChildren());
    model.addTestResult(failed1);
    assertTrue(model.hasChildren());
  }
  
  public void testGetNumbers() throws Exception {
    ResultModel model = new EclipseJstdBrowserRunResult(null, "browser");
    
    assertEquals(0, model.getNumberOfTests());
    assertEquals(0, model.getNumberOfFailures());
    assertEquals(0, model.getNumberOfErrors());
    
    model.addTestResult(passing1);
    model.addTestResult(failed1);
    model.addTestResult(error1);

    assertEquals(3, model.getNumberOfTests());
    assertEquals(1, model.getNumberOfFailures());
    assertEquals(1, model.getNumberOfErrors());

    model.addTestResult(passing2);
    model.addTestResult(failed2);
    model.addTestResult(failed3);
    model.addTestResult(error2);
    
    assertEquals(7, model.getNumberOfTests());
    assertEquals(3, model.getNumberOfFailures());
    assertEquals(2, model.getNumberOfErrors());
  }
  
  public void testAddTestResultTestCase() throws Exception {
    ResultModel model = new EclipseJstdBrowserRunResult(null, "browser");

    model.addTestResult(passing1);
    model.addTestResult(failed1);
    model.addTestResult(error1);
    model.addTestResult(passing2);
    model.addTestResult(failed2);
    model.addTestResult(failed3);
    model.addTestResult(error2);
    
    assertEquals(2, model.getChildren().size());
    Iterator<? extends ResultModel> iterator = model.getChildren().iterator();
    ResultModel testcase1 = iterator.next();
    ResultModel testcase2 = iterator.next();
    
    assertEquals("testcase1 (10.0 ms)", testcase1.getDisplayLabel());
    assertEquals(3, testcase1.getNumberOfTests());
    assertEquals(1, testcase1.getNumberOfErrors());
    assertEquals(1, testcase1.getNumberOfFailures());

    assertEquals("testcase (18.0 ms)", testcase2.getDisplayLabel());
    assertEquals(4, testcase2.getNumberOfTests());
    assertEquals(1, testcase2.getNumberOfErrors());
    assertEquals(2, testcase2.getNumberOfFailures());
  }
  
  public void testGetTotalTimeTaken() throws Exception {
    BaseResultModel model = new EclipseJstdBrowserRunResult(null, "browser");

    model.addTestResult(passing1);
    model.addTestResult(failed1);
    model.addTestResult(error1);
    model.addTestResult(passing2);
    model.addTestResult(failed2);
    model.addTestResult(failed3);
    model.addTestResult(error2);
    
    assertEquals(2, model.getChildren().size());
    assertEquals(28f, model.getTotalTimeTaken());
    Iterator<? extends ResultModel> iterator = model.getChildren().iterator();
    BaseResultModel testcase1 = (BaseResultModel) iterator.next();
    BaseResultModel testcase2 = (BaseResultModel) iterator.next();
    
    assertEquals("testcase1 (10.0 ms)", testcase1.getDisplayLabel());
    assertEquals(10f, testcase1.getTotalTimeTaken());

    assertEquals("testcase (18.0 ms)", testcase2.getDisplayLabel());
    assertEquals(18f, testcase2.getTotalTimeTaken());
  }
}
