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

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * Base class for all test results, including individual tests, test cases and browsers.
 *
 * The heirarchy of results which extend from this, from root to leaf is as follows :
 *
 * <pre>
 * {@link EclipseJstdTestRunResult}
 * -> {@link EclipseJstdBrowserRunResult}
 *    -> {@link EclipseJstdTestCaseResult}
 *       -> {@link EclipseJstdTestResult}
 * </pre>
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public abstract class BaseResultModel implements ResultModel {

  private static final String TEST_SUITE_PASS_ICON = "/icons/tsuiteok.gif";
  private static final String TEST_SUITE_FAIL_ICON = "/icons/tsuitefail.gif";
  private static final String TEST_SUITE_ERROR_ICON = "/icons/tsuiteerror.gif";

  private final String label;

  private final ResultModel parent;
  private final Map<String, BaseResultModel> results = Maps.newHashMap();

  /**
   * Constructs a {@link BaseResultModel} with the given parent and label.
   * @param parent the parent of this result model instance
   * @param label the label to be displayed when rendering the result model.
   */
  public BaseResultModel(ResultModel parent, String label) {
    this.parent = parent;
    this.label = label;
  }

  protected ResultModel getResultModel(String key) {
    return results.get(key);
  }

  protected void addChildResult(String key, BaseResultModel model) {
    results.put(key, model);
  }

  @Override
  public boolean passed() {
    for (ResultModel test : results.values()) {
      if (!test.passed()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int getNumberOfTests() {
    int total = 0;
    for (BaseResultModel model : results.values()) {
      total += model.getNumberOfTests();
    }
    return total;
  }

  @Override
  public int getNumberOfFailures() {
    int total = 0;
    for (BaseResultModel model : results.values()) {
      total += model.getNumberOfFailures();
    }
    return total;
  }

  @Override
  public int getNumberOfErrors() {
    int total = 0;
    for (BaseResultModel model : results.values()) {
      total += model.getNumberOfErrors();
    }
    return total;
  }

  @Override
  public String getDisplayLabel() {
    return label + " (" + getTotalTimeTaken() + " ms)";
  }

  @Override
  public Collection<BaseResultModel> getChildren() {
    return results.values();
  }

  @Override
  public ResultModel getParent() {
    return parent;
  }

  @Override
  public boolean hasChildren() {
    return results.size() > 0;
  }

  @Override
  public void clear() {
    for (ResultModel model : results.values()) {
      model.clear();
    }
    results.clear();
  }

  @Override
  public String getDisplayImagePath() {
    if (passed()) {
      return TEST_SUITE_PASS_ICON;
    } else if (getNumberOfErrors() > 0){
      return TEST_SUITE_ERROR_ICON;
    } else {
      return TEST_SUITE_FAIL_ICON;
    }
  }

  /**
   * Total time taken in milliseconds for everything under this test result to finish.
   *
   * @return the total time taken for everything under this test result in milliseconds.
   */
  protected float getTotalTimeTaken() {
    float total = 0f;
    for (ResultModel model : results.values()) {
      total += ((BaseResultModel) model).getTotalTimeTaken();
    }
    return total;
  }
  @Override
  public String toString() {
    return "BaseResultModel [label=" + label + ", parent=" + parent.getClass().getName()
    + ", results=" + results.size() + "]";
  }
}