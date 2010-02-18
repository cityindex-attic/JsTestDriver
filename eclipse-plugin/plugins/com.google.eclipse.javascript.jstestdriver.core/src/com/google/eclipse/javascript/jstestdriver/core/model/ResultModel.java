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

import java.util.Collection;

/**
 * Model of the test results from JS Test Driver which is used by the UI to render and display test
 * results returned from JS Test Driver.
 * Each level of test results, like an individual test, a test case, a browser test suite etc
 * needs to implement these common methods for ease of rendering in the tree.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public interface ResultModel {

  /**
   * Did the test (potentially set of tests) pass or not.
   *
   * @return boolean representing whether this set of test results passed or not. A single failure
   *    in a child implies a failure overall.
   */
  boolean passed();

  /**
   * The display label for this collection of results.
   *
   * @return string to represent the test result in a results tree.
   */
  String getDisplayLabel();

  /**
   * Gets all the children of this result model. Empty collection if no children present.
   *
   * @return all the children results of this model
   */
  Collection<? extends ResultModel> getChildren();

  /**
   * Parent of this result model, or {@code null} if it is the root.
   *
   * @return get the parent result model, or {@code null} if this is the root
   */
  ResultModel getParent();

  /**
   * Does this result model have any children.
   *
   * @return false if this the leaf result, true otherwise
   */
  boolean hasChildren();

  /**
   * The absolute path to the image.
   *
   * @return the absolute path to the display image
   */
  String getDisplayImagePath();

  /**
   * Clears all the test results underneath this result model. Recursively clears everything under
   * it first before clearing itself.
   */
  void clear();

  /**
   * Add the JS Test Driver test result as a child. Each type of test result in the
   * tree should handle their labeling themselves.
   *
   * @param result The JS Test Driver test result to be added as a child.
   * @return the leaf result model created
   */
  ResultModel addTestResult(TestResult result);

  /**
   * Total number of tests under this model.
   * @return the total number of tests under this result model. Recurses into its children.
   */
  int getNumberOfTests();

  /**
   * Number of failures under this model.
   *
   * @return the total number of failing tests under this result model. Recurses into its children.
   */
  int getNumberOfFailures();

  /**
   * Number of errors under this model.
   *
   * @return the total number of tests with errors under this result model. Recurses into its
   *     children.
   */
  int getNumberOfErrors();

}