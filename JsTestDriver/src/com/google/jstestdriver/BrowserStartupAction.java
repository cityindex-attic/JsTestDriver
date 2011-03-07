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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.browser.BrowserControl;
import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.model.RunData;
import com.google.jstestdriver.util.StopWatch;

/**
 * Starts a list of browsers when run.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserStartupAction implements Action {

  private final Set<BrowserRunner> browsers;
  private final String serverAddress;
  private final JsTestDriverClient client;
  private final StopWatch stopWatch;
  private final ExecutorService executor;

  @Inject
  public BrowserStartupAction(Set<BrowserRunner> browsers,
                              StopWatch stopWatch,
                              JsTestDriverClient client,
                              @Named("server") String serverAddress,
                              ExecutorService executor) {
      this.browsers = browsers;
      this.stopWatch = stopWatch;
      this.client = client;
      this.serverAddress = serverAddress;
      this.executor = executor;
  }

  public RunData run(RunData runData) {
    List<Future<String>> browserIds = Lists.newArrayListWithCapacity(browsers.size());
    for (final BrowserRunner browser : browsers) {
      browserIds.add(executor.submit(new Callable<String>() {
        public String call() throws Exception {
          return new BrowserControl(browser, serverAddress, stopWatch, client)
              .captureBrowser(client.getNextBrowserId());
        }
      }));
    }
    // check for exceptions. Gotta be a better way.
    for (Future<String> futureId : browserIds) {
      try {
        futureId.get();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
    return runData;
  }

  public Set<BrowserRunner> getBrowsers() {
    return browsers;
  }

  public String getServerAddress() {
    return serverAddress;
  }
}
