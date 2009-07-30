// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.eclipse.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.eclipse.ui.launch.model.EclipseJstdTestRunResult;

/**
 * Show the test results.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestResultsPanel extends Composite {

  private EclipseJstdTestRunResult testRunResult;
  private TreeViewer testResultsTree;

  public TestResultsPanel(Composite parent, int style) {
    super(parent, style);
    testRunResult = new EclipseJstdTestRunResult();
    testResultsTree = new TreeViewer(this, SWT.NONE);
    testResultsTree.getTree().setBounds(5, 7, 245, 290);
    testResultsTree.setLabelProvider(new TestResultsTreeLabelProvider());
    testResultsTree.setContentProvider(new TestResultsTreeContentProvider());
    testResultsTree.setSorter(new NameSorter());
    testResultsTree.setAutoExpandLevel(3);
    testResultsTree.setInput(testRunResult);
  }

  
  public void clearTestRun() {
    testRunResult.clear();
    testResultsTree.refresh();
  }

  public void addResult(TestResult testResult) {
    testRunResult.addTestResult(testResult);
  }


  public void refresh() {
    testResultsTree.refresh();
  }
}
