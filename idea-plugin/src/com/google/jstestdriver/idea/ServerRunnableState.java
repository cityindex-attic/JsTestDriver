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

import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.JsTestDriverServer;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.RunnableState;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.Collections;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ServerRunnableState implements RunnableState {

  private final JSTestDriverConfiguration jsTestDriverConfiguration;

  public ServerRunnableState(RunnerInfo runnerInfo, RunnerSettings runnerSettings,
                             ConfigurationPerRunnerSettings configurationSettings,
                             JSTestDriverConfiguration jsTestDriverConfiguration,
                             Project project) {

    this.jsTestDriverConfiguration = jsTestDriverConfiguration;
  }

  @Nullable
  public ExecutionResult execute() throws ExecutionException {
    int port = Integer.parseInt(jsTestDriverConfiguration.getServerPort());

    CapturedBrowsers browsers = new CapturedBrowsers();
    FilesCache cache = new FilesCache(Collections.<String, FileInfo>emptyMap());
    JsTestDriverServer server = new JsTestDriverServer(port, browsers, cache);
    return new ServerExecutionResult(server);
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

  private class ServerExecutionResult implements ExecutionResult {

    private final JsTestDriverServer server;

    public ServerExecutionResult(
        JsTestDriverServer server) {
      this.server = server;
    }

    public ExecutionConsole getExecutionConsole() {
      return null;
    }

    public AnAction[] getActions() {
      return new AnAction[0];
    }

    public ProcessHandler getProcessHandler() {
      return new ProcessHandler() {
        @Override
        protected void destroyProcessImpl() {
          server.stop();
        }

        @Override
        protected void detachProcessImpl() {
          server.stop();
        }

        @Override
        public boolean detachIsDefault() {
          return false;
        }

        @Override
        public OutputStream getProcessInput() {
          return null;
        }
      };
    }
  }
}
