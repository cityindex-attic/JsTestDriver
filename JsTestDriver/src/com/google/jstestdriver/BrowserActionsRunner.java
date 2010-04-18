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

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Executes each {@link BrowserAction} on each browser.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserActionsRunner implements Action {

  private final JsTestDriverClient client;
  private final List<BrowserAction> actions;
  private final ExecutorService executor;

  public BrowserActionsRunner(JsTestDriverClient client, List<BrowserAction> actions,
      ExecutorService executor) {
    this.client = client;
    this.actions = actions;
    this.executor = executor;
  }

  public void run() {
    Collection<BrowserInfo> browsers = client.listBrowsers();
    int browsersNumber = browsers.size();

    if (browsersNumber == 0) {
      throw new RuntimeException("No browsers were captured, nothing to run...");
    }
    CountDownLatch latch = new CountDownLatch(browsersNumber);

    // TODO(corysmith): Change the threaded action runner to
    // return useful information about a run.
    List<Future<Boolean>> futures = Lists.newLinkedList();
    for (BrowserInfo browserInfo : browsers) {
      futures.add(executor.submit(
          new BrowserActionRunner(browserInfo.getId().toString(),
          client, latch, actions)));
    }
    executor.shutdown();
    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    for (Future<Boolean> future : futures) {
      try {
        future.get();
      } catch (InterruptedException e) {
        // TODO(corysmith): fix the error reporting to be more useful.
        throw new RuntimeException("Failure during run", e);
      } catch (ExecutionException e) {
        // TODO(corysmith): fix the error reporting to be more useful.
        throw new RuntimeException("Failure during run", e);
      }
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
