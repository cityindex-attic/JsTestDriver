// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.config;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.name.Named;
import com.google.jstestdriver.Flags;
import com.google.jstestdriver.FlagsParser;
import com.google.jstestdriver.JsTestDriverModule;
import com.google.jstestdriver.PathResolver;
import com.google.jstestdriver.PluginLoader;
import com.google.jstestdriver.guice.DebugModule;
import com.google.jstestdriver.hooks.PluginInitializer;
import com.google.jstestdriver.html.HtmlDocModule;
import com.google.jstestdriver.runner.RunnerMode;

import org.kohsuke.args4j.CmdLineException;

import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.Set;

/**
 * Handles the creation the list of modules necessary to create an ActionRunner.
 * @author corbinrsmith@gmail.com
 *
 */
public class Initializer {
  private final PluginLoader pluginLoader;
  private final PathResolver pathResolver;
  private final FlagsParser flagsParser;
  private final Set<PluginInitializer> initializers;
  private final PrintStream outputStream;
  private final File basePath;

  @Inject
  public Initializer(PluginLoader pluginLoader,
                     PathResolver pathResolver,
                     FlagsParser flagsParser,
                     Set<PluginInitializer> initializers,
                     @Named("outputStream") PrintStream outputStream,
                     @Named("basePath") File basePath) {
    this.pluginLoader = pluginLoader;
    this.pathResolver = pathResolver;
    this.flagsParser = flagsParser;
    this.initializers = initializers;
    this.outputStream = outputStream;
    this.basePath = basePath;
    
  }

  public List<Module> initialize(List<Module> pluginModules,
      Configuration configuration,
      RunnerMode runnerMode,
      String[] args) throws CmdLineException {

    // TODO(corysmith): Figure out how to allow custom plugin flags
    // Might delegate to the flag parser to remove them, before creating Flags
    // Then, flags can have a method to retrieve them.
    Flags flags = flagsParser.parseArgument(args);
    final List<Module> modules = Lists.newLinkedList();
    Configuration resolvedConfiguration = configuration.resolvePaths(pathResolver);

    modules.addAll(pluginLoader.load(resolvedConfiguration.getPlugins()));

    for (PluginInitializer initializer : initializers) {
      final Module module = initializer.initializeModule(flags, resolvedConfiguration);
      modules.add(module);
    }

    modules.add(new HtmlDocModule()); // by default the html plugin is installed.
    modules.add(new DebugModule(runnerMode.isDebug()));
    modules.add(
        new JsTestDriverModule(flags,
            resolvedConfiguration.getFilesList(),
            resolvedConfiguration.createServerAddress(flags.getServer(),
                flags.getPort()),
            outputStream,
            basePath, configuration.getTestSuiteTimeout()));
    return modules;
  }
}