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
