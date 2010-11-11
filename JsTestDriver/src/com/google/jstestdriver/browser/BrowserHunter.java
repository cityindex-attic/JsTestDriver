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

import static com.google.jstestdriver.runner.RunnerType.BROWSER;
import static com.google.jstestdriver.runner.RunnerType.CLIENT;
import static com.google.jstestdriver.runner.RunnerType.STANDALONE;
import static com.google.jstestdriver.server.handlers.CaptureHandler.RUNNER_TYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.TimeImpl;
import com.google.jstestdriver.annotations.BrowserTimeout;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.runner.RunnerType;
import com.google.jstestdriver.server.handlers.pages.PageType;

/**
 * Creates SlaveBrowsers.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserHunter {

  private static final Logger logger =
      LoggerFactory.getLogger(BrowserHunter.class.getName());

  private static final String CLIENT_CONSOLE_RUNNER =
    "/slave/id/%s/page/CONSOLE/mode/%s/" + RUNNER_TYPE + "/" + CLIENT;
  
  private static final String STANDALONE_CONSOLE_RUNNER =
    "/runner/id/%s/page/RUNNER/mode/%s/timeout/%s/" + RUNNER_TYPE + "/" + STANDALONE;

  private static final String BROWSER_CONTROLLED_RUNNER =
    "/bcr/id/%s/page/" + PageType.VISUAL_STANDALONE_RUNNER + "/mode/%s/timeout/%s/" + RUNNER_TYPE + "/" + BROWSER;

  private final CapturedBrowsers capturedBrowsers;

  private final long browserTimeout;

  private final HandlerPathPrefix prefix;

  @Inject
  public BrowserHunter(CapturedBrowsers capturedBrowsers,
      @BrowserTimeout long browserTimeout,
      HandlerPathPrefix prefix) {
    this.capturedBrowsers = capturedBrowsers;
    this.browserTimeout = browserTimeout;
    this.prefix = prefix;
  }

  public SlaveBrowser captureBrowser(String name, String version, String os) {
    return captureBrowser(capturedBrowsers.getUniqueId(), name, version, os);
  }

  public String getCaptureUrl(String id, String mode, RunnerType type, Long timeout) {
    long computedBrowserTimeout = computeTimeout(timeout);
    switch(type) {
      case CLIENT:
        return prefix.prefixPath(String.format(CLIENT_CONSOLE_RUNNER, id, mode, computedBrowserTimeout));
      case STANDALONE:
        return prefix.prefixPath(String.format(STANDALONE_CONSOLE_RUNNER, id, mode, computedBrowserTimeout));
      case BROWSER:
        return prefix.prefixPath(String.format(BROWSER_CONTROLLED_RUNNER, id, mode, computedBrowserTimeout));
    }
    throw new UnsupportedOperationException("Unsupported Runner type: " + type);
  }

  /**
   * Begins tracking a browser in the CapturedBrowsers collection.
   * @param rawId
   * @param name
   * @param version
   * @param os
   * @return A slave browser
   */
  public SlaveBrowser captureBrowser(String rawId, String name, String version, String os) {
    return captureBrowser(rawId, name, version, os, browserTimeout);
  }

  public SlaveBrowser captureBrowser(String rawId, String name, String version, String os, Long browserTimeout) {
    BrowserInfo browserInfo = new BrowserInfo();

    final Integer id = parseBrowserId(rawId);
    browserInfo.setId(id);
    browserInfo.setName(name);
    browserInfo.setVersion(version);
    browserInfo.setOs(os);
    // TODO(corysmith):move all browser timeout configuration to the capture servlet.
    long computedBrowserTimeout = computeTimeout(browserTimeout);
    SlaveBrowser slave =
      new SlaveBrowser(new TimeImpl(), id.toString(), browserInfo, computedBrowserTimeout);

    capturedBrowsers.addSlave(slave);
    logger.debug("Browser Captured: {}", slave);
    logger.info("Browser Captured: {} version {} ({})", new Object[] {name, version, id});
    return slave;
  }

  private long computeTimeout(Long browserTimeout) {
    return browserTimeout == null ? this.browserTimeout : browserTimeout;
  }

  private Integer parseBrowserId(String id) {
    if (id == null) {
      return Integer.parseInt(capturedBrowsers.getUniqueId());
    }
    return Integer.valueOf(id);
  }

  public boolean isBrowserCaptured(String id) {
    return capturedBrowsers.getBrowser(id) != null;
  }

  public void freeBrowser(String id) {
    capturedBrowsers.removeSlave(id);
  }
}
