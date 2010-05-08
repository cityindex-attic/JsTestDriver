package com.google.jstestdriver.config;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.jstestdriver.Plugin;
import com.google.jstestdriver.runner.RunnerMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A collection of CmdLinFlags with convenience methods for the preparsable
 * flags: config, plugins, and basePath. It is used to allow the initialization
 * system to have plugins that draw from the flags and the configuration.
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class CmdFlags {
  private static final Set<String> PREPARSE_FLAGS =
    ImmutableSet.of("--plugins", "--config", "--basePath", "--runnerMode");

  private final List<CmdLineFlag> flags;

  public CmdFlags(List<CmdLineFlag> flags) {
    this.flags = flags;
  }

  public List<Plugin> getPlugins() {
    for (CmdLineFlag cmdLineFlag : flags) {
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

  public File getConfigPath() {
    for (CmdLineFlag cmdLineFlag : flags) {
      if ("--config".equals(cmdLineFlag.flag)) {
        return new File(cmdLineFlag.safeValue());
      }
    }
    return new File("./jsTestDriver.conf");
  }

  public File getBasePath() throws IOException {
    for (CmdLineFlag cmdLineFlag : flags) {
      if ("--basePath".equals(cmdLineFlag.flag)) {
        return new File(cmdLineFlag.safeValue()).getCanonicalFile();
      }
    }
    final File parentFile = getConfigPath().getParentFile();
    if (parentFile != null) {
      return parentFile.getCanonicalFile();
    }
    return new File(".").getCanonicalFile();
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
      if (!PREPARSE_FLAGS.contains(flag.flag)) {
        flag.addToArgs(args);
      }
    }
    return args.toArray(new String[args.size()]);
  }
}