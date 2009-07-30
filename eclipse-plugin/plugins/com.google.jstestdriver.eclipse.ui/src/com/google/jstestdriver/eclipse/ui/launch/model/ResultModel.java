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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public abstract class ResultModel {

  public static final String TEST_SUITE_PASS_ICON = "icons/tsuiteok.gif";
  public static final String TEST_SUITE_FAIL_ICON = "icons/tsuiteerror.gif";
  public static final String TEST_PASS_ICON = "icons/testok.gif";
  public static final String TEST_FAIL_ICON = "icons/testfail.gif";
  private final String label;
  protected String passImagePath;
  protected String failImagePath;
  private final ResultModel parent;
  protected Map<String, ResultModel> results;
  protected Icons icon = new Icons();
  
  public ResultModel(ResultModel parent, String label) {
    this.parent = parent;
    this.label = label;
    results = new HashMap<String, ResultModel>();
  }

  public abstract void addTestResult(TestResult result);

  public ResultModel getResultModel(String key) {
    return results.get(key);
  }
  
  public boolean didPass() {
    for (ResultModel test : results.values()) {
      if (!test.didPass()) {
        return false;
      }
    }
    return true;
  }
  
  public String getDisplayLabel() {
    return label;
  }

  public Collection<ResultModel> getChildren() {
    return results.values();
  }

  public ResultModel getParent() {
    return parent;
  }

  public boolean hasChildren() {
    return results.size() > 0;
  }
  
  public void clear() {
    for (ResultModel model : results.values()) {
      model.clear();
    }
    results.clear();
  }
  
  public Image getDisplayImage() {
    if (didPass()) {
      return icon.getImage(passImagePath);
    } else {
      return icon.getImage(failImagePath);
    }
  }
}
