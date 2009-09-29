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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

import com.google.jstestdriver.eclipse.internal.core.Logger;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JavascriptLaunchConfigurationHelper {
  private static final String JSTD_LAUNCH_CONFIGURATION_TYPE =
      "com.google.jstestdriver.eclipse.ui.jstdTestDriverLaunchConfiguration";
  private Logger logger = new Logger();
  public ILaunchConfiguration getLaunchConfiguration(String projectName) {
    try {
      ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
      ILaunchConfigurationType type = launchManager
          .getLaunchConfigurationType(JSTD_LAUNCH_CONFIGURATION_TYPE);
      ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations(type);
      for (ILaunchConfiguration launchConfiguration : launchConfigurations) {
        String configProjectName =
            launchConfiguration.getAttribute(LaunchConfigurationConstants.PROJECT_NAME,
                "");
        if (configProjectName.equals(projectName)
            && isValidToRun(projectName, launchConfiguration)) {
          return launchConfiguration;
        }
      }
    } catch (CoreException e) {
      logger.logException(e);
    }
    return null;
  }
  
  public boolean isExistingLaunchConfigWithRunOnSaveOtherThanCurrent(
      String projectName, String launchConfigName) {
    try {
      ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
      ILaunchConfigurationType type = launchManager
          .getLaunchConfigurationType(JSTD_LAUNCH_CONFIGURATION_TYPE);
      ILaunchConfiguration[] launchConfigurations = launchManager.getLaunchConfigurations(type);
      for (ILaunchConfiguration launchConfiguration : launchConfigurations) {
        String configProjectName =
            launchConfiguration.getAttribute(LaunchConfigurationConstants.PROJECT_NAME,
                "");
        boolean runOnEveryBuild =
            launchConfiguration.getAttribute(LaunchConfigurationConstants.RUN_ON_EVERY_SAVE,
                false);
        String configName = launchConfiguration.getName();
        boolean isProjectNameEqual = configProjectName.equals(projectName);
        boolean isLaunchConfigNameEqual = launchConfigName.equals(configName); 
        if (isProjectNameEqual && runOnEveryBuild && !isLaunchConfigNameEqual) {
          return true;
        }
      }
    } catch (CoreException e) {
      logger.logException(e);
    }
    return false;
  }
  
  public boolean isExistingLaunchConfigurationWithRunOnSave(String projectName) {
    return getLaunchConfiguration(projectName) != null;
  }
  
  private boolean isValidToRun(String projectName, ILaunchConfiguration launchConfiguration)
      throws CoreException {
    boolean runOnEveryBuild =
        launchConfiguration.getAttribute(LaunchConfigurationConstants.RUN_ON_EVERY_SAVE,
            false);
    if (runOnEveryBuild
        && !isExistingLaunchConfigWithRunOnSaveOtherThanCurrent(projectName,
            launchConfiguration.getName())) {
      return true;
    }
    return false;
  }
}
