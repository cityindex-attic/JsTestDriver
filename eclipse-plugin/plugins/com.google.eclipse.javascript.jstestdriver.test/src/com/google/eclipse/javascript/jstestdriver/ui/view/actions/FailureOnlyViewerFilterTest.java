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
package com.google.eclipse.javascript.jstestdriver.ui.view.actions;

import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.jface.viewers.ViewerFilter;

import com.google.eclipse.javascript.jstestdriver.core.model.EclipseJstdBrowserRunResult;
import com.google.eclipse.javascript.jstestdriver.core.model.ResultModel;
import com.google.eclipse.javascript.jstestdriver.ui.view.actions.FailureOnlyViewerFilter;
import com.google.jstestdriver.TestResult;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class FailureOnlyViewerFilterTest extends TestCase {
  TestResult passing1 = new TestResult(null, "passed", "", "", "testcase1", "test1", 0f);
  TestResult passing2 = new TestResult(null, "passed", "", "", "testcase", "test2", 0f);
  TestResult failed1 = new TestResult(null, "failed", "", "", "testcase", "test3", 0f);
  TestResult failed2 = new TestResult(null, "failed", "", "", "testcase", "test4", 0f);
  TestResult error1 = new TestResult(null, "error", "", "", "testcase2", "test6", 0f);
  TestResult error2 = new TestResult(null, "error", "", "", "testcase", "test7", 0f);

  public void testSelect() throws Exception {
    ViewerFilter filter = new FailureOnlyViewerFilter();
    ResultModel model = new EclipseJstdBrowserRunResult(null, "browser");

    model.addTestResult(passing1);
    model.addTestResult(passing2);
    model.addTestResult(failed1);
    model.addTestResult(failed2);
    model.addTestResult(error1);
    model.addTestResult(error2);

    Iterator<? extends ResultModel> iterator = model.getChildren().iterator();
    ResultModel testcase1 = iterator.next();
    ResultModel testcase3 = iterator.next();
    ResultModel testcase2 = iterator.next();
    
    assertTrue(filter.select(null, null, model));
    assertTrue(filter.select(null, null, testcase1));
    assertTrue(filter.select(null, null, testcase2));
    assertFalse(filter.select(null, null, testcase3));
    
    assertTrue(filter.select(null, null, testcase1.getChildren().iterator().next()));
    
    iterator = testcase2.getChildren().iterator();
    assertTrue(filter.select(null, null, iterator.next()));
    assertTrue(filter.select(null, null, iterator.next()));
    assertFalse(filter.select(null, null, iterator.next()));
    
    assertFalse(filter.select(null, null, testcase3.getChildren().iterator().next()));
  }
}
