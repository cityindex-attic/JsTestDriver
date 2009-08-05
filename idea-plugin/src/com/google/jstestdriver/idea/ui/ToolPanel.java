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
import com.google.jstestdriver.DryRunInfo;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.TestResultPrinter;
import com.google.jstestdriver.idea.TestRunnerState;

import com.intellij.util.ui.UIUtil;

import java.awt.BorderLayout;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.swing.BorderFactory.createTitledBorder;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ToolPanel extends JPanel {

  private TestExecutionPanel testExecutionPanel = new TestExecutionPanel();
  private final AtomicInteger totalTests = new AtomicInteger(0);

  public ToolPanel() {
    setLayout(new BorderLayout());
    setBackground(UIUtil.getTreeTextBackground());
    add(new ServerControlPanel() {{
      setBorder(createTitledBorder("Server"));
    }}, BorderLayout.NORTH);
    add(testExecutionPanel, BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(new ToolPanel());
    frame.pack();
    frame.setVisible(true);
  }

  public ResponseStream getTestResultStream() {
    return testExecutionPanel;
  }

  public void clearTestResults() {
    testExecutionPanel.clearTestResults();
    totalTests.set(0);
  }

  public void setTestsCount(int i) {
    testExecutionPanel.setTestsCount(i);
  }

  public ResponseStreamFactory createResponseStreamFactory() {
    return new ResponseStreamFactory() {
      public ResponseStream getRunTestsActionResponseStream(String browserId) {
        return getTestResultStream();
      }

      public ResponseStream getDryRunActionResponseStream() {
        return new ResponseStream() {
          public void stream(Response response) {
            DryRunInfo info = DryRunInfo.fromJson(response);
            totalTests.addAndGet(info.getNumTests());
          }

          public void finish() {
          }
        };
      }

      public ResponseStream getEvalActionResponseStream() {
        return null;
      }

      public ResponseStream getResetActionResponseStream() {
        return null;
      }
    };
  }

  public void dryRunComplete() {
    setTestsCount(totalTests.get());
  }

  public void setTestRunner(TestRunnerState testRunnerState) {
    testExecutionPanel.setTestRunner(testRunnerState);
  }

  public void setResetRunner(ActionRunner resetRunner) {
    testExecutionPanel.setResetRunner(resetRunner);
  }
}
