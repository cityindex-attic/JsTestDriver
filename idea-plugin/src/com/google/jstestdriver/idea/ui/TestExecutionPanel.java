// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.idea.PluginResources;
import com.google.jstestdriver.idea.ui.TestResultTreeModel.PassFailNode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * The section of the Tool Window which shows the test results and controls test execution.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestExecutionPanel extends JPanel implements ResponseStream {

  private TestResultTreeModel treeModel = new TestResultTreeModel(
      new DefaultMutableTreeNode("Test Results Tree"));
  private JTextArea outputArea = new JTextArea();
  private GreenRedProgressBar progressBar = new GreenRedProgressBar();

  public TestExecutionPanel() {
    setLayout(new BorderLayout());
    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(new JPanel() {{
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(new JButton(PluginResources.getRerunIcon()));
        add(new JButton(PluginResources.getFilterPassedIcon()));
        add(new JButton(PluginResources.getRerunFailedIcon()));
        add(new JButton(PluginResources.getResetBrowsersIcon()));
      }});
      add(progressBar);
    }}, BorderLayout.NORTH);
    add(new JPanel() {{
      setLayout(new GridLayout(2,1));
      add(new JTree(treeModel) {{
        setRootVisible(false);
        treeModel.addTreeModelListener(new TestResultTreeModelListener(this));
        addTreeSelectionListener(new TreeSelectionListener() {
          public void valueChanged(TreeSelectionEvent e) {
            PassFailNode node = (PassFailNode) e.getPath().getLastPathComponent();
            outputArea.setText(node.getOutput());
          }
        });
        setCellRenderer(new TestResultTreeCellRenderer());
      }});
      add(outputArea);
    }}, BorderLayout.CENTER);
  }

  public void stream(Response response) {
    Collection<TestResult> testResultCollection =
        new TestResultGenerator().getTestResults(response);
    for (TestResult result : testResultCollection) {
      progressBar.respondToTestResult(result.getResult());
      treeModel.addResult(result);
    }
  }

  public void finish() {
  }

  public void clearTestResults() {
    treeModel.clear();
    progressBar.reset();
  }

  public void setTestsCount(int testsCount) {
    progressBar.setTestsCount(testsCount);
  }

  private class TestResultTreeModelListener implements TreeModelListener {

    private final JTree tree;

    public TestResultTreeModelListener(JTree tree) {
      this.tree = tree;
    }

    public void treeNodesChanged(TreeModelEvent e) {
    }

    public void treeNodesInserted(TreeModelEvent e) {
      TreePath path = e.getTreePath();
      // path element 0 is the root, a length of 1 is a browser node
      if (path.getPath().length < 2) {
        tree.expandPath(path);
      }
      if (path.getLastPathComponent() instanceof PassFailNode) {
        PassFailNode node = (PassFailNode) path.getLastPathComponent();
        if (node.getResult() != Result.passed) {
          tree.expandPath(path);
        }
      }
    }

    public void treeNodesRemoved(TreeModelEvent e) {
    }

    public void treeStructureChanged(TreeModelEvent e) {
    }
  }

  private class TestResultTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
      Component component = 
          super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      if (value instanceof PassFailNode) {
        PassFailNode node = (PassFailNode) value;
        setIcon(resultToIcon(node.getResult()));
      }
      return component;
    }

    private Icon resultToIcon(Result result) {
      switch(result) {
        case error:
          return PluginResources.getErroredTestIcon();
        case failed:
          return PluginResources.getFailedTestIcon();
        case passed:
          return PluginResources.getPassedTestIcon();
        default:
          throw new IllegalArgumentException("Unknown result " + result);
      }
    }
  }
}
