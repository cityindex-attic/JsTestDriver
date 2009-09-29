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

import com.google.jstestdriver.JsTestDriverServer;
import com.google.jstestdriver.idea.ui.ToolPanel;
import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationModule;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Encapsulates the execution state of the test runner.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestRunnerState extends JavaCommandLineState {

  private final JSTestDriverConfiguration jsTestDriverConfiguration;
  private final Project project;
  private final RunConfigurationModule configurationModule;

  public TestRunnerState(JSTestDriverConfiguration jsTestDriverConfiguration, Project project,
                         ExecutionEnvironment env, RunConfigurationModule configurationModule) {
    super(env);
    this.jsTestDriverConfiguration = jsTestDriverConfiguration;
    this.project = project;
    this.configurationModule = configurationModule;
  }

  protected JavaParameters createJavaParameters() throws ExecutionException {
    JavaParameters javaParameters = new JavaParameters();
    Module module = configurationModule.getModule();
    Sdk jdk = module == null ?
        ProjectRootManager.getInstance(project).getProjectJdk() : 
        ModuleRootManager.getInstance(module).getSdk();
    javaParameters.setJdk(jdk);
    javaParameters.setMainClass(TestRunner.class.getName());
    javaParameters.getClassPath().add(PathUtil.getJarPathForClass(JsTestDriverServer.class));
    javaParameters.getClassPath().add(PathUtil.getJarPathForClass(TestRunner.class));
    String serverURL = (jsTestDriverConfiguration.getServerType() == ServerType.INTERNAL ?
        "http://localhost:" + ToolPanel.serverPort :
        jsTestDriverConfiguration.getServerAddress());
    javaParameters.getProgramParametersList().add(serverURL);
    File configFile = new File(jsTestDriverConfiguration.getSettingsFile());
    javaParameters.setWorkingDirectory(configFile.getParentFile());
    javaParameters.getProgramParametersList().add(jsTestDriverConfiguration.getSettingsFile());
    return javaParameters;
  }

  @Nullable
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner) throws ExecutionException {
    ProcessHandler processHandler = startProcess();
    ConsoleView consoleView = SMTestRunnerConnectionUtil.attachRunner(project, processHandler, this, jsTestDriverConfiguration, "JSTestDriver");

    return new DefaultExecutionResult(consoleView, processHandler, createActions(consoleView, processHandler));
  }
}
