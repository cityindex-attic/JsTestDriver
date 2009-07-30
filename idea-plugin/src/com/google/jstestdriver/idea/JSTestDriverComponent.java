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

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Top-level of the plugin - this class is registered in the plugin XML.
 * Provides a new type of Run Configuration which launches the JSTestDriver server.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JSTestDriverComponent implements ConfigurationType {

  private ConfigurationFactory jsTestDriverConfigFactory = new JSTestDriverConfigurationFactory(this);

  public void initComponent() {
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return PluginResources.getPluginName() + ":ConfigurationType";
  }

  public String getDisplayName() {
    return PluginResources.getPluginName();
  }

  public String getConfigurationTypeDescription() {
    return PluginResources.getPluginName();
  }

  public Icon getIcon() {
    return PluginResources.getSmallIcon();
  }

  public ConfigurationFactory[] getConfigurationFactories() {
    return new ConfigurationFactory[]{ jsTestDriverConfigFactory };
  }
}
