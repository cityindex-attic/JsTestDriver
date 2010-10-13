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

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.jstestdriver.config.DefaultConfiguration;
import com.google.jstestdriver.guice.BrowserActionProvider;
import com.google.jstestdriver.guice.FlagsModule;
import com.google.jstestdriver.html.HtmlDocModule;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A builder for IDE's to use. Minimizes the surface area of the API which needs
 * to be maintained on the IDE plugin side. TODO(jeremiele) We should rename
 * this for other API uses. Refactor the crap out of this.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IDEPluginActionBuilder {

  private final ConfigurationParser configParser;
  private final String serverAddress;
  private final ResponseStreamFactory responseStreamFactory;

  private List<String> tests = new LinkedList<String>();
  private final LinkedList<Module> modules = new LinkedList<Module>();
  private boolean reset = false;
  private List<String> dryRunFor = new LinkedList<String>();
  private File basePath;

  public IDEPluginActionBuilder(ConfigurationParser configParser,
      String serverAddress, ResponseStreamFactory responseStreamFactory,
      File basePath) {
    this.configParser = configParser;
    this.serverAddress = serverAddress;
    this.responseStreamFactory = responseStreamFactory;
    this.basePath = basePath;
  }

  public IDEPluginActionBuilder addAllTests() {
    tests.add("all");
    return this;
  }

  public IDEPluginActionBuilder addTests(List<String> testCases) {
    tests.addAll(testCases);
    return this;
  }

  public IDEPluginActionBuilder dryRunFor(List<String> dryRunFor) {
    this.dryRunFor.addAll(dryRunFor);
    return this;
  }

  public IDEPluginActionBuilder resetBrowsers() {
    reset = true;
    return this;
  }

  public IDEPluginActionBuilder install(Module module) {
    modules.add(module);
    return this;
  }

  public ActionRunner build() {
    configParser.parse();
    modules.add(new HtmlDocModule());
    Injector injector = Guice.createInjector(new ActionFactoryModule(),
        new ConfigurationModule(modules, tests, reset, dryRunFor,
            serverAddress != null ? serverAddress : configParser.getServer(),
            basePath, configParser.getFilesList(), responseStreamFactory));

    return injector.getInstance(ActionRunner.class);
  }

  // TODO(corysmith): Combine this class with the JsTestDriverModule
  private static class ConfigurationModule extends AbstractModule {

    private final List<String> tests;
    private final boolean reset;
    private final List<String> dryRunFor;
    private final String serverAddress;
    private final File basePath;
    private final Set<FileInfo> fileSet;
    private final ResponseStreamFactory responseStreamFactory;
    private final LinkedList<Module> modules;

    public ConfigurationModule(LinkedList<Module> modules, List<String> tests,
        boolean reset, List<String> dryRunFor, String serverAddress,
        File basePath, Set<FileInfo> fileSet,
        ResponseStreamFactory responseStreamFactory) {
      this.modules = modules;
      this.tests = tests;
      this.reset = reset;
      this.dryRunFor = dryRunFor;
      this.serverAddress = serverAddress;
      this.basePath = basePath;
      this.fileSet = fileSet;
      this.responseStreamFactory = responseStreamFactory;
    }

    @Override
    protected void configure() {
      FlagsImpl flags = new FlagsImpl();
      flags.setTests(tests);
      flags.setReset(reset);
      flags.setDryRunFor(dryRunFor);
      install(new FlagsModule(flags));
      bind(new TypeLiteral<Set<FileInfo>>() {})
          .annotatedWith(Names.named("originalFileSet"))
          .toInstance(fileSet);
      bind(String.class)
          .annotatedWith(Names.named("server"))
          .toInstance(serverAddress);
      bind(Boolean.class)
          .annotatedWith(Names.named("debug"))
          .toInstance(Boolean.FALSE);
     

      bind(new TypeLiteral<List<Action>>() {
      }).toProvider(ActionListProvider.class);

      // TODO(corysmith): Change this to an actual class, so that we can JITI
      // it.
      bind(ResponseStreamFactory.class).toInstance(responseStreamFactory);
      bind(File.class).annotatedWith(Names.named("basePath")).toInstance(
          basePath);
      bind(new TypeLiteral<List<BrowserAction>>() {
      }).toProvider(BrowserActionProvider.class);
      bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());

      bind(Long.class).annotatedWith(Names.named("testSuiteTimeout"))
          .toInstance(DefaultConfiguration.DEFAULT_TEST_TIMEOUT);

      for (Module module : modules) {
        install(module);
      }
      bind(new TypeLiteral<Set<FileInfo>>() {
      }).annotatedWith(Names.named("fileSet"))
          .toProvider(FileSetProvider.class).in(Singleton.class);
      bind(new TypeLiteral<List<FileInfo>>() {
      }).annotatedWith(Names.named("tests")).toInstance(
          Collections.<FileInfo> emptyList());
      bind(new TypeLiteral<List<FileInfo>>() {
      }).annotatedWith(Names.named("plugins")).toInstance(
        Collections.<FileInfo> emptyList());
    }
  }
}
