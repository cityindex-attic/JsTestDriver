// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea;

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
    return new JSTestDriverRunnableState(runnerInfo, runnerSettings, configurationSettings, this,
        getProject(), ProjectRootManager.getInstance(getProject()).getProjectJdk());
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