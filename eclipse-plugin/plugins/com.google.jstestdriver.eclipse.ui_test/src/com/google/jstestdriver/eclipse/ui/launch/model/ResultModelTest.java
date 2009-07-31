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

import java.util.Iterator;

import junit.framework.TestCase;

import com.google.jstestdriver.TestResult;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class ResultModelTest extends TestCase {
  TestResult passing1 = new TestResult(null, "passed", "", "", "testcase1", "test1", 0f);
  TestResult passing2 = new TestResult(null, "passed", "", "", "testcase", "test2", 0f);
  TestResult failed1 = new TestResult(null, "failed", "", "", "testcase1", "test3", 0f);
  TestResult failed2 = new TestResult(null, "failed", "", "", "testcase", "test4", 0f);
  TestResult failed3 = new TestResult(null, "failed", "", "", "testcase", "test5", 0f);
  TestResult error1 = new TestResult(null, "error", "", "", "testcase1", "test6", 0f);
  TestResult error2 = new TestResult(null, "error", "", "", "testcase", "test7", 0f);

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
    Iterator<ResultModel> iterator = model.getChildren().iterator();
    ResultModel testcase1 = iterator.next();
    ResultModel testcase2 = iterator.next();
    
    assertEquals("testcase1", testcase1.getDisplayLabel());
    assertEquals(3, testcase1.getNumberOfTests());
    assertEquals(1, testcase1.getNumberOfErrors());
    assertEquals(1, testcase1.getNumberOfFailures());

    assertEquals("testcase", testcase2.getDisplayLabel());
    assertEquals(4, testcase2.getNumberOfTests());
    assertEquals(1, testcase2.getNumberOfErrors());
    assertEquals(2, testcase2.getNumberOfFailures());
    

  }
}
