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
package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Swing model that backs the test result tree view.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestResultTreeModel extends DefaultTreeModel {

  private ConcurrentMap<BrowserInfo, TestResultTreeNode> browserNodes =
      new ConcurrentHashMap<BrowserInfo, TestResultTreeNode>();
  private ConcurrentMap<String, TestResultTreeNode> testCaseNodes =
      new ConcurrentHashMap<String, TestResultTreeNode>();
  private boolean passingTestsFiltered = false;


  public TestResultTreeModel(TestResultTreeNode root) {
    super(root);
  }

  @Override
  public Object getChild(Object parent, int index) {
    if (!passingTestsFiltered) {
      return super.getChild(parent, index);
    }
    TreeNode node = (TreeNode) parent;
    int count = 0;
    for (int i=0; i<node.getChildCount(); i++) {
      TestResultTreeNode child = (TestResultTreeNode) node.getChildAt(i);
      if (child.getResult() != Result.passed) {
        if (count == index) {
          return child;
        }
        count++;
      }
    }
    throw new ArrayIndexOutOfBoundsException("Index " + index + " out of range");
  }

  @Override
  public int getChildCount(Object parent) {
    if (!passingTestsFiltered) {
      return super.getChildCount(parent);
    }
    TreeNode node = (TreeNode) parent;
    int count = 0;
    for (int i=0; i<node.getChildCount(); i++) {
      TestResultTreeNode child = (TestResultTreeNode) node.getChildAt(i);
      if (child.getResult() != Result.passed) {
        count++;
      }
    }
    return count;
  }

  /**
   * This method may be called concurrently from multiple test running threads.
   * Any changes to Swing models are therefore done on the AWT Event thread.
   * @param result the test result from a test
   */
  public void addResult(final TestResult result) {
    final BrowserInfo browser = result.getBrowserInfo();
    String outputLog = String.format("%s\n%s\n%s", result.getParsedMessage(), result.getStack(), result.getLog());

    if (browserNodes.putIfAbsent(browser, new TestResultTreeNode(browser)) == null) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          insertNodeInto(browserNodes.get(browser), (MutableTreeNode) root, root.getChildCount());
        }
      });
    }

    final String testCaseKey = browser + result.getTestCaseName();
    if (testCaseNodes.putIfAbsent(testCaseKey, new TestResultTreeNode(result.getTestCaseName())) == null) {
      SwingUtilities.invokeLater(new Runnable() {
      public void run() {
          insertNodeInto(testCaseNodes.get(testCaseKey), browserNodes.get(browser),
              browserNodes.get(browser).getChildCount());
        }
      });
    }
    
    final TestResultTreeNode testNode = new TestResultTreeNode(result.getTestName(), result.getResult(), outputLog);
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (browserNodes.get(browser).childTestResult(result.getResult())) {
          nodeChanged(browserNodes.get(browser));
        }
        TestResultTreeNode testCaseNode = testCaseNodes.get(testCaseKey);
        if (testCaseNode.childTestResult(result.getResult())) {
          nodeChanged(testCaseNode);
        }
        insertNodeInto(testNode, testCaseNode, testCaseNode.getChildCount());
      }
    });
  }

  public void setPassingTestsFilter(boolean filter) {
    passingTestsFiltered = filter;
    nodeStructureChanged(root);
  }

  public void clear() {
    browserNodes.clear();
    testCaseNodes.clear();
    setRoot(new TestResultTreeNode("Test Results Tree"));
    reload();
  }

}
