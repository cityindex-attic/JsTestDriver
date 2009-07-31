// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.idea.PluginResources;
import com.google.jstestdriver.idea.ui.TestResultTreeModel.PassFailNode;

import static com.intellij.openapi.util.IconLoader.findIcon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * The section of the Tool Window which shows the test results and controls test execution.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestExecutionPanel extends JPanel implements ResponseStream {

  private TestResultTreeModel treeModel = new TestResultTreeModel(
      new DefaultMutableTreeNode("Test Results Tree"));

  public TestExecutionPanel() {
    setLayout(new BorderLayout());
    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(new JPanel() {{
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(new JButton(findIcon("/actions/refreshUsages.png")));
        add(new JButton(findIcon("/runConfigurations/rerunFailedTests.png")));
        add(new JButton(findIcon("/runConfigurations/hidePassed.png")));
        add(new JButton(findIcon("/actions/reset.png")));
      }});
      add(new JProgressBar());
    }}, BorderLayout.NORTH);
    add(new JTree(treeModel) {{
      setRootVisible(false);
      treeModel.addTreeModelListener(new TestResultTreeModelListener(this));
      setCellRenderer(new TestResultTreeCellRenderer());
    }}, BorderLayout.CENTER);
    add(new JTextArea("Log output from Firefox 3.0 Linux:\nPASSED: some log output"),
        BorderLayout.SOUTH);
  }

  public void stream(Response response) {
    Collection<TestResult> testResultCollection =
        new TestResultGenerator().getTestResults(response);
    for (TestResult result : testResultCollection) {
      treeModel.addResult(result);
    }
  }

  public void finish() {
  }

  public void clearTestResults() {
    treeModel.clear();
  }

  private class TestResultTreeModelListener implements TreeModelListener {

    private final JTree tree;

    public TestResultTreeModelListener(JTree tree) {
      this.tree = tree;
    }

    public void treeNodesChanged(TreeModelEvent e) {
    }

    public void treeNodesInserted(TreeModelEvent e) {
      tree.expandPath(e.getTreePath());
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
