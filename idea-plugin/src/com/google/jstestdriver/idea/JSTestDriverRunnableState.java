// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationPerRunnerSettings;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;

import java.io.File;

/**
 * Holds the Java invocation of the server process.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JSTestDriverRunnableState extends JavaCommandLineState {

  private final RunnerInfo runnerInfo;
  private final RunnerSettings runnerSettings;
  private final ConfigurationPerRunnerSettings configurationSettings;
  private final JSTestDriverConfiguration jsTestDriverConfiguration;
  private final Project project;
  private final ProjectJdk projectJdk;

  public JSTestDriverRunnableState(RunnerInfo runnerInfo, RunnerSettings runnerSettings,
                                   ConfigurationPerRunnerSettings configurationSettings,
                                   JSTestDriverConfiguration jsTestDriverConfiguration,
                                   Project project, ProjectJdk projectJdk) {
    super(runnerSettings, configurationSettings);
    this.runnerInfo = runnerInfo;
    this.runnerSettings = runnerSettings;
    this.configurationSettings = configurationSettings;
    this.jsTestDriverConfiguration = jsTestDriverConfiguration;
    this.project = project;
    this.projectJdk = projectJdk;
  }

  @Override
  public RunnerSettings getRunnerSettings() {
    return runnerSettings;
  }

  @Override
  public ConfigurationPerRunnerSettings getConfigurationSettings() {
    return configurationSettings;
  }

  @Override
  public Module[] getModulesToCompile() {
    return jsTestDriverConfiguration.getModules();
  }

  @Override
  protected JavaParameters createJavaParameters() throws ExecutionException {
    JavaParameters params = new JavaParameters();
    params.setMainClass("com.google.jstestdriver.JsTestDriverServer");
    params.setJdk(projectJdk);
    params.getClassPath().add(PathManager.getPluginsPath() + File.separator + "jstestdriver-plugin"
                       + File.separator + "lib" + File.separator + "JsTestDriver.jar");
    params.getProgramParametersList().add("--config", jsTestDriverConfiguration.getSettingsFile());
    params.getProgramParametersList().add("--port", jsTestDriverConfiguration.getServerPort());
    return params;
  }

  @Override
  protected TextConsoleBuilder getConsoleBuilder() {
    return TextConsoleBuilderFactory.getInstance().createBuilder(project);
  }
}
