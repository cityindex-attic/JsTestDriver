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

import java.util.List;
import java.util.Set;

import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.PathResolver;
import com.google.jstestdriver.Plugin;



/**
 * The interface for configuration files.
 * @author corbinsmith@gmail.com (Cory Smith)
 */
public interface Configuration {

  public Set<FileInfo> getFilesList();

  public String createServerAddress(String flagValue, int port);

  /** A list of plugin from the configuration file. */
  public List<Plugin> getPlugins();
  
  /** The total length of time a test should take to run.*/
  public long getTestSuiteTimeout();
  
  /** Resolves all wildcard paths and places them in a new configuration. */
  public Configuration resolvePaths(PathResolver resolver);

  /** Returns a list of files containing test. */
  public List<FileInfo> getTests();
}
