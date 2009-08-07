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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
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
import org.eclipse.ui.editors.text.TextEditor;

import com.google.jstestdriver.eclipse.core.SlaveBrowserRootData;
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
    if (selection instanceof IStructuredSelection) {
      try {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        ILaunchConfiguration[] launchConfigurations = getJstdLaunchConfigurations();
        if (launchConfigurations.length < 1) {
          // Error out.
          return;
        }
        List<String> testCases = new ArrayList<String>();
        for (Object object : structuredSelection.toArray()) {
          if (object instanceof IFile) {
            IFile file = (IFile) object;
            testCases.addAll(finder.getTestCases(file.getLocation().toFile()));
          }
        }
        runTests(launchConfigurations[0], testCases);
      } catch (CoreException e) {
        logger.logException(e);
      } catch (IOException e) {
        logger.logException(e);
      }
    }
  }

  private List<String> getTestCases(File file) throws IOException {
    return finder.getTestCases(file);
  }

  public void launch(IEditorPart editor, String mode) {
    List<String> testCases = new ArrayList<String>();
    try {
      ILaunchConfiguration[] launchConfigurations = getJstdLaunchConfigurations();
      if (launchConfigurations.length < 1) {
        // Error out.
        return;
      }
      if (editor instanceof TextEditor) {
        TextEditor textEditor = (TextEditor) editor;
        if (textEditor.getSelectionProvider().getSelection() instanceof ITextSelection) {
          int startLine = updateTestCasesFromSelection(testCases, textEditor);
          if (testCases.isEmpty()) {
            updateTestCasesFromCurrentLine(testCases, textEditor, startLine);
          }
        }
      }
      if (editor.getEditorInput() instanceof IFileEditorInput) {
        if (testCases.isEmpty()) {
          updateTestCasesFromFile(editor, testCases);
        }
      }
      runTests(launchConfigurations[0], testCases);
    } catch (CoreException e) {
      logger.logException(e);
    } catch (IOException e) {
      logger.logException(e);
    }
  }

  private void updateTestCasesFromFile(IEditorPart editor,
      List<String> testCases) throws IOException {
    IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
    File jsFile = editorInput.getFile().getLocation().toFile();
    testCases.addAll(getTestCases(jsFile));
  }

  private int updateTestCasesFromSelection(List<String> testCases,
      TextEditor textEditor) {
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
      TextEditor textEditor, int startLine) {
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
    ILaunchConfigurationWorkingCopy workingCopy = launchConfiguration.copy(
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
          SlaveBrowserRootData data = SlaveBrowserRootData.getInstance();
          panel.addNumberOfTests(testCases.size() * data.getNumberOfSlaves());
        } catch (PartInitException e) {
          logger.logException(e);
        }
      }
    });
    // Might need a specific tests dry run in case there are some tests which are not run 
    // in a specific browser
    actionRunnerFactory.getSpecificTestsActionRunner(workingCopy, testCases).runActions();
  }

  private ILaunchConfiguration[] getJstdLaunchConfigurations()
      throws CoreException {
    ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
    ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(
        "com.google.jstestdriver.eclipse.ui.jstdTestDriverLaunchConfiguration");
    ILaunchConfiguration[] launchConfigurations = launchManager
        .getLaunchConfigurations(type);
    return launchConfigurations;
  }

}
