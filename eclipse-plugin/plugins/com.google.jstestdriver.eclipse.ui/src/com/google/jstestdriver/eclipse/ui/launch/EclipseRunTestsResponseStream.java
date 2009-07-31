// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.eclipse.ui.launch;

import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.views.JsTestDriverView;
import com.google.jstestdriver.eclipse.ui.views.TestResultsPanel;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class EclipseRunTestsResponseStream implements ResponseStream {

  private final TestResultGenerator testResultGenerator;
  private final Logger logger = new Logger();

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
              .showView("com.google.jstestdriver.eclipse.ui.views.JsTestDriverView");
          TestResultsPanel panel = view.getTestResultsPanel();
          for (TestResult testResult : testResults) {
            panel.addResult(testResult);
            panel.refresh();
          }
        } catch (PartInitException e) {
          logger.logException(e);
        }
      }
    });
  }

}
