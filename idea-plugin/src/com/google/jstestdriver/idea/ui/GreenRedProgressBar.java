// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.TestResult.Result;

import java.awt.Color;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class GreenRedProgressBar extends JProgressBar {

  boolean passed;
  private final Color red = Color.decode("#FF6666");
  private final Color green = Color.decode("#66CC66");

  public GreenRedProgressBar() {
    reset();
  }

  public void respondToTestResult(final Result result) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setValue(getValue() + 1);
        if (passed && result != Result.passed) {
          setForeground(red);
        }
      }
    });
  }

  public void setTestsCount(final int testsCount) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        setModel(new DefaultBoundedRangeModel(0, 0, 0, testsCount));
      }
    });
  }

  public void reset() {
    passed = true;
    setValue(0);
    setForeground(green);
  }
}
