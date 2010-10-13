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
package com.google.jstestdriver.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.Flags;
import com.google.jstestdriver.PathResolver;
import com.google.jstestdriver.Plugin;
import com.google.jstestdriver.model.HandlerPathPrefix;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a parsed configuration.
 *
 * @author corysmith@google.com (Cory Smith)
 */
public class ParsedConfiguration implements Configuration {
  private final Set<FileInfo> filesList;
  private final String server;
  private final List<Plugin> plugins;
  private final Set<FileInfo> excludedFiles;
  private final long testTimeout;
  private final List<FileInfo> tests;

  public ParsedConfiguration(Set<FileInfo> filesList, Set<FileInfo> excludedFiles,
      List<Plugin> plugins, String server, long testTimeout, List<FileInfo> tests) {
    this.filesList = filesList;
    this.excludedFiles = excludedFiles;
    this.plugins = plugins;
    this.server = server;
    this.testTimeout = testTimeout;
    this.tests = tests;
  }

  public Set<FileInfo> getFilesList() {
    Set<FileInfo> finalList = new LinkedHashSet<FileInfo>(filesList);
    finalList.removeAll(excludedFiles);
    return finalList;
  }

  public List<Plugin> getPlugins() {
    return plugins;
  }

  public String getServer(String flagValue, int port, HandlerPathPrefix handlerPrefix) {
    if (flagValue != null && flagValue.length() != 0) {
      return flagValue;
    }
    if (server.length() > 0) {
      return handlerPrefix.suffixServer(server);
    }
    if (port == -1) {
      throw new RuntimeException("Oh Snap! No server defined!");
    }

    return handlerPrefix.suffixServer(String.format("http://%s:%d", "127.0.0.1", port));
  }

  public Configuration resolvePaths(PathResolver resolver, Flags flags) {
    Set<FileInfo> resolvedFiles = resolver.resolve(filesList);
    Set<FileInfo> testFiles = resolver.resolve(Sets.newLinkedHashSet(tests));
    Set<FileInfo> resolvedExcluded = resolver.resolve(excludedFiles);
    resolvedFiles.removeAll(resolvedExcluded);
    testFiles.removeAll(resolvedExcluded);
    return new ResolvedConfiguration(
        resolvedFiles,
        resolver.resolve(plugins),
        getServer(
            flags.getServer(),
            flags.getPort(),
            flags.getServerHandlerPrefix()),
        testTimeout,
        Lists.newArrayList(testFiles));
  }

  public long getTestSuiteTimeout() {
    return testTimeout;
  }

  public List<FileInfo> getTests() {
    return tests;
  }
}
