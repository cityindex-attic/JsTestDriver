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
package com.google.jstestdriver.browser;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.BrowserAction;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.RunTestsAction;
import com.google.jstestdriver.TestErrors;
import com.google.jstestdriver.model.JstdTestCase;
import com.google.jstestdriver.model.RunData;
import com.google.jstestdriver.util.RetryingCallable;
import com.google.jstestdriver.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Executes each {@link BrowserAction} on each browser.
 *
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserActionExecutorAction implements Action {
  private static final Logger logger = LoggerFactory.getLogger(BrowserActionExecutorAction.class);

  private final JsTestDriverClient client;
  private final List<BrowserAction> actions;
  private final ExecutorService executor;
  private final Set<BrowserRunner> browserRunners;
  private final String serverAddress;
  private final long testSuiteTimeout;

  private final StopWatch stopWatch;

  private final BrowserSessionManager sessionManager;

  @Inject
  public BrowserActionExecutorAction(JsTestDriverClient client,
      List<BrowserAction> actions,
      ExecutorService executor,
      Set<BrowserRunner> browserRunners,
      @Named("server") String serverAddress,
      @Named("testSuiteTimeout") long testTimeout,
      StopWatch stopWatch,
      BrowserSessionManager sessionManager) {
    this.client = client;
    this.actions = actions;
    this.executor = executor;
    this.browserRunners = browserRunners;
    this.serverAddress = serverAddress;
    this.testSuiteTimeout = testTimeout;
    this.stopWatch = stopWatch;
    this.sessionManager = sessionManager;
  }

  public RunData run(RunData runData) {
    stopWatch.start("run %s", actions);
    logger.trace("Starting BrowserActions {}.", actions);
    Collection<BrowserInfo> browsers = client.listBrowsers();
    if (browsers.size() == 0 && browserRunners.size() == 0 && actions.size() > 0) {
      throw new RuntimeException("No browsers available, yet actions requested. " +
          "If running against a persistent server please capture browsers. "+
          "Otherwise, ensure that browsers are defined.");
    }
    // TODO(corysmith): Change the threaded action runner to
    // return useful information about a run.
    List<Callable<Collection<ResponseStream>>> runners = Lists.newLinkedList();
    for (JstdTestCase testCase : runData.getTestCases()) {
      for (BrowserInfo browserInfo : browsers) {
        runners.add(new BrowserActionRunner(browserInfo.getId().toString(),
            client,
            actions,
            stopWatch,
            testCase,
            sessionManager));
        logger.debug("Queueing BrowserActionRunner {} for {}.", actions, browserInfo);
      }
      for (BrowserRunner runner : browserRunners) {
        String browserId = client.getNextBrowserId();
        final BrowserActionRunner actionRunner =
          new BrowserActionRunner(browserId, client, actions, stopWatch, testCase, sessionManager);
        runners.add(createBrowserManagedRunner(runData, runner, browserId, actionRunner));
        logger.debug("Queueing BrowserActionRunner {} for {}.", actions, runner);
      }
    }
    List<Throwable> exceptions = Lists.newLinkedList();
    long currentTimeout = testSuiteTimeout;
    try {
      final List<Future<Collection<ResponseStream>>> results =
          executor.invokeAll(runners, currentTimeout, TimeUnit.SECONDS);

      for (Future<Collection<ResponseStream>> result : results) {
        try {
          for (ResponseStream response : result.get()) {
            runData = runData.recordResponse(response);
          }
        } catch (CancellationException e) {
          exceptions.add(new RuntimeException(
              "Test run cancelled, exceeded " + currentTimeout + "s", e));
        } catch (ExecutionException e) {
          exceptions.add(e.getCause());
        } catch (Exception e) {
          exceptions.add(e);
        }
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      // something isn't working....
      executor.shutdownNow();
    }
    logger.debug("Finished BrowserActions {}.", actions);
    if (!exceptions.isEmpty()) {
      throw new TestErrors("Failures during test run.", exceptions);
    }

    // TODO(corysmith): Move this to the ActionRunner?
    runData.finish(); // finalizes the rundata collection.

    stopWatch.stop("run %s", actions);
    return runData;
  }

  // TODO(corysmith): Pull this into a factory.
  private Callable<Collection<ResponseStream>> createBrowserManagedRunner(RunData runData, BrowserRunner runner,
      String browserId, BrowserActionRunner actionRunner) {
    return new RetryingCallable<Collection<ResponseStream>>(runner.getNumStartupTries(),
        new BrowserCallable<Collection<ResponseStream>>(actionRunner, browserId,
      new BrowserControl(runner, serverAddress, stopWatch, client))) ;
  }

  public List<BrowserAction> getActions() {
    return actions;
  }

  public RunTestsAction getRunTestsAction() {
    for (BrowserAction action : actions) {
      if (action instanceof RunTestsAction) {
        return (RunTestsAction) action;
      }
    }
    return null;
  }

  public JsTestDriverClient getClient() {
    return client;
  }
}
