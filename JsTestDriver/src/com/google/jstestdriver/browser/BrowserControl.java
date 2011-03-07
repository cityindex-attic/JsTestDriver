/*
 * Copyright 2011 Google Inc.
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

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.server.handlers.pages.SlavePageRequest;
import com.google.jstestdriver.util.StopWatch;

/**
 *
 *
 * @author Cory Smith (corbinrsmith@gmail.com) 
 */
public class BrowserControl {
  private static final String CAPTURE_URL =
    String.format("%%s/capture/%s/%%s/%s/%%s/%s/%%s/",
        SlavePageRequest.ID, SlavePageRequest.TIMEOUT, SlavePageRequest.UPLOAD_SIZE);

  private static final Logger logger = LoggerFactory.getLogger(BrowserControl.class);
  private final BrowserRunner runner;
  private final String serverAddress;
  private final StopWatch stopWatch;
  private final JsTestDriverClient client;

  /**
   * @param runner
   * @param stopWatch 
   * @param serverAddress 
   * @param client 
   */
  public BrowserControl(BrowserRunner runner, String serverAddress, StopWatch stopWatch, JsTestDriverClient client) {
    this.runner = runner;
    this.serverAddress = serverAddress;
    this.stopWatch = stopWatch;
    this.client = client;
  }

  /** Slaves a new browser window with the provided id. */
  public String captureBrowser(String browserId) throws InterruptedException {
    final String url = String.format(CAPTURE_URL, serverAddress, browserId, runner.getHeartbeatTimeout(), runner.getUploadSize());
    runner.startBrowser(url);
    long timeOut = TimeUnit.MILLISECONDS.convert(runner.getTimeout(), TimeUnit.SECONDS);
    long start = System.currentTimeMillis();
    // TODO(corysmith): replace this with a stream from the client on browser
    // updates.
    while (!isBrowserCaptured(browserId, client)) {
      Thread.sleep(50);
      if (System.currentTimeMillis() - start > timeOut) {
        throw new RuntimeException("Could not start browser " + runner + " in "
            + runner.getTimeout());
      }
    }
    logger.debug("Browser {} started with id {}", runner, browserId);
    return browserId;
  }

  /** Stop a browser window. */
  public void stopBrowser() {
    stopWatch.start("browser stop %s", runner);
    runner.stopBrowser();
    stopWatch.stop("browser stop %s", runner);
  }

  public boolean isBrowserCaptured(String browserId, JsTestDriverClient client) {
    for (BrowserInfo browserInfo : client.listBrowsers()) {
      if (browserId.equals(String.valueOf(browserInfo.getId())) 
          && browserInfo.serverReceivedHeartbeat()) {
        logger.debug("Started {}", browserInfo);
        return true;
      }
    }
    return false;
  }
}
