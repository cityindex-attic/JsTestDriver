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

  private String settingsFile;
  private ConfigurationFactory jsTestDriverConfigFactory = new JSTestDriverConfigurationFactory(this);

  public void initComponent() {
  }

  public void disposeComponent() {
  }

  @NotNull
  public String getComponentName() {
    return PluginResources.getPluginName() + ":ConfigurationType";
  }

  public String getSettingsFile() {
    return settingsFile;
  }

  public void setSettingsFile(final String settingsFile) {
    this.settingsFile = settingsFile;
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
    return new ConfigurationFactory[]{jsTestDriverConfigFactory};
  }
}
