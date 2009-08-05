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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.views.JsTestDriverView;
import com.google.jstestdriver.eclipse.ui.views.TestResultsPanel;

/**
 * Launcher which knows how to run from a specific editor window.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JsTestDriverLaunchShortcut implements ILaunchShortcut {
  private final TestCaseNameFinder finder = new TestCaseNameFinder();
  private final ActionRunnerFactory actionRunnerFactory = new ActionRunnerFactory();
  private final Logger logger = new Logger();

  public void launch(ISelection selection, String mode) {
  }

  public void launch(IEditorPart editor, String mode) {
    // org.eclipse.ui.editors.text.TextEditor
    // org.eclipse.ui.part.FileEditorInput
    if (editor.getEditorInput() instanceof IFileEditorInput) {
      try {
        ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(
            "com.google.jstestdriver.eclipse.ui.jstdTestDriverLaunchConfiguration");
        ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations(type);
        if (launchConfigurations.length < 1) {
          // Error out.
          return;
        }
        IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
        File jsFile = editorInput.getFile().getLocation().toFile();
        final List<String> testCases = finder.getTestCases(jsFile);
        ILaunchConfigurationWorkingCopy workingCopy = 
            launchConfigurations[0].copy("new run").getWorkingCopy();
        workingCopy.setAttribute(LaunchConfigurationConstants.TESTS_TO_RUN, testCases);
        final ILaunchConfiguration configuration = workingCopy.doSave();
        Display.getDefault().asyncExec(new Runnable() {

          public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage();
            try {
              JsTestDriverView view = (JsTestDriverView) page
                  .showView("com.google.jstestdriver.eclipse.ui.views.JsTestDriverView");
              TestResultsPanel panel = view.getTestResultsPanel();
              panel.setupForNextTestRun(configuration);
              panel.addNumberOfTests(testCases.size());
            } catch (PartInitException e) {
              logger.logException(e);
            }
          }
        });
        // Might need a specific tests dry run
        actionRunnerFactory.getSpecificTestsActionRunner(workingCopy, testCases).runActions();
      } catch (IOException e) {
        logger.logException(e);
      } catch (CoreException e) {
        logger.logException(e);
      }
    }
  }

}
