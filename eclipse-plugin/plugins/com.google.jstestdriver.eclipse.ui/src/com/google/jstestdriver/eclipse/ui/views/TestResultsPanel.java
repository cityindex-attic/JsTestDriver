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
package com.google.jstestdriver.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.internal.junit.ui.JUnitProgressBar;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.eclipse.ui.icon.Icons;
import com.google.jstestdriver.eclipse.ui.launch.model.EclipseJstdTestResult;
import com.google.jstestdriver.eclipse.ui.launch.model.EclipseJstdTestRunResult;
import com.google.jstestdriver.eclipse.ui.launch.model.ResultModel;

/**
 * Show the test results.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestResultsPanel extends Composite {

  private Icons icons;
  private EclipseJstdTestRunResult testRunResult;
  private TreeViewer testResultsTree;
  private JUnitProgressBar testProgressIndicator;
  private Label failuresLabel;
  private Label errorsLabel;
  private Label totalRunLabel;
  private Button rerunFailedFirstButton;
  private Button rerunButton;
  private Button showOnlyFailedButton;
  private Text testDetailsText;
  private int totalNumTests;
  private ViewerFilter showOnlyFailuresFilter;

  public TestResultsPanel(Composite parent, int style) {
    super(parent, style);
    icons = new Icons();
    showOnlyFailuresFilter = new FailureOnlyViewerFilter();
    testRunResult = new EclipseJstdTestRunResult();
    testResultsTree = new TreeViewer(this, SWT.NONE);
    testResultsTree.getTree().setBounds(0, 95, 281, 242);
    testResultsTree.setLabelProvider(new TestResultsTreeLabelProvider());
    testResultsTree.setContentProvider(new TestResultsTreeContentProvider());
    testResultsTree.setInput(testRunResult);
    testProgressIndicator = new JUnitProgressBar(this);
    testProgressIndicator.setBounds(0, 59, 275, 30);
    testDetailsText = new Text(this, SWT.MULTI | SWT.WRAP);
    testDetailsText.setBounds(0, 343, 281, 58);
    showOnlyFailedButton = new Button(this,SWT.TOGGLE);
    showOnlyFailedButton.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        if (showOnlyFailedButton.getSelection()) {
          testResultsTree.setFilters(new ViewerFilter[] {showOnlyFailuresFilter});
        } else {
          testResultsTree.setFilters(new ViewerFilter[0]);
        }
      }
      
    });
    showOnlyFailedButton.setImage(icons.getImage("icons/failures.gif"));
    showOnlyFailedButton.setBounds(46, 0, 60, 30);
    rerunButton = new Button(this, SWT.NONE);
    rerunButton.setImage(icons.getImage("icons/relaunch.gif"));
    rerunButton.setBounds(106, 0, 60, 30);
    rerunFailedFirstButton = new Button(this, SWT.CENTER);
    rerunFailedFirstButton.setText("RFF");
    rerunFailedFirstButton.setBounds(166, 0, 60, 30);
    totalRunLabel = new Label(this, SWT.NONE);
    totalRunLabel.setText("Run : 0 / 0");
    totalRunLabel.setBounds(6, 36, 90, 17);
    errorsLabel = new Label(this, SWT.NONE);
    errorsLabel.setText("Errors : 0");
    errorsLabel.setBounds(116, 36, 60, 17);
    failuresLabel = new Label(this, SWT.NONE);
    failuresLabel.setText("Failed : 0");
    failuresLabel.setBounds(190, 36, 75, 17);
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

  public void setupForNextTestRun() {
    testRunResult.clear();
    testResultsTree.refresh();
    testProgressIndicator.reset();
    totalNumTests = 0;
    totalRunLabel.setText("Run : 0 / 0");
    errorsLabel.setText("Errors : 0");
    failuresLabel.setText("Failed : 0");
  }
  
  public synchronized void addNumberOfTests(int numTests) {
    totalNumTests += numTests;
    testProgressIndicator.setMaximum(totalNumTests);
    totalRunLabel.setText("Run : 0 / " + totalNumTests);
  }

  public synchronized void addTestResults(Collection<TestResult> testResults) {
    Collection<ResultModel> failedTests = new ArrayList<ResultModel>();
    for (TestResult result : testResults) {
      testProgressIndicator.step(result.getResult() == TestResult.Result.passed ? 0 : 1);
      ResultModel addedResult = testRunResult.addTestResult(result);
      if (!addedResult.didPass()) {
        failedTests.add(addedResult);
      }
    }
    testResultsTree.refresh();
    
    for (ResultModel resultModel : failedTests) {
      testResultsTree.expandToLevel(resultModel, TreeViewer.ALL_LEVELS);
    }
    errorsLabel.setText("Errors : " + testRunResult.getNumberOfErrors());
    failuresLabel.setText("Failed : " + testRunResult.getNumberOfFailures());
    totalRunLabel.setText("Run : " + testRunResult.getNumberOfTests() + " / " + totalNumTests);
  }
}
