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
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.guice.ThreadedActionProvider;
import com.google.jstestdriver.hooks.ActionListProcessor;
import com.google.jstestdriver.output.XmlPrinter;

/**
 * Provides a sequence of actions from a large number of arguments.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 * @author corysmith@google.com (Cory Smith)
 */
@Singleton
public class DefaultActionListProvider implements ActionListProvider {

  private final ActionFactory actionFactory;
  private final FileLoader fileLoader;
  private final List<String> tests;
  private final List<String> arguments;
  private final Set<BrowserRunner> browsers;
  private final boolean reset;
  private final List<String> dryRunFor;
  private final int port;
  private final String server;
  private final boolean preloadFiles;
  private final Set<FileInfo> fileSet;
  private final String testOutput;
  private final ResponseStreamFactory responseStreamFactory;
  private final ThreadedActionProvider threadedActionProvider;
  private final Provider<JsTestDriverClient> clientProvider;
  private final Provider<URLTranslator> urlTranslatorProvider;
  private final Provider<URLRewriter> urlRewriterProvider;
  private final FailureAccumulator accumulator;
  private final Set<ActionListProcessor> processors;
  private final XmlPrinter xmlPrinter;

  // TODO(corysmith): Refactor this. Currently in a temporary,
  //  make dependencies visible to aid refactoring state.
  @Inject
  public DefaultActionListProvider(
      ActionFactory actionFactory,
      FileLoader fileLoader,
      @Named("tests") List<String> tests,
      @Named("arguments") List<String> arguments,
      Set<BrowserRunner> browsers,
      @Named("reset") boolean reset,
      @Named("dryRunFor") List<String> dryRunFor,
      @Named("preloadFiles") boolean preloadFiles,
      @Named("port") int port,
      @Named("fileSet") Set<FileInfo> fileSet,
      @Named("server") String server,
      @Named("testOutput") String testOutput,
      ResponseStreamFactory responseStreamFactory,
      ThreadedActionProvider threadedActionProvider,
      Provider<JsTestDriverClient> clientProvider,
      Provider<URLTranslator> urlTranslatorProvider,
      Provider<URLRewriter> urlRewriterProvider,
      FailureAccumulator accumulator,
      Set<ActionListProcessor> processors,
      XmlPrinter xmlPrinter) {
    this.actionFactory = actionFactory;
    this.fileLoader = fileLoader;
    this.tests = tests;
    this.arguments = arguments;
    this.browsers = browsers;
    this.reset = reset;
    this.dryRunFor = dryRunFor;
    this.preloadFiles = preloadFiles;
    this.port = port;
    this.fileSet = fileSet;
    this.server = server;
    this.testOutput = testOutput;
    this.responseStreamFactory = responseStreamFactory;
    this.threadedActionProvider = threadedActionProvider;
    this.clientProvider = clientProvider;
    this.urlTranslatorProvider = urlTranslatorProvider;
    this.urlRewriterProvider = urlRewriterProvider;
    this.accumulator = accumulator;
    this.processors = processors;
    this.xmlPrinter = xmlPrinter;
  }

  @Provides
  public List<Action> get() {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(actionFactory,
                                  fileLoader,
                                  responseStreamFactory,
                                  threadedActionProvider,
                                  clientProvider,
                                  urlTranslatorProvider,
                                  urlRewriterProvider,
                                  accumulator);
    builder.usingFiles(fileSet, preloadFiles)
           .addTests(tests)
           .addCommands(arguments)
           .onBrowsers(browsers)
           .reset(reset)
           .asDryRunFor(dryRunFor)
           .withLocalServerPort(port)
           .withRemoteServer(server);
    if (testOutput.length() > 0) {
      builder.printingResultsWhenFinished(xmlPrinter);
    }
           
    List<Action> actions = builder.build();
    for (ActionListProcessor processor : processors) {
      actions = processor.process(actions);
    }
    return actions;
  }
}
