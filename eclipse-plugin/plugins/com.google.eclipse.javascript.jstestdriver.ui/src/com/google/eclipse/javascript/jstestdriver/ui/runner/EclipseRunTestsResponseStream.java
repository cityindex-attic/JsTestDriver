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
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
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
