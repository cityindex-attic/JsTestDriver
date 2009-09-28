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

import com.google.jstestdriver.ActionRunner;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.idea.PluginResources;
import com.google.jstestdriver.idea.TestRunnerState;
import com.intellij.execution.ExecutionException;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * The section of the Tool Window which shows the test results and controls test execution.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestExecutionPanel extends JPanel implements ResponseStream {

  private TestResultTreeModel treeModel = new TestResultTreeModel(new TestResultTreeNode("root"));
  private JTextArea outputArea = new JTextArea();
  private GreenRedProgressBar progressBar = new GreenRedProgressBar();
  private TestRunnerState testRunner;
  private ActionRunner resetRunner;
  private TestExecutionPanel.RerunTestsAction resetAction = new RerunTestsAction();

  public TestExecutionPanel() {
    setLayout(new BorderLayout());

    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(new JPanel() {{
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(new JButton(resetAction));
        add(new JToggleButton(new FilterPassingTestsAction()));
        add(new JButton(new RerunFailedTestsAction()));
        add(new JButton(new ResetBrowsersAction()));
      }});
      add(progressBar);
    }}, BorderLayout.NORTH);
    
    add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        new JScrollPane(new TestResultTree(treeModel) {{
          addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
              TestResultTreeNode node = (TestResultTreeNode) e.getPath().getLastPathComponent();
              outputArea.setText(node.getOutput());
            }
          });
        }}),
        new JScrollPane(outputArea)),
        BorderLayout.CENTER);
  }

  private class FilterPassingTestsAction extends AbstractAction {
    FilterPassingTestsAction() {
      super("", PluginResources.getFilterPassedIcon());
      putValue(SHORT_DESCRIPTION, "Filter passing tests");
    }
    public void actionPerformed(ActionEvent e) {
      if (((JToggleButton)e.getSource()).getModel().isSelected()) {
        treeModel.setPassingTestsFilter(true);
      } else {
        treeModel.setPassingTestsFilter(false);
      }
    }
  }

  private class RerunTestsAction extends AbstractAction {
    RerunTestsAction() {
      super("", PluginResources.getRerunIcon());
      putValue(SHORT_DESCRIPTION, "Rerun the last test execution");
    }
    public void actionPerformed(ActionEvent e) {
      if (testRunner != null) {
        try {
          testRunner.execute(null, null);
        } catch (ExecutionException e1) {
          throw new RuntimeException(e1);
        }
      }
    }
  }

  private class RerunFailedTestsAction extends AbstractAction {
    RerunFailedTestsAction() {
      super("", PluginResources.getRerunFailedIcon());
      putValue(SHORT_DESCRIPTION, "Rerun failed tests");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
    }
  }

  private class ResetBrowsersAction extends AbstractAction {
    ResetBrowsersAction() {
      super("", PluginResources.getResetBrowsersIcon());
      putValue(SHORT_DESCRIPTION, "Reset all browsers");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
      if (resetRunner != null) {
        resetRunner.runActions();
      }
    }
  }

  public void stream(Response response) {
    // run these on the AWt event thread?
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

  public void setTestRunner(TestRunnerState testRunner) {
    this.testRunner = testRunner;
  }


  public void setResetRunner(ActionRunner resetRunner) {
    this.resetRunner = resetRunner;
    resetAction.setEnabled(true);

  }
}
