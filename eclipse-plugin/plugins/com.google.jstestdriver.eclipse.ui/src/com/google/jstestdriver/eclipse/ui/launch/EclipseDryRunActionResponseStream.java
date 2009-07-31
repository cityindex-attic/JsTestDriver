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
package com.google.jstestdriver.eclipse.ui.launch;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.jstestdriver.DryRunInfo;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.views.JsTestDriverView;
import com.google.jstestdriver.eclipse.ui.views.TestResultsPanel;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 *
 */
public class EclipseDryRunActionResponseStream implements ResponseStream {

  protected Logger logger = new Logger();

  public void finish() {
  }

  public void stream(Response response) {
    final DryRunInfo info = DryRunInfo.fromJson(response);
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          JsTestDriverView view = (JsTestDriverView) page
              .showView("com.google.jstestdriver.eclipse.ui.views.JsTestDriverView");
          TestResultsPanel panel = view.getTestResultsPanel();
          panel.addNumberOfTests(info.getNumTests());
        } catch (PartInitException e) {
          logger .logException(e);
        }
      }
    });
  }

}
