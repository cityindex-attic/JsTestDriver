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
package com.google.jstestdriver;

import java.util.List;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.jstestdriver.guice.FlagsModule;

/**
 * Guice module for configuring JsTestDriver.
 * 
 * @author corysmith
 * 
 */
public class JsTestDriverModule extends AbstractModule {

  private final Flags flags;
  private final Set<FileInfo> fileSet;
  private final List<Module> plugins;
  private final String serverAddress;

  public JsTestDriverModule(Flags flags,
                            Set<FileInfo> fileSet,
                            List<Module> plugins,
                            String serverAddress) {
    this.flags = flags;
    this.fileSet = fileSet;
    this.plugins = plugins;
    this.serverAddress = serverAddress;
  }

  @Override
  protected void configure() {
    // TODO(corysmith): Change this to an actual class, so that we can JITI it.
    bind(new TypeLiteral<Set<FileInfo>>() {})
         .annotatedWith(Names.named("fileSet")).toInstance(fileSet);

    bind(String.class)
         .annotatedWith(Names.named("server")).toInstance(serverAddress);

    bind(new TypeLiteral<List<Action>>(){}).toProvider(ActionListProvider.class);

    bind(FailureAccumulator.class).in(Singleton.class);

    install(new FlagsModule(flags));
    install(new ActionFactoryModule());

    // install plugin modules.
    for (Module module : plugins) {
      install(module);
    }
  }
}
