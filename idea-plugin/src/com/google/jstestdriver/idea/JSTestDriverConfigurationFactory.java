// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

/**
 * Simple factory that produces Run Configurations for a particular project.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JSTestDriverConfigurationFactory extends ConfigurationFactory {

  protected JSTestDriverConfigurationFactory(ConfigurationType type) {
    super(type);
  }

  @Override
  public RunConfiguration createTemplateConfiguration(Project project) {
    return new JSTestDriverConfiguration(project, this, "JSTestDriver");
  }
}
