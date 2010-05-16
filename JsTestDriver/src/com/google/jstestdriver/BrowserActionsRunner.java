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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.browser.BrowserManagedRunner;
import com.google.jstestdriver.browser.BrowserRunner;

/**
 * Executes each {@link BrowserAction} on each browser.
 *
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserActionsRunner implements Action {
  private static final Logger logger = LoggerFactory.getLogger(BrowserActionsRunner.class);

  private final JsTestDriverClient client;
  private final List<BrowserAction> actions;
  private final ExecutorService executor;
  private final Set<BrowserRunner> browserRunners;
  private final String serverAddress;
  private final long testSuiteTimeout;

  @Inject
  public BrowserActionsRunner(JsTestDriverClient client, List<BrowserAction> actions,
      ExecutorService executor, Set<BrowserRunner> browserRunners,
      @Named("server") String serverAddress,
      @Named("testSuiteTimeout") long testTimeout) {
    this.client = client;
    this.actions = actions;
    this.executor = executor;
    this.browserRunners = browserRunners;
    this.serverAddress = serverAddress;
    this.testSuiteTimeout = testTimeout;
  }

  public void run() {
    logger.trace("Starting BrowserActions {}.", actions);
    Collection<BrowserInfo> browsers = client.listBrowsers();
    if (browsers.size() == 0 && browserRunners.size() == 0 && actions.size() > 0) {
      throw new RuntimeException("No browsers available, yet actions requested. " +
          "If running against a persistent server please capture browsers. "+
          "Otherwise, ensure that browsers are defined.");
    }
    // TODO(corysmith): Change the threaded action runner to
    // return useful information about a run.
    List<Callable<ResponseStream>> runners = Lists.newLinkedList();
    for (BrowserInfo browserInfo : browsers) {
      runners.add(new BrowserActionRunner(browserInfo.getId().toString(), client, actions));
    }
    for (BrowserRunner runner : browserRunners) {
      String browserId = client.getNextBrowserId();
      runners.add(new BrowserManagedRunner(runner, browserId, serverAddress, client,
          new BrowserActionRunner(browserId, client, actions)));
    }
    List<Throwable> exceptions = Lists.newLinkedList();
    final List<ResponseStream> streams = Lists.newLinkedList();
    long currentTimeout = testSuiteTimeout;
    try {
      final List<Future<ResponseStream>> results = executor.invokeAll(runners);
      for (Future<ResponseStream> result : results) {
          try {
            streams.add(result.get(currentTimeout, TimeUnit.SECONDS));
          } catch (ExecutionException e) {
            exceptions.add(e.getCause());
          } catch(TimeoutException e) {
            currentTimeout = 0; //one timeout, skip the rest.¯
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
    // TODO(corysmith):Remove this when there is a better way to return results
    // of a list of actions.
    for (ResponseStream stream : streams) {
      stream.finish();
    }
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
