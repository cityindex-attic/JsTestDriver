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

import com.intellij.openapi.util.IconLoader;

import java.net.URL;

import javax.swing.*;// Copyright 2009 Google Inc. All Rights Reserved.

/**
 * Access to all the text and image resources for the plugin.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class PluginResources {

  private PluginResources() {}

  public static String getPluginName() {
    return MessageBundle.message("plugin.name");
  }

  public static Icon getSmallIcon() {
    URL resource = PluginResources.class.getClassLoader().getResource("com/google/jstestdriver/idea/icons/JsTestDriver.png");
    return IconLoader.getIcon(new ImageIcon(resource, getPluginName()).getImage());
  }
}
