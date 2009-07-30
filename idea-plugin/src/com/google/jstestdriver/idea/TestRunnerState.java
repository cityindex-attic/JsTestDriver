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

import com.google.inject.Guice;
import com.google.jstestdriver.ActionFactory;
import com.google.jstestdriver.ActionFactoryModule;
import com.google.jstestdriver.ConfigurationParser;
import com.google.jstestdriver.IDEPluginActionBuilder;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunnableState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestRunnerState implements RunnableState {

  private final JSTestDriverConfiguration jsTestDriverConfiguration;

  public TestRunnerState(
      RunnerInfo runnerInfo, RunnerSettings runnerSettings,
      ConfigurationPerRunnerSettings configurationSettings,
      JSTestDriverConfiguration jsTestDriverConfiguration, Project project) {

    this.jsTestDriverConfiguration = jsTestDriverConfiguration;
  }

  @Nullable
  public ExecutionResult execute() throws ExecutionException {
    ActionFactory actionFactory =
        Guice.createInjector(new ActionFactoryModule()).getInstance(ActionFactory.class);
    File path = new File("/home/alexeagle/IdeaProjects/untitled4");
    FileReader configReader = null;
    try {
      configReader = new FileReader(jsTestDriverConfiguration.getSettingsFile());
    } catch (FileNotFoundException e) {
      throw new ExecutionException("Failed to read settings file " +
                                   jsTestDriverConfiguration.getSettingsFile(), e);
    }
    ConfigurationParser configurationParser = new ConfigurationParser(path, configReader);
    String serverURL = "http://localhost:" + jsTestDriverConfiguration.getServerPort();
    IDEPluginActionBuilder pluginActionBuilder =
        new IDEPluginActionBuilder(configurationParser, serverURL, actionFactory);
    pluginActionBuilder.addAllTests().sendTestResultsTo(new ResponseStream() {
      public void stream(Response response) {
      }

      public void finish() {
      }
    }).build().runActions();
    return null;
  }

  public RunnerSettings getRunnerSettings() {
    return null;
  }

  public ConfigurationPerRunnerSettings getConfigurationSettings() {
    return null;
  }

  public Module[] getModulesToCompile() {
    return new Module[0];
  }
}
