// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.eclipse.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Show the test results.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestResultsPanel extends Composite {

  private Text resultsText;

  public TestResultsPanel(Composite parent, int style) {
    super(parent, style);
    resultsText = new Text(this, SWT.MULTI);
    resultsText.setBounds(0, 0, 288, 239);
  }

  public void addText(String text) {
    resultsText.setText(resultsText.getText() + "\n" + text);
  }
}
