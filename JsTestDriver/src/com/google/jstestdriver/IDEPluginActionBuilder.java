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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.jstestdriver.guice.FlagsModule;

/**
 * A builder for IDE's to use. Minimizes the surface area of the API which needs to
 * be maintained on the IDE plugin side.
 * TODO(jeremiele) We should rename this for other API uses. Refactor the crap out of this.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IDEPluginActionBuilder {

  private final ConfigurationParser configParser;
  private final String serverAddress;
  private final ResponseStreamFactory responseStreamFactory;

  private List<String> tests = new LinkedList<String>();
  private final LinkedList<Module> modules = new LinkedList<Module>();
  private boolean dryRun = false;
  private boolean reset = false;

  public IDEPluginActionBuilder(ConfigurationParser configParser, String serverAddress,
      ResponseStreamFactory responseStreamFactory) {
    this.configParser = configParser;
    this.serverAddress = serverAddress;
    this.responseStreamFactory = responseStreamFactory;
  }

  public IDEPluginActionBuilder addAllTests() {
    tests.add("all");
    return this;
  }
  
  public IDEPluginActionBuilder addTests(List<String> testCases) {
    tests.addAll(testCases);
    return this;
  }

  public IDEPluginActionBuilder dryRun() {
    dryRun = true;
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
    // TODO(corysmith): Get this injector out of here, once we have a good
    // methodology for creating a clean API. It would be best to have a single parent injector,
    // and create new injector with every action runner.
    configParser.parse();
    Injector injector = Guice.createInjector(modules).createChildInjector(
        new ActionFactoryModule(),
        new ConfigurationModule(tests, reset, dryRun, serverAddress, configParser.getFilesList(),
            responseStreamFactory, configParser.getServer()));

    return injector.getInstance(ActionRunner.class);
  }

  private static class ConfigurationModule extends AbstractModule{

    private final List<String> tests;
    private final boolean reset;
    private final boolean dryRun;
    private final String serverAddress;
    private final Set<FileInfo> fileSet;
    private final ResponseStreamFactory responseStreamFactory;
    private final String defaultServer;

    public ConfigurationModule(List<String> tests, boolean reset, boolean dryRun,
        String serverAddress, Set<FileInfo> fileSet, ResponseStreamFactory responseStreamFactory,
        String defaultServer) {
      this.tests = tests;
      this.reset = reset;
      this.dryRun = dryRun;
      this.serverAddress = serverAddress;
      this.fileSet = fileSet;
      this.responseStreamFactory = responseStreamFactory;
      this.defaultServer = defaultServer;
    }

    @Override
    protected void configure() {
      FlagsImpl flags = new FlagsImpl();
      flags.setTests(tests);
      flags.setReset(reset);
      flags.setDryRun(dryRun);
      flags.setServer(serverAddress);
      install(new FlagsModule(flags));

      // TODO(corysmith): Change this to an actual class, so that we can JITI it.
      bind(new TypeLiteral<Set<FileInfo>>(){})
          .annotatedWith(Names.named("fileSet"))
          .toInstance(fileSet != null ? fileSet : Collections.<FileInfo>emptySet());
      bind(new TypeLiteral<List<Action>>(){}).toProvider(ActionListProvider.class);
      bind(ResponseStreamFactory.class).toInstance(responseStreamFactory);
      bind(String.class)
          .annotatedWith(Names.named("defaultServerAddress")).toInstance(defaultServer);
    }
  }
}
