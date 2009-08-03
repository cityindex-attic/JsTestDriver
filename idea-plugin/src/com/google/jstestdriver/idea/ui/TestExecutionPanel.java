// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.idea.PluginResources;
import com.google.jstestdriver.idea.ui.TestResultTreeModel.PassFailNode;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

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
        add(new JButton(PluginResources.getRerunIcon()) {{
          setToolTipText("Rerun the last test execution");
          setEnabled(false);
        }});
        add(new JButton(PluginResources.getFilterPassedIcon()) {{
          setToolTipText("Filter passing tests");
          setEnabled(false);
        }});
        add(new JButton(PluginResources.getRerunFailedIcon()) {{
          setToolTipText("Rerun failed tests");
          setEnabled(false);
        }});
        add(new JButton(PluginResources.getResetBrowsersIcon()) {{
          setToolTipText("Reset all browsers");
          setEnabled(false);
        }});
      }});
      add(progressBar);
    }}, BorderLayout.NORTH);
    
    add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        new JScrollPane(new TestResultTree(treeModel) {{
          addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
              PassFailNode node = (PassFailNode) e.getPath().getLastPathComponent();
              outputArea.setText(node.getOutput());
            }
          });
        }}),
        new JScrollPane(outputArea)),
        BorderLayout.CENTER);
  }

  public void stream(Response response) {
    Collection<TestResult> testResultCollection = new TestResultGenerator().getTestResults(response);
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
}
