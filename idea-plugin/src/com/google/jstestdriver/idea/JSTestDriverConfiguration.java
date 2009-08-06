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
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;

import org.jdom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * One configured instance of the Run Configuration. The user can create several different configs
 * and save them all.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JSTestDriverConfiguration extends ModuleBasedConfiguration {

  private final JSTestDriverConfigurationFactory jsTestDriverConfigurationFactory;

  public String settingsFile;
  private String serverPort;

  public JSTestDriverConfiguration(Project project,
                                   JSTestDriverConfigurationFactory jsTestDriverConfigurationFactory,
                                   String pluginName) {
    super(pluginName, new RunConfigurationModule(project, true), jsTestDriverConfigurationFactory);
    this.jsTestDriverConfigurationFactory = jsTestDriverConfigurationFactory;
  }

  public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
    return new ConfigurationForm();
  }

  public RunProfileState getState(DataContext context, RunnerInfo runnerInfo,
                                  RunnerSettings runnerSettings,
                                  ConfigurationPerRunnerSettings configurationSettings)
      throws ExecutionException {
    return new TestRunnerState(this, getProject());
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    if (settingsFile == null) {
      throw new RuntimeConfigurationException("Settings file is required");
    }
    if (!new File(settingsFile).exists()) {
      throw new RuntimeConfigurationException("Settings file does not exist: " + settingsFile);
    }
  }

  @Override
  public Collection<Module> getValidModules() {
    List<Module> modules = new ArrayList<Module>();
    Module[] allModules = ModuleManager.getInstance(getProject()).getModules();
    modules.addAll(Arrays.asList(allModules));
    return modules;
  }

  @Override
  protected ModuleBasedConfiguration createInstance() {
    return new JSTestDriverConfiguration(getConfigurationModule().getProject(),
        jsTestDriverConfigurationFactory, getName());
  }

  @Override
  public void readExternal(Element element) throws InvalidDataException {
    super.readExternal(element);
    readModule(element);
    settingsFile = JDOMExternalizer.readString(element, "settingsFile");
    serverPort = JDOMExternalizer.readString(element, "serverPort");
  }

  @Override
  public void writeExternal(Element element) throws WriteExternalException {
    super.writeExternal(element);
    writeModule(element);
    JDOMExternalizer.write(element, "settingsFile", settingsFile);
    JDOMExternalizer.write(element, "serverPort", serverPort);
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