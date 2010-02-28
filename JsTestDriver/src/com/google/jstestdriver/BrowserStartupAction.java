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

import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.jstestdriver.browser.BrowserRunner;

/**
 * Starts a list of browsers when run.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserStartupAction implements Action, Observer {

  private static final Logger logger =
      LoggerFactory.getLogger(BrowserStartupAction.class);
  private final CountDownLatch latch;
  private final Set<BrowserRunner> browsers;
  private final String serverAddress;

  public BrowserStartupAction(Set<BrowserRunner> browsers,
                              String serverAddress,
                              CountDownLatch latch) {
      this.browsers = browsers;
      this.serverAddress = serverAddress;
      this.latch = latch;
  }

  public void run() {
    try {
      String url = String.format("%s/capture", serverAddress);
      for (BrowserRunner browser : browsers) {
        browser.startBrowser(url);
      }
      if (!latch.await(30, TimeUnit.SECONDS)) {
        long count = latch.getCount();

        if (count < browsers.size()) {
          logger.warn("Not all browsers were captured continuing anyway...");
        } else {
          logger.error("None of the browsers were captured after 30 seconds");
          for (BrowserRunner browser : browsers) {
            browser.stopBrowser();
          }
        }
      }
    } catch (InterruptedException e) {
      logger.error("Error in starting browsers: {}", e.toString());
    }
  }

  public void update(Observable o, Object arg) {
    latch.countDown();
  }
  
  public Set<BrowserRunner> getBrowsers() {
    return browsers;
  }
  
  public String getServerAddress() {
    return serverAddress;
  }
}
