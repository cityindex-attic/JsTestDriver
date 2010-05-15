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
package com.google.jstestdriver.config;

import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.PathResolver;
import com.google.jstestdriver.Plugin;

import java.util.List;
import java.util.Set;

/**
 * A configuration were all the paths have been resolved.
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class ResolvedConfiguration implements Configuration {

  private final Set<FileInfo> filesList;
  private final List<Plugin> plugins;
  private final String server;
  private final long testSuiteTimeout;

  public ResolvedConfiguration(Set<FileInfo> filesList,
                               List<Plugin> plugins,
                               String server,
                               long testSuiteTimeout) {
    this.filesList = filesList;
    this.plugins = plugins;
    this.server = server;
    this.testSuiteTimeout = testSuiteTimeout;
  }

  public String createServerAddress(String flagValue, int port) {
    if (flagValue != null && flagValue.length() != 0) {
      return flagValue;
    }
    if (server.length() > 0) {
      return server;
    }
    if (port == -1) {
      throw new RuntimeException("Oh Snap! No server defined!");
    }
    return String.format("http://%s:%d", "127.0.0.1", port);
  }

  public Set<FileInfo> getFilesList() {
    return filesList;
  }

  public List<Plugin> getPlugins() {
    return plugins;
  }

  public Configuration resolvePaths(PathResolver resolver) {
    return this;
  }

  public long getTestSuiteTimeout() {
    return testSuiteTimeout > 0 ? testSuiteTimeout : DefaultConfiguration.DEFAULT_TEST_TIMEOUT;
  }
}
