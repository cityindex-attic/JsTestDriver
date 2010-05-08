// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.config;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.jstestdriver.PluginLoader;
import com.google.jstestdriver.hooks.FileParsePostProcessor;
import com.google.jstestdriver.hooks.PluginInitializer;

import java.io.File;
import java.io.PrintStream;

/**
 * The configuration module for initializing jstestdriver. It provides a sane
 * set of defaults, as well as documenting the requirements for the
 * initialize call.
 * 
 * @author corbinrsmith@gmail.com
 *
 */
final public class InitializeModule implements Module {
  private final PluginLoader pluginLoader;
  private final File basePath;

  public InitializeModule(PluginLoader pluginLoader, File basePath) {
    this.pluginLoader = pluginLoader;
    this.basePath = basePath;
  }

  public void configure(Binder binder) {
    Multibinder.newSetBinder(binder, FileParsePostProcessor.class);
    Multibinder.newSetBinder(binder, PluginInitializer.class);
    binder.bind(PrintStream.class).annotatedWith(
        Names.named("outputStream")).toInstance(System.out);
    binder.bind(PluginLoader.class).toInstance(pluginLoader);
    binder.bind(File.class)
        .annotatedWith(Names.named("basePath"))
        .toInstance(basePath);
  }
}