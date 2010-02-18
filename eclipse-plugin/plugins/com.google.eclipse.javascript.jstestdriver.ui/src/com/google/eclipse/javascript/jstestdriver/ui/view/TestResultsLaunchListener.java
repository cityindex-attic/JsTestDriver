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

package com.google.eclipse.javascript.jstestdriver.ui.view;

import com.google.eclipse.javascript.jstestdriver.core.JstdLaunchListener;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 *
 */
public class TestResultsLaunchListener implements JstdLaunchListener {

  private static final Logger logger =
      Logger.getLogger(TestResultsLaunchListener.class.getCanonicalName());
  @Override
  public void aboutToLaunch(final ILaunchConfiguration launchConfiguration) {
    Display.getDefault().asyncExec(new Runnable() {

      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          JsTestDriverView view = (JsTestDriverView) page.showView(JsTestDriverView.ID);
          view.getTestResultsPanel().setupForNextTestRun(launchConfiguration);
        } catch (PartInitException e) {
          logger.log(Level.WARNING, "PartInitException trying to show JsTestDriverView for new run",
              e);
        }
      }
    });
  }
}
