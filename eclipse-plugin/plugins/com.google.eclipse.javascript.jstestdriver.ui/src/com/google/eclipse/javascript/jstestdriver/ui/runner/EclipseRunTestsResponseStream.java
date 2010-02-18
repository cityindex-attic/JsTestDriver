// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.eclipse.javascript.jstestdriver.ui.runner;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.eclipse.javascript.jstestdriver.ui.view.JsTestDriverView;
import com.google.eclipse.javascript.jstestdriver.ui.view.TestResultsPanel;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseRunTestsResponseStream implements ResponseStream {

  private final TestResultGenerator testResultGenerator;
  private final Logger logger =
      Logger.getLogger(EclipseRunTestsResponseStream.class.getName());

  public EclipseRunTestsResponseStream(TestResultGenerator testResultGenerator) {
    this.testResultGenerator = testResultGenerator;
  }

  public void finish() {
  }

  public void stream(Response response) {
    final Collection<TestResult> testResults = testResultGenerator
        .getTestResults(response);
    Display.getDefault().asyncExec(new Runnable() {

      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          JsTestDriverView view = (JsTestDriverView) page
              .showView(JsTestDriverView.ID);
          TestResultsPanel panel = view.getTestResultsPanel();
          panel.addTestResults(testResults);
        } catch (PartInitException e) {
          logger.log(Level.SEVERE, "", e);
        }
      }
    });
  }
}
