// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.TestResult.Result;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author alexeagle@google.com (Alex Eagle)
*/
public class TestResultTreeNode extends DefaultMutableTreeNode {
  private Result result;
  private final String outputLog;

  public TestResultTreeNode(Object userObject, Result result, String outputLog) {
    super(userObject);
    this.result = result;
    this.outputLog = outputLog;
  }

  public TestResultTreeNode(Object userObject) {
    this(userObject, Result.passed, "");
  }

  public Result getResult() {
    return result;
  }

  /**
   * Let the node respond to a child result
   * @param childResult the result of a child of this node
   * @return true iff this node's result was changed
   */
  public boolean childTestResult(Result childResult) {
    if (result == Result.passed && childResult != Result.passed) {
      result = childResult;
      return true;
    }
    if (result == Result.failed && childResult == Result.error) {
      result = childResult;
      return true;
    }
    return false;
  }

  public String getOutput() {
    return outputLog;
  }
}
