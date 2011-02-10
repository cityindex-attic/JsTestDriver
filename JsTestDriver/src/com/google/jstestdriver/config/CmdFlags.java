package com.google.jstestdriver.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.jstestdriver.Plugin;
import com.google.jstestdriver.runner.RunnerMode;

/**
 * A collection of CmdLinFlags with convenience methods for the preparsable
 * flags: config, plugins, and basePath. It is used to allow the initialization
 * system to have plugins that draw from the flags and the configuration.
 *
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class CmdFlags {
  private static final Map<String, CmdLineFlagMetaData> PREPARSE_FLAGS = 
    ImmutableMap.<String, CmdLineFlagMetaData>builder()
      .put(
          "--plugins",
          new CmdLineFlagMetaData("--plugins", "VAL[,VAL]",
              "Comma separated list of paths to plugin jars."))
      .put("--config", new CmdLineFlagMetaData("--config", "VAL", "Path to configuration file."))
      .put(
          "--basePath",
          new CmdLineFlagMetaData("--basePath", "VAL", "Override the base path in the "
              + "configuration file. Defaults to the parent directory of the configuration file."))
      .put(
          "--runnerMode",
          new CmdLineFlagMetaData("--runnerMode", "VAL", "The configuration of the "
              + "logging and frequency that the runner reports actions: DEBUG, "
              + "DEBUG_NO_TRACE, DEBUG_OBSERVE, PROFILE, QUIET (default), INFO")).build();
  private final List<CmdLineFlag> flags;

  public CmdFlags(List<CmdLineFlag> flags) {
    this.flags = flags;
  }

  public List<Plugin> getPlugins() throws IOException {
    for (CmdLineFlag cmdLineFlag : flags) {
      if ("--plugins".equals(cmdLineFlag.flag)) {
        List<Plugin> plugins = Lists.newLinkedList();
        for (String pluginPath : cmdLineFlag.valuesList()) {
          plugins.add(new Plugin(null, new File(getBasePath(), pluginPath).getAbsolutePath(), null,
              Collections.<String> emptyList()));
        }
        return plugins;
      }
    }
    return Collections.<Plugin> emptyList();
  }

  private String getConfigPathNoDefault() {
    for (CmdLineFlag cmdLineFlag : flags) {
      if ("--config".equals(cmdLineFlag.flag)) {
        return cmdLineFlag.safeValue();
      }
    }
    return null;
  }

  private String getBasePathNoDefault() throws IOException {
    for (CmdLineFlag cmdLineFlag : flags) {
      if ("--basePath".equals(cmdLineFlag.flag)) {
        return new File(cmdLineFlag.safeValue()).getCanonicalPath();
      }
    }
    return null;
  }

  public File getConfigPath() throws IOException {
    String configPath = getConfigPathNoDefault();

    if (configPath != null) {
      return new File(configPath).getAbsoluteFile();
    }
    return new File("jsTestDriver.conf").getAbsoluteFile();
  }

  public File getBasePath() throws IOException {
    final String basePath = getBasePathNoDefault();
    if (basePath != null) {
      return new File(basePath);
    }
    final File configPath = getConfigPath();
    if (configPath == null) {
      return null;
    }
    return configPath.getParentFile();
  }

  public RunnerMode getRunnerMode() {
    for (CmdLineFlag cmdLineFlag : flags) {
      if ("--runnerMode".equals(cmdLineFlag.flag)) {
        return RunnerMode.valueOf(cmdLineFlag.safeValue());
      }
    }
    return RunnerMode.QUIET;
  }

  public String[] getUnusedFlagsAsArgs() {
    final ArrayList<String> args = Lists.newArrayList();
    for (CmdLineFlag flag : flags) {
      if (!PREPARSE_FLAGS.containsKey(flag.flag)) {
        System.out.println(flag.flag);
        System.out.println(PREPARSE_FLAGS);
        flag.addToArgs(args);
      }
    }
    return args.toArray(new String[args.size()]);
  }
  
  public void printUsage(ByteArrayOutputStream stream) {
    for (CmdLineFlagMetaData meta : PREPARSE_FLAGS.values()) {
      try {
        meta.printUsage(stream);
        stream.write('\n');
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}
