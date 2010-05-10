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

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.guice.FlagsModule;

import java.io.File;
import java.io.PrintStream;
import java.lang.annotation.Retention;
import java.util.List;
import java.util.Set;

/**
 * Guice module for configuring JsTestDriver.
 * 
 * @author corysmith
 * 
 */
public class JsTestDriverModule extends AbstractModule {

  private final Flags flags;
  private final Set<FileInfo> fileSet;
  private final String serverAddress;
  private final PrintStream outputStream;
  private final File basePath;

  public JsTestDriverModule(Flags flags,
                            Set<FileInfo> fileSet,
                            String serverAddress,
                            PrintStream outputStream,
                            File basePath) {
    this.flags = flags;
    this.fileSet = fileSet;
    this.serverAddress = serverAddress;
    this.outputStream = outputStream;
    this.basePath = basePath;
  }

  @BindingAnnotation @Retention(RUNTIME) public @interface BrowserCount{}

  @Override
  protected void configure() {
    bind(PrintStream.class)
         .annotatedWith(Names.named("outputStream")).toInstance(outputStream);
    bind(String.class)
         .annotatedWith(Names.named("server")).toInstance(serverAddress);

    bind(new TypeLiteral<Set<FileInfo>>() {}).annotatedWith(Names.named("originalFileSet"))
        .toInstance(fileSet);

    bind(new TypeLiteral<List<Action>>(){}).toProvider(ActionListProvider.class);

    bind(FailureAccumulator.class).in(Singleton.class);

    bind(File.class).annotatedWith(Names.named("basePath")).toInstance(basePath);

    install(new FlagsModule(flags));
    install(new ActionFactoryModule());

    for (BrowserRunner runner : flags.getBrowser()) {
      Multibinder.newSetBinder(binder(),
          BrowserRunner.class).addBinding().toInstance(runner);
    }

    bind(new TypeLiteral<Set<FileInfo>>() {}).annotatedWith(Names.named("fileSet")).
        toProvider(FileSetProvider.class).in(Singleton.class);
    bind(Integer.class).annotatedWith(BrowserCount.class).
        toProvider(BrowserCountProvider.class).in(Singleton.class);
  }

  /**
   * Provides the number of browsers. Needed by any code that is aware of the threading model for
   * running tests in multiple browsers.
   *
   * @author alexeagle@google.com (Alex Eagle)
   */
  public static class BrowserCountProvider implements Provider<Integer> {
    private final JsTestDriverClient client;

    @Inject
    public BrowserCountProvider(JsTestDriverClient client) {
      this.client = client;
    }

    public synchronized Integer get() {
      try {
        return client.listBrowsers().size();
      } catch (Exception e) {
        throw new RuntimeException("Cannot inject the browser count until the server has started." +
            " Try injecting a Provider of it instead.", e);
      }
    }
  }

}
