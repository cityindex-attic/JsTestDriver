// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.TestResult.Result;
import com.google.jstestdriver.idea.PluginResources;

import com.intellij.ui.treeStructure.SimpleTree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * The View of the results tree.
 * Should expand to display failed tests, and show test status via icons.
 * Each browser is a root in the tree, with TestCases as children, and tests as grandchildren.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestResultTree extends SimpleTree {

  public TestResultTree(TestResultTreeModel treeModel) {
    super(treeModel);
    setRootVisible(false);
    setShowsRootHandles(true);
    treeModel.addTreeModelListener(new TestResultTreeModelListener());
    setCellRenderer(new TestResultTreeCellRenderer());
  }


  private class TestResultTreeModelListener implements TreeModelListener {
    public void treeNodesInserted(final TreeModelEvent e) {
      if (e.getTreePath().getPath().length < 2) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            expandPath(e.getTreePath());
          }
        });
      }
      if (e.getTreePath().getLastPathComponent() instanceof TestResultTreeNode &&
          ((TestResultTreeNode) e.getTreePath().getLastPathComponent()).getResult() != Result.passed) {
        SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            expandPath(e.getTreePath());
          }
        });
      }
    }

    public void treeNodesChanged(TreeModelEvent e) {}

    public void treeNodesRemoved(TreeModelEvent e) {}

    public void treeStructureChanged(TreeModelEvent e) {}
  }

  private class TestResultTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                  boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
      JLabel label =
          (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      if (value instanceof TestResultTreeNode) {
        TestResultTreeNode node = (TestResultTreeNode) value;
        label.setIcon(resultToIcon(node.getResult()));
      }
      return label;
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
