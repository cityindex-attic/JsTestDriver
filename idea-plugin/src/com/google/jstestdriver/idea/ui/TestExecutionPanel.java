// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTree;

/**
 * The section of the Tool Window which shows the test results and controls test execution.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestExecutionPanel extends JPanel {
  JTree resultsTree = new JTree();

  public TestExecutionPanel() {
    setLayout(new BorderLayout());
    add(new JPanel() {{
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      add(new JPanel() {{
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(new JButton("Rerun"));
        add(new JButton("Rerun failed"));
        add(new JButton("Filter passed"));
        add(new JButton("Reload browsers"));
      }});
      add(new JProgressBar());
    }}, BorderLayout.NORTH);
    add(resultsTree, BorderLayout.CENTER);
    add(new JTextArea("Log output from Firefox 3.0 Linux:\nPASSED: some log output"),
        BorderLayout.SOUTH);
  }
}
