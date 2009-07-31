// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.eclipse.ui.views;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.eclipse.ui.icon.Icons;
import com.google.jstestdriver.eclipse.ui.launch.model.EclipseJstdTestResult;
import com.google.jstestdriver.eclipse.ui.launch.model.EclipseJstdTestRunResult;
import com.google.jstestdriver.eclipse.ui.launch.model.ResultModel;
import org.eclipse.jface.dialogs.ProgressIndicator;

/**
 * Show the test results.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestResultsPanel extends Composite {

  private Icons icons;
  private EclipseJstdTestRunResult testRunResult;
  private TreeViewer testResultsTree;
  private ProgressIndicator testProgressIndicator;
  private Label failuresLabel;
  private Label errorsLabel;
  private Label totalRunLabel;
  private Button rerunFailedFirstButton;
  private Button rerunButton;
  private Button showOnlyFailed;
  private Text testDetailsText;

  public TestResultsPanel(Composite parent, int style) {
    super(parent, style);
    icons = new Icons();
    testRunResult = new EclipseJstdTestRunResult();
    testResultsTree = new TreeViewer(this, SWT.NONE);
    testResultsTree.getTree().setBounds(0, 95, 281, 242);
    testResultsTree.setLabelProvider(new TestResultsTreeLabelProvider());
    testResultsTree.setContentProvider(new TestResultsTreeContentProvider());
    testResultsTree.setSorter(new NameSorter());
    testResultsTree.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
    testResultsTree.setInput(testRunResult);
    testProgressIndicator = new ProgressIndicator(this);
    testProgressIndicator.setBounds(0, 59, 275, 30);
    testProgressIndicator.beginTask(100);
    testDetailsText = new Text(this, SWT.MULTI | SWT.WRAP);
    testDetailsText.setBounds(0, 343, 281, 58);
    showOnlyFailed = new Button(this, SWT.NONE);
    showOnlyFailed.setImage(icons.getImage("icons/failures.gif"));
    showOnlyFailed.setBounds(46, 0, 60, 30);
    rerunButton = new Button(this, SWT.NONE);
    rerunButton.setImage(icons.getImage("icons/relaunch.gif"));
    rerunButton.setBounds(106, 0, 60, 30);
    rerunFailedFirstButton = new Button(this, SWT.CENTER);
    rerunFailedFirstButton.setText("RFF");
    rerunFailedFirstButton.setBounds(166, 0, 60, 30);
    totalRunLabel = new Label(this, SWT.NONE);
    totalRunLabel.setText("Run : 0 / 0");
    totalRunLabel.setBounds(6, 36, 70, 17);
    errorsLabel = new Label(this, SWT.NONE);
    errorsLabel.setText("Errors : 0");
    errorsLabel.setBounds(106, 36, 60, 17);
    failuresLabel = new Label(this, SWT.NONE);
    failuresLabel.setText("Failed : 0");
    failuresLabel.setBounds(180, 36, 75, 17);
    testResultsTree.addDoubleClickListener(new IDoubleClickListener() {

      public void doubleClick(DoubleClickEvent event) {
        TreeSelection selection = (TreeSelection) event.getSelection();
        if (selection.getFirstElement() instanceof EclipseJstdTestResult) {
          EclipseJstdTestResult result = (EclipseJstdTestResult) selection
              .getFirstElement();
          String detailsString = "";
          if (!result.getResult().getMessage().trim().equals("")) {
            detailsString = "Message : " + result.getResult().getMessage() + "\n";
          }
          detailsString += "Log : " + result.getResult().getLog();
          testDetailsText.setText(detailsString);
        }
      }
    });
  }

  public void clearTestRun() {
    testRunResult.clear();
    testProgressIndicator.beginTask(100);
    testResultsTree.refresh();
  }

  public synchronized void addResult(TestResult testResult) {
    testProgressIndicator.worked(10);
    testRunResult.addTestResult(testResult);
    errorsLabel.setText("Errors : " + testRunResult.getNumberOfErrors());
    failuresLabel.setText("Failed : " + testRunResult.getNumberOfFailures());
    totalRunLabel.setText("Run : " + testRunResult.getNumberOfTests() + " / " + testRunResult.getNumberOfTests());
  }

  public void refresh() {
    testResultsTree.refresh();
  }
}
