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
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.idea.ui.ToolPanel;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunnableState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestRunnerState implements RunnableState {

  private final JSTestDriverConfiguration jsTestDriverConfiguration;
  private final Project project;

  public TestRunnerState(
      RunnerInfo runnerInfo, RunnerSettings runnerSettings,
      ConfigurationPerRunnerSettings configurationSettings,
      JSTestDriverConfiguration jsTestDriverConfiguration, Project project) {

    this.jsTestDriverConfiguration = jsTestDriverConfiguration;
    this.project = project;
  }

  @Nullable
  public ExecutionResult execute() throws ExecutionException {
    ToolWindow window =
        ToolWindowManager.getInstance(project).getToolWindow(JSTestDriverToolWindow.TOOL_WINDOW_ID);
    window.show(new Runnable() {
      public void run() {
      }
    });
    ActionFactory actionFactory =
        Guice.createInjector(new ActionFactoryModule()).getInstance(ActionFactory.class);
    File path = new File("/home/alexeagle/IdeaProjects/untitled4");
    FileReader configReader;
    try {
      configReader = new FileReader(jsTestDriverConfiguration.getSettingsFile());
    } catch (FileNotFoundException e) {
      throw new ExecutionException("Failed to read settings file " +
                                   jsTestDriverConfiguration.getSettingsFile(), e);
    }
    ConfigurationParser configurationParser = new ConfigurationParser(path, configReader);
    String serverURL = "http://localhost:" + jsTestDriverConfiguration.getServerPort();
    ToolPanel toolPanel = (ToolPanel) window.getContentManager().getContent(0).getComponent();
    toolPanel.clearTestResults();
    final ResponseStream responseStream = toolPanel.getResponseStream();

    ResponseStreamFactory responseStreamFactory = new ResponseStreamFactory() {
      public ResponseStream getRunTestsActionResponseStream() {
        return responseStream;
      }

      public ResponseStream getDryRunActionResponseStream() {
        return null;
      }

      public ResponseStream getEvalActionResponseStream() {
        return null;
      }

      public ResponseStream getResetActionResponseStream() {
        return null;
      }
    };
    IDEPluginActionBuilder pluginActionBuilder =
        new IDEPluginActionBuilder(configurationParser, serverURL, actionFactory, responseStreamFactory);

    pluginActionBuilder.addAllTests().build().runActions();
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
