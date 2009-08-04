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
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.jstestdriver.ActionFactory;
import com.google.jstestdriver.ActionFactoryModule;
import com.google.jstestdriver.ConfigurationParser;
import com.google.jstestdriver.IDEPluginActionBuilder;
import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.internal.core.ProjectHelper;
import com.google.jstestdriver.eclipse.ui.views.JsTestDriverView;
import com.google.jstestdriver.eclipse.ui.views.TestResultsPanel;

/**
 * Handles a Js Test Driver launch.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JsTestDriverLaunchConfigurationDelegate implements
    ILaunchConfigurationDelegate2 {

  private final Logger logger = new Logger();

  public void launch(ILaunchConfiguration configuration, String mode,
      ILaunch launch, IProgressMonitor monitor) throws CoreException {
    if (!mode.equals(ILaunchManager.RUN_MODE)) {
      throw new IllegalStateException(
          "Can only launch JS Test Driver configuration from Run mode");
    }

    String projectName = configuration.getAttribute(
        LaunchConfigurationConstants.PROJECT_NAME, "");
    String confFileName = configuration.getAttribute(
        LaunchConfigurationConstants.CONF_FILENAME, "");

    ConfigurationParser configurationParser = getConfigurationParser(
        projectName, confFileName);
    Injector injector = Guice.createInjector(new ActionFactoryModule());

    Display.getDefault().asyncExec(new Runnable() {

      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage();
        try {
          JsTestDriverView view = (JsTestDriverView) page
              .showView("com.google.jstestdriver.eclipse.ui.views.JsTestDriverView");
          TestResultsPanel panel = view.getTestResultsPanel();
          panel.setupForNextTestRun();
        } catch (PartInitException e) {
          logger.logException(e);
        }
      }
    });

    IDEPluginActionBuilder dryRunBuilder = new IDEPluginActionBuilder(
        configurationParser, Server.SERVER_URL, injector
            .getInstance(ActionFactory.class),
        new EclipseResponseStreamFactory());
    dryRunBuilder.dryRun().build().runActions();
    
    configurationParser = getConfigurationParser(projectName, confFileName);
    IDEPluginActionBuilder allTestsBuilder = new IDEPluginActionBuilder(
        configurationParser, Server.SERVER_URL, injector
            .getInstance(ActionFactory.class),
        new EclipseResponseStreamFactory());
    allTestsBuilder.addAllTests().build().runActions();
  }

  private ConfigurationParser getConfigurationParser(String projectName,
      String confFileName) {
    IProject project = new ProjectHelper().getProject(projectName);
    IResource confFileResource = project.findMember(confFileName);
    File configFile = confFileResource.getLocation().toFile();
    File parentDir = configFile.getParentFile();
    ConfigurationParser configurationParser = null;
    try {
      configurationParser = new ConfigurationParser(parentDir, new FileReader(configFile));
    } catch (FileNotFoundException e) {
      logger.logException(e);
    }
    return configurationParser;
  }

  public boolean buildForLaunch(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) {
    return mode.equals(ILaunchManager.RUN_MODE) && !monitor.isCanceled();
  }

  public boolean finalLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) {
    return mode.equals(ILaunchManager.RUN_MODE) && !monitor.isCanceled();
  }

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) {
    if (mode.equals(ILaunchManager.RUN_MODE)) {
      return new Launch(configuration, mode, new JavascriptSourceLocator());
    } else {
      throw new IllegalStateException(
          "Can only launch JS Test Driver configuration from Run mode");
    }
  }

  public boolean preLaunchCheck(ILaunchConfiguration configuration,
      String mode, IProgressMonitor monitor) {
    return mode.equals(ILaunchManager.RUN_MODE) && !monitor.isCanceled();
  }

}
