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
package com.google.jstestdriver.idea.ui;

import com.google.jstestdriver.idea.JSTestDriverConfiguration;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;

import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
