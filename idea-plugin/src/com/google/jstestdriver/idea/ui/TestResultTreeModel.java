// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Swing model that backs the test result tree view.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestResultTreeModel extends DefaultTreeModel {

  ConcurrentMap<BrowserInfo, PassFailNode> browserNodes =
      new ConcurrentHashMap<BrowserInfo, PassFailNode>();
  ConcurrentMap<String, PassFailNode> testCaseNodes =
      new ConcurrentHashMap<String, PassFailNode>();


  public TestResultTreeModel(TreeNode root) {
    super(root);
  }

  /**
   * This method may be called concurrently from multiple test running threads.
   * Any changes to Swing models are therefore done on the AWT Event thread.
   * @param result the test result from a test
   */
  public void addResult(final TestResult result) {
    final BrowserInfo browser = result.getBrowserInfo();
    String outputLog = String.format("%s\n%s\n%s", result.getParsedMessage(), result.getStack(), result.getLog());
    final PassFailNode testNode = new PassFailNode(result.getTestName(), result.getResult(), outputLog);

    if (browserNodes.putIfAbsent(browser, new PassFailNode(browser)) == null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          insertNodeInto(browserNodes.get(browser), (MutableTreeNode) root, root.getChildCount());
        }
      });
    }
    final String testCaseKey = browser + result.getTestCaseName();
    if (testCaseNodes.putIfAbsent(testCaseKey, new PassFailNode(result.getTestCaseName())) == null) {
      SwingUtilities.invokeLater(new Runnable() {
      public void run() {
          insertNodeInto(testCaseNodes.get(testCaseKey), browserNodes.get(browser),
              browserNodes.get(browser).getChildCount());
        }
      });
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (browserNodes.get(browser).childTestResult(result.getResult())) {
          nodeChanged(browserNodes.get(browser));
        }
        PassFailNode testCaseNode = testCaseNodes.get(testCaseKey);
        if (testCaseNode.childTestResult(result.getResult())) {
          nodeChanged(testCaseNode);
        }
        insertNodeInto(testNode, testCaseNode, testCaseNode.getChildCount());
      }
    });

  }

  public void clear() {
    browserNodes.clear();
    testCaseNodes.clear();
    setRoot(new DefaultMutableTreeNode("Test Results Tree"));
    reload();
  }

  public static class PassFailNode extends DefaultMutableTreeNode {
    private Result result;
    private final String outputLog;

    public PassFailNode(Object userObject, Result result, String outputLog) {
      super(userObject);
      this.result = result;
      this.outputLog = outputLog;
    }

    public PassFailNode(Object userObject) {
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
}
