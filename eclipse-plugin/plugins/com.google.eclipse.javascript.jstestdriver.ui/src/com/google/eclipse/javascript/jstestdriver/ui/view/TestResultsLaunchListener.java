// Copyright 2009 Google Inc. All Rights Reserved.

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
 * @author shyamseshadri@google.com (Shyam Seshadri)
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
