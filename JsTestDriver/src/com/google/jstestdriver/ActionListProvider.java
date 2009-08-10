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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.internal.Nullable;
import com.google.inject.name.Named;

/**
 * Parses the flags into a sequence of actions.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionListProvider implements Provider<List<Action>> {

  private final ActionFactory actionFactory;
  private final FileLoader fileLoader;
  private final List<String> tests;
  private final String testOutput;
  private final boolean verbose;
  private final boolean captureConsole;
  private final List<String> arguments;
  private final List<String> browsers;
  private final boolean reset;
  private final boolean dryRun;
  private final int port;
  private final String server;
  private final boolean preloadFiles;
  private final Set<FileInfo> fileSet;
  private final String defaultServerAddress;

  // TODO(corysmith): Refactor this. Currently in a temporary,
  //  make dependencies visible to aid refactoring state.
  @Inject
  public ActionListProvider(ActionFactory actionFactory,
                      FileLoader fileLoader,
                      @Named("tests") List<String> tests,
                      @Named("testOutput") String testOutput,
                      @Named("verbose") boolean verbose,
                      @Named("captureConsole") boolean captureConsole,
                      @Named("arguments") List<String> arguments,
                      @Named("browsers") List<String> browsers,
                      @Named("reset") boolean reset,
                      @Named("dryRun") boolean dryRun,
                      @Named("preloadFiles") boolean preloadFiles,
                      @Named("port") int port,
                      @Named("fileSet") Set<FileInfo> fileSet,
                      @Nullable @Named("server") String server,
                      @Named("defaultServerAddress") String defaultServerAddress) {
    this.actionFactory = actionFactory;
    this.fileLoader = fileLoader;
    this.tests = tests;
    this.testOutput = testOutput;
    this.verbose = verbose;
    this.captureConsole = captureConsole;
    this.arguments = arguments;
    this.browsers = browsers;
    this.reset = reset;
    this.dryRun = dryRun;
    this.preloadFiles = preloadFiles;
    this.port = port;
    this.fileSet = fileSet;
    this.server = server;
    this.defaultServerAddress = defaultServerAddress;
  }
  
  public List<Action> get() {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(actionFactory, fileLoader);
    builder.usingFiles(fileSet, preloadFiles)
           .addTests(tests,
                     testOutput,
                     verbose, 
                     captureConsole)
           .addCommands(arguments)
           .onBrowsers(browsers)
           .reset(reset)
           .asDryRun(dryRun);
    if (port != -1) {
      builder.withLocalServerPort(port);
    } else {
      builder.withRemoteServer(server != null ?
                               server : defaultServerAddress);
    }
    return builder.build();
  }
}
