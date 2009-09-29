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
package com.google.jstestdriver.eclipse.ui.launch.save;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.ui.launch.JavascriptLaunchConfigurationHelper;
import com.google.jstestdriver.eclipse.ui.launch.LaunchConfigurationConstants;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JavascriptOnSaveTestRunner implements IResourceChangeListener {

  public class ProjectFindingDeltaVisitor implements IResourceDeltaVisitor {
    String projectName = "";

    public boolean visit(IResourceDelta delta) throws CoreException {
      IResource resource = delta.getResource();
      if (resource != null && resource.getProject() != null) {
        projectName = resource.getProject().getName();
      }
      return "".equals(projectName);
    }

    public String getProjectName() {
      return projectName;
    }

  }

  private final Logger logger = new Logger();
  private final JavascriptLaunchConfigurationHelper configurationHelper =
      new JavascriptLaunchConfigurationHelper();
  
  public void resourceChanged(IResourceChangeEvent event) {
    try {
      ProjectFindingDeltaVisitor visitor = new ProjectFindingDeltaVisitor();
      event.getDelta().accept(visitor);
      String projectName = visitor.getProjectName();
      ILaunchConfiguration launchConfiguration =
          configurationHelper.getLaunchConfiguration(projectName);
      if (launchConfiguration != null) {
        ILaunchConfigurationWorkingCopy workingCopy = launchConfiguration.getWorkingCopy();
        workingCopy.setAttribute(
            LaunchConfigurationConstants.RUN_ON_EVERY_SAVE, true);
        workingCopy.launch(ILaunchManager.RUN_MODE, null);
        Display.getDefault().asyncExec(new Runnable() {

          public void run() {
            PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage()
                .getActiveEditor()
                .setFocus();
          }
        });
      }
    } catch (CoreException e) {
      logger.logException(e);
    }
  }

}
