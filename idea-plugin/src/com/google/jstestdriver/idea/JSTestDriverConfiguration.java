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
package com.google.jstestdriver.idea;

import com.google.jstestdriver.idea.ui.ConfigurationForm;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationInfoProvider;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.JavaProgramRunner;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.JDOMExternalizable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * One configured instance of the Run Configuration. The user can create several different configs
 * and save them all.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JSTestDriverConfiguration extends RunConfigurationBase {

  private final JSTestDriverConfigurationFactory jsTestDriverConfigurationFactory;
  private final String pluginName;

  public String settingsFile;
  private String serverPort;

  public JSTestDriverConfiguration(Project project,
                                   JSTestDriverConfigurationFactory jsTestDriverConfigurationFactory,
                                   String pluginName) {
    super(project, jsTestDriverConfigurationFactory, pluginName);
    this.jsTestDriverConfigurationFactory = jsTestDriverConfigurationFactory;
    this.pluginName = pluginName;
  }

  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new ConfigurationForm();
  }

  public JDOMExternalizable createRunnerSettings(ConfigurationInfoProvider provider) {
    return null;
  }

  public SettingsEditor<JDOMExternalizable> getRunnerSettingsEditor(JavaProgramRunner runner) {
    return null;
  }

  public RunProfileState getState(DataContext context, RunnerInfo runnerInfo,
                                  RunnerSettings runnerSettings,
                                  ConfigurationPerRunnerSettings configurationSettings)
      throws ExecutionException {
    ProjectJdk projectJdk = ProjectRootManager.getInstance(getProject()).getProjectJdk();
    return new TestRunnerState(runnerInfo, runnerSettings, configurationSettings, this,
        getProject());
  }

  public void checkConfiguration() throws RuntimeConfigurationException {
    if (settingsFile == null) {
      throw new RuntimeConfigurationException("Settings file is required");
    }
    if (!new File(settingsFile).exists()) {
      throw new RuntimeConfigurationException("Settings file does not exist: " + settingsFile);
    }
  }

  public Module[] getModules() {
    List<Module> modules = new ArrayList<Module>();
    Module[] allModules = ModuleManager.getInstance(getProject()).getModules();
    modules.addAll(Arrays.asList(allModules));
    return modules.toArray(new Module[modules.size()]);
  }

  public String getSettingsFile() {
    return settingsFile;
  }

  public void setSettingsFile(String settingsFile) {
    this.settingsFile = settingsFile;
  }

  public String getServerPort() {
    return serverPort;
  }

  public void setServerPort(String serverPort) {
    this.serverPort = serverPort;
  }
}