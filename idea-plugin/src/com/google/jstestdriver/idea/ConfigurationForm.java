// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Configuration GUI
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ConfigurationForm extends SettingsEditor<JSTestDriverConfiguration> {

  private JPanel rootComponent;
  private JLabel settingsFileLabel;
  private JTextField settingsFile;
  private JLabel serverPortLabel;
  private JTextField serverPort;

  @Override
  protected void resetEditorFrom(JSTestDriverConfiguration config) {
    settingsFile.setText(config.getSettingsFile());
    serverPort.setText(config.getServerPort());
  }

  @Override
  protected void applyEditorTo(JSTestDriverConfiguration config) throws ConfigurationException {
    config.setSettingsFile(settingsFile.getText());
    config.setServerPort(serverPort.getText());
  }

  @Override
  @NotNull
  protected JComponent createEditor() {
    return rootComponent;
  }

  @Override
  protected void disposeEditor() {
  }

  public ConfigurationForm() {
    settingsFileLabel.setLabelFor(settingsFile);
    serverPortLabel.setLabelFor(serverPort);
  }
}
