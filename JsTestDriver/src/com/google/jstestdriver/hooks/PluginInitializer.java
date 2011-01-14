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
package com.google.jstestdriver.hooks;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.jstestdriver.Flags;
import com.google.jstestdriver.config.Configuration;

/**
 * A hook that allows the plugin to initialize with access to flags and
 * command line arguments.
 * 
 * Currently, only plugins passed in as flags can take advantage of this.
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public interface PluginInitializer {
  
  /**
   * A simple constant for plugins to return.
   */
  public static final Module NULL_MODULE = new Module() {
    public void configure(Binder binder) {
    }
  }; 
  
  /**
   * This is called during the initialization of JsTD.
   * @param flags The jstd flags.
   * @param config The configuration file read from disk.
   * @return A guice module that implements the plugin hooks.
   */
  public Module initializeModule(Flags flags, Configuration config);
}
