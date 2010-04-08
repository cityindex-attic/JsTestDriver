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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.jstestdriver.hooks.TestsPreProcessor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

/**
 * Produces instances of Actions, so they can have observers, and other stuff.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
@Singleton
public class ActionFactory {

  Map<Class<?>, List<Observer>> observers = new HashMap<Class<?>, List<Observer>>();
  private final Provider<JsTestDriverClient> clientProvider;
  private final Set<TestsPreProcessor> testPreProcessors;
  private final long browserTimeout;
  @Inject
  public ActionFactory(Provider<JsTestDriverClient> clientProvider,
                       Set<TestsPreProcessor> testPreProcessors,
                       @Named("browserTimeout") long browserTimeout) {
    this.clientProvider = clientProvider;
    this.testPreProcessors = testPreProcessors;
    this.browserTimeout = browserTimeout;
  }


  public ServerStartupAction getServerStartupAction(Integer port,
      CapturedBrowsers capturedBrowsers, FilesCache preloadedFilesCache,
      URLTranslator urlTranslator, URLRewriter urlRewriter) {
    ServerStartupAction serverStartupAction =
        new ServerStartupAction(port,
                                capturedBrowsers,
                                preloadedFilesCache,
                                urlTranslator,
                                urlRewriter,
                                browserTimeout);

    if (observers.containsKey(CapturedBrowsers.class)) {
      for (Observer o : observers.get(CapturedBrowsers.class)) {
        capturedBrowsers.addObserver(o);
      }
    }
    if (observers.containsKey(ServerStartupAction.class)) {
      serverStartupAction.addObservers(observers.get(ServerStartupAction.class));
    }
    return serverStartupAction;
  }

  public void registerListener(Class<?> clazz, Observer observer) {
    if (!observers.containsKey(clazz)) {
      observers.put(clazz, new LinkedList<Observer>());
    }
    observers.get(clazz).add(observer);
  }

  public ResetAction createResetAction(ResponseStreamFactory responseStreamFactory) {
    return new ResetAction(responseStreamFactory);
  }

  public DryRunAction createDryRunAction(ResponseStreamFactory responseStreamFactory,
      List<String> expressions) {
    return new DryRunAction(responseStreamFactory, expressions);
  }

  public RunTestsAction createRunTestsAction(ResponseStreamFactory responseStreamFactory,
      List<String> tests, boolean captureConsole) {
    return new RunTestsAction(responseStreamFactory, tests, captureConsole, testPreProcessors);
  }

  public EvalAction createEvalAction(ResponseStreamFactory responseStreamFactory, String cmd) {
    return new EvalAction(responseStreamFactory, cmd);
  }

  public JsTestDriverClient getJsTestDriverClient(Set<FileInfo> filesList, String serverAddress) {
    return clientProvider.get();
  }
}
