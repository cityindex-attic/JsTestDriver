package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.TestResult.Result;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * In the test results tree UI, this is an element representing a test case result.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestCaseNode {
  public Map<String, SMTestProxy> testProxyMap = new HashMap<String, SMTestProxy>();
  public final SMTestProxy node;
  // Determines how we represent the node. If any children fail, for example, the parent is also marked failed.
  private Result worstResult = Result.passed;

  public TestCaseNode(SMTestProxy node) {
    this.node = node;
  }

  public boolean allTestsComplete() {
    for (SMTestProxy testProxy : testProxyMap.values()) {
      if (testProxy.isInProgress()) {
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
