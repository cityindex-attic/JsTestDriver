package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.TestResult.Result;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * In the test results tree UI, this is an element representing a browser. It will have results for that browser as
 * children nodes.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class BrowserNode {
  public Map<String, TestCaseNode> testCaseMap = new HashMap<String, TestCaseNode>();
  public final SMTestProxy node;
  private Result worstResult = Result.passed;

  public BrowserNode(SMTestProxy node) {
    this.node = node;
  }

  public boolean allTestCasesComplete() {
    for (TestCaseNode testCaseNode : testCaseMap.values()) {
      if (!testCaseNode.allTestsComplete()) {
        return false;
      }
    }
    return true;
  }

  public void setTestFailed(Result result) {
    if (result == Result.error && worstResult != Result.error) {
      node.setTestFailed("", "", true);
    } else if (result == Result.failed && worstResult == Result.passed) {
      node.setTestFailed("", "", false);
    }
  }
}
