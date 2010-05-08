/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.jstestdriver.config.Configuration;
import com.google.jstestdriver.config.DefaultConfiguration;
import com.google.jstestdriver.config.YamlParser;
import com.google.jstestdriver.guice.DebugModule;
import com.google.jstestdriver.guice.TestResultPrintingModule;
import com.google.jstestdriver.hooks.FileParsePostProcessor;
import com.google.jstestdriver.html.HtmlDocModule;
import com.google.jstestdriver.runner.RunnerMode;

import org.kohsuke.args4j.CmdLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.LogManager;

public class JsTestDriver {
  private static final Logger logger =
      LoggerFactory.getLogger(JsTestDriver.class);

  public static void main(String[] args) {
    try {
      // pre-parse parsing... These are the flags
      // that must be dealt with before we parse the flags.

      List<CmdLineFlag> cmdLineFlags = new CmdLineFlagsFactory().create(args);
      File basePath = getBasePath(cmdLineFlags);
      File config = getConfigPath(cmdLineFlags);
      List<Plugin> cmdLinePlugins = getPlugins(cmdLineFlags);

      YamlParser parser = new YamlParser();
      Flags flags = new FlagsParser().parseArgument(args);

      PathResolver pathResolver =
          new PathResolver(basePath,
              new DefaultPathRewriter(),
              Collections.<FileParsePostProcessor>emptySet());
      List<Module> modules = Lists.newLinkedList();

      LogManager.getLogManager().readConfiguration(
          flags.getRunnerMode().getLogConfig());

      Configuration configuration = new DefaultConfiguration();
      if (flags.hasWork()) {
        if (!config.exists()) {
          throw new RuntimeException("Config file doesn't exist: " + flags.getConfig());
        }
        configuration = parser.parse(new java.io.FileReader(config));
        modules.addAll(new PluginLoader().load(pathResolver.resolve(configuration.getPlugins())));
        modules.add(new HtmlDocModule()); // by default the html plugin is installed.
      }

      modules.add(new TestResultPrintingModule(System.out, flags.getTestOutput()));
      modules.add(new DebugModule(flags.getRunnerMode() == RunnerMode.DEBUG));

      Injector injector = Guice.createInjector(
          new JsTestDriverModule(flags,
              configuration.resolvePaths(pathResolver).getFilesList(),
              modules,
              configuration.createServerAddress(flags.getServer(),
                                                flags.getPort())));

      injector.getInstance(ActionRunner.class).runActions();
    } catch (CmdLineException e){
      System.out.println(e.getMessage());
      System.exit(1);
    } catch (FailureException e) {
      System.out.println("Tests failed.");
      System.exit(1);
    } catch (Exception e) {
      logger.debug("Error {}", e);
      e.printStackTrace();
      System.out.println("Unexpected Runner Condition: " + e.getMessage() + "\n Use --runnerMode DEBUG for more information.");
      System.exit(1);
    }
  }

  private static List<Plugin> getPlugins(List<CmdLineFlag> cmdLineFlags) {
    for (CmdLineFlag cmdLineFlag : cmdLineFlags) {
      if ("--plugins".equals(cmdLineFlag.flag)) {
        List<Plugin> plugins = Lists.newLinkedList();
        for (String pluginPath : cmdLineFlag.valuesList()) {
          plugins.add(
              new Plugin(null, pluginPath, null, Collections.<String>emptyList()));
        }
        return plugins;
      }
    }
    return Collections.<Plugin>emptyList();
  }

  private static File getConfigPath(List<CmdLineFlag> cmdLineFlags) {
    for (CmdLineFlag cmdLineFlag : cmdLineFlags) {
      if ("--config".equals(cmdLineFlag.flag)) {
        return new File(cmdLineFlag.safeValue());
      }
    }
    return new File("jsTestDriver.conf");
  }

  private static File getBasePath(List<CmdLineFlag> cmdLineFlags) {
    for (CmdLineFlag cmdLineFlag : cmdLineFlags) {
      if ("--basePath".equals(cmdLineFlag.flag)) {
        return new File(cmdLineFlag.safeValue());
      }
    }
    return getConfigPath(cmdLineFlags).getParentFile();
  }

  /**
   * An extremely simple flag object. It only support the name and the value
   * as a simple string, or as a boolean.
   * @author corysmith@google.com (Cory Smith)
   *
   */
  public static class CmdLineFlag {
    public final String flag;
    public final String value;

    public CmdLineFlag(String flag, String value) {
      this.flag = flag;
      this.value = value;
    }

    public String safeValue() {
      return value == null ? "" : value;
    }

    public List<String> valuesList() {
      if (value == null) {
        return Collections.<String>emptyList();
      }
      List<String> values =  Lists.<String>newLinkedList();
      for (String string : value.split(",")) {
        values.add(string);
      }
      return values;
    }
  }

  /**
   * Poorman's flag parser. This will take a String[] and translate it into a
   * List<CmdLineFlags>, a very lightweight representation of flag objects.
   * @author corysmith@google.com (Cory Smith)
   */
  public static class CmdLineFlagsFactory {
    public List<CmdLineFlag> create(String[] args) {
      CmdLineFlagIterator iterator = new CmdLineFlagIterator(args);
      List<CmdLineFlag> flags = Lists.newLinkedList();
      while (iterator.hasNext()) {
        flags.add(iterator.next());
      }
      return flags;
    }

    /**
     * Iterates over an array of String[] returning CmdLineFlags object. Poor
     * mans flag parser, really. This is used to extract flags as a precursor 
     * until we can use the heavy weight flag parsing machinery.
     */
    private static class CmdLineFlagIterator implements Iterator<CmdLineFlag> {
      private final String[] args;
      private int pos = 0;

      public CmdLineFlagIterator(String[] args) {
        this.args = args;
      }

      public boolean hasNext() {
        while (pos < args.length) {
          if (args[pos].startsWith("--")) {
            return true;
          }
          pos++;
        }
        return false;
      }

      public CmdLineFlag next() {
        int current = pos++;
        int next = pos;
        if (next >= args.length || args[next].startsWith("--")) {
          if (args[current].contains("=")) {
            String[] flagValue = args[current].split("=");
            return new CmdLineFlag(flagValue[0], flagValue[1]);
          }
          return new CmdLineFlag(args[current], null);
        }
        pos++; // consume the next because it's the value.
        return new CmdLineFlag(args[current], args[next]);
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }
    }
  }
}
