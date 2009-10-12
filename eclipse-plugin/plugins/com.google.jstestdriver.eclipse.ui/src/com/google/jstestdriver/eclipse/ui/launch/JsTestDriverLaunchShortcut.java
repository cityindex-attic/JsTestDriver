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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.Activator;
import com.google.jstestdriver.eclipse.ui.LaunchConfigCreator;
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
  public static final String LAUNCH_CONFIG_CREATORS =
      "com.google.jstestdrvier.eclipse.ui.launchConfigCreator";

  public void launch(ISelection selection, String mode) {
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
    if (selection instanceof IStructuredSelection) {
      try {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        List<String> testCases = new ArrayList<String>();
        String projectName = "";
        List<IFile> files = new ArrayList<IFile>();
        for (Object object : structuredSelection.toArray()) {
          if (object instanceof IFile) {
            IFile file = (IFile) object;
            files.add(file);
            projectName = file.getProject().getName();
            testCases.addAll(finder.getTestCases(file.getLocation().toFile()));
          }
        }
        ILaunchConfiguration launchConfiguration = null;
        launchConfiguration = getLaunchConfigFromExtensions(projectName, files);
        if (launchConfiguration == null) {
          launchConfiguration = getJstdLaunchConfigurations(projectName);
        }
        if (launchConfiguration == null) {
          IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
              "No Launch Configurations found for selection");
          ErrorDialog.openError(Display.getCurrent().getActiveShell(),
              "JS Test Driver", "JS Test Driver Error", status);
          return;
        }
        runTests(launchConfiguration, testCases);
      } catch (CoreException e) {
        logger.logException(e);
      } catch (IOException e) {
        logger.logException(e);
      }
    }
  }

  public void launch(IEditorPart editor, String mode) {
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
    List<String> testCases = new ArrayList<String>();
    try {
      String projectName = "";
      List<IFile> files = new ArrayList<IFile>();
      if (editor instanceof AbstractTextEditor) {
        AbstractTextEditor textEditor = (AbstractTextEditor) editor;
        //org.eclipse.wst.jsdt.internal.ui.javaeditor.CompilationUnitEditor
        // If we can find some particular tests selected in the file
        if (textEditor.getSelectionProvider().getSelection() instanceof ITextSelection) {
          int startLine = updateTestCasesFromSelection(testCases, textEditor);
          if (testCases.isEmpty()) {
            updateTestCasesFromCurrentLine(testCases, textEditor, startLine);
          }
        }
      }
      
      // Else lets add the entire file
      if (editor.getEditorInput() instanceof IFileEditorInput) {
        IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
        files.add(file);
        projectName = file.getProject().getName();
        if (testCases.isEmpty()) {
          updateTestCasesFromFile(editor, testCases);
        }
      }
      ILaunchConfiguration launchConfiguration = null;
      launchConfiguration = getLaunchConfigFromExtensions(projectName, files);
      if (launchConfiguration == null) {
        launchConfiguration = getJstdLaunchConfigurations(projectName);
      }
      if (launchConfiguration == null) {
        IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
            "No Launch Configurations found for current file");
        ErrorDialog.openError(Display.getCurrent().getActiveShell(),
            "JS Test Driver", "JS Test Driver Error", status);
        return;
      }
      runTests(launchConfiguration, testCases);
    } catch (CoreException e) {
      logger.logException(e);
    } catch (IOException e) {
      logger.logException(e);
    }
  }

  private ILaunchConfiguration getLaunchConfigFromExtensions(
      String projectName, List<IFile> files) throws CoreException {
    IConfigurationElement[] configs =
        Platform.getExtensionRegistry().getConfigurationElementsFor(LAUNCH_CONFIG_CREATORS);
    for (IConfigurationElement config : configs) {
      final Object o = config.createExecutableExtension("class");
      if (o instanceof LaunchConfigCreator) {
        LaunchConfigCreator creator = (LaunchConfigCreator) o;
        ILaunchConfiguration launchConfiguration =
            creator.getLaunchConfiguration(projectName, files);
        if (launchConfiguration != null) {
          return launchConfiguration;
        }
      }
    }
    return null;
  }

  private void updateTestCasesFromFile(IEditorPart editor,
      List<String> testCases) throws IOException {
    IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
    File jsFile = editorInput.getFile().getLocation().toFile();
    testCases.addAll(finder.getTestCases(jsFile));
  }

  private int updateTestCasesFromSelection(List<String> testCases,
      AbstractTextEditor textEditor) {
    ITextSelection selection = (ITextSelection) textEditor
        .getSelectionProvider().getSelection();
    int startLine = selection.getStartLine();
    String selectionString = selection.getText();
    if (selectionString != null && !"".equals(selectionString.trim())) {
      testCases.addAll(finder.getTestCases(selectionString));
    }
    return startLine;
  }

  private void updateTestCasesFromCurrentLine(List<String> testCases,
      AbstractTextEditor textEditor, int startLine) {
    IDocument document = textEditor.getDocumentProvider().getDocument(
        textEditor.getEditorInput());
    try {
      IRegion region = document.getLineInformation(startLine);
      String sourceCode = document.get(region.getOffset(), region.getLength());
      testCases.addAll(finder.getTestCases(sourceCode));
    } catch (BadLocationException e) {
      logger.logException(e);
    }
  }

  private void runTests(ILaunchConfiguration launchConfiguration,
      final List<String> testCases) throws CoreException {
    final ILaunchConfigurationWorkingCopy workingCopy = launchConfiguration.copy(
        "new run").getWorkingCopy();
    workingCopy.setAttribute(LaunchConfigurationConstants.TESTS_TO_RUN,
        testCases);
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
          actionRunnerFactory.getDryActionRunner(configuration, testCases).runActions();
          actionRunnerFactory.getSpecificTestsActionRunner(workingCopy, testCases).runActions();
        } catch (PartInitException e) {
          logger.logException(e);
        }
      }
    });
  }

  private ILaunchConfiguration getJstdLaunchConfigurations(String projectName)
      throws CoreException {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(
        "com.google.jstestdriver.eclipse.ui.jstdTestDriverLaunchConfiguration");
    ILaunchConfiguration[] launchConfigurations = launchManager
        .getLaunchConfigurations(type);
    for (ILaunchConfiguration configuration : launchConfigurations) {
      if (configuration.getAttribute(LaunchConfigurationConstants.PROJECT_NAME, "").equals(projectName)) {
        return configuration;
      }
    }
    return null;
  }

}
