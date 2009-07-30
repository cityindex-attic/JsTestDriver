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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestRunnerState implements RunnableState {
  private static final Logger logger = Logger.getInstance(TestRunnerState.class.getCanonicalName());
  private InputStream configFileInputStream = new StringBufferInputStream("server: http://localhost:9876\n"
                                                                          + "\n"
                                                                          + "load:\n"
                                                                          + "  - src/*.js\n"
                                                                          + "  - src-test/*.js");
  private String serverAddress = "http://localhost:9876";
  private Reader configReader = new InputStreamReader(configFileInputStream);

  public TestRunnerState(
      RunnerInfo runnerInfo, RunnerSettings runnerSettings,
      ConfigurationPerRunnerSettings configurationSettings,
      JSTestDriverConfiguration jsTestDriverConfiguration, Project project) {

  }

  @Nullable
  public ExecutionResult execute() throws ExecutionException {
    ActionFactory actionFactory =
        Guice.createInjector(new ActionFactoryModule()).getInstance(ActionFactory.class);
    File path = new File("/home/alexeagle/IdeaProjects/untitled4");
    ConfigurationParser configurationParser = new ConfigurationParser(path, configReader);
    IDEPluginActionBuilder pluginActionBuilder =
        new IDEPluginActionBuilder(configurationParser, serverAddress, actionFactory);
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
