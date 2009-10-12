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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.Activator;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 * 
 */
public class RerunLastLaunchActionDelegate implements IViewActionDelegate {

  private TestResultsPanel view;
  private final Logger logger = new Logger();

  public void init(IViewPart view) {
    if (view instanceof JsTestDriverView) {
      this.view = ((JsTestDriverView) view).getTestResultsPanel();
    }
  }

  public void run(IAction action) {
    if (Server.getInstance() == null || !Server.getInstance().isStarted()) {
      IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
          "Cannot run tests if server is not running");
      ErrorDialog.openError(Display.getCurrent().getActiveShell(),
          "JS Test Driver", "JS Test Driver Error", status);
      return;
    } else if (!Server.getInstance().isReadyToRunTests()) {
          IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
          "Cannot run tests if no browsers captured");
      ErrorDialog.openError(Display.getCurrent().getActiveShell(),
          "JS Test Driver", "JS Test Driver Error", status);
      return;
    }
    if (view.getLastLaunchConfiguration() != null) {
      Display.getDefault().asyncExec(new Runnable() {
        public void run() {
          view.setupForNextTestRun(view.getLastLaunchConfiguration());
          try {
            view.getLastLaunchConfiguration().launch(ILaunchManager.RUN_MODE, null);
          } catch (CoreException e) {
            logger.logException(e);
          }
        }
      });
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }

}
