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

import com.google.jstestdriver.TestResult.Result;

import javax.swing.*;
import java.awt.*;

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

  /**
   * TODO This method should only be called on the event thread
   * @param result
   */
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

  /**
   * TODO This method should only be called on the event thread
   * @param testsCount
   */
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
