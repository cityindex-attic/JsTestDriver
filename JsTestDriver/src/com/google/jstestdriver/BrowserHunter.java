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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserHunter {

  private static final Logger logger =
      LoggerFactory.getLogger(BrowserHunter.class.getName());

  private static final String REMOTE_CONSOLE_RUNNER =
      "/slave/%s/RemoteConsoleRunner%s.html";

  private final CapturedBrowsers capturedBrowsers;

  private final long browserTimeout;

  public BrowserHunter(CapturedBrowsers capturedBrowsers, long browserTimeout) {
    this.capturedBrowsers = capturedBrowsers;
    this.browserTimeout = browserTimeout;
  }

  public SlaveBrowser captureBrowser(String name, String version, String os) {
    return captureBrowser(capturedBrowsers.getUniqueId(), name, version, os);
  }

  public String getCaptureUrl(String id, String mode) {
    return String.format(REMOTE_CONSOLE_RUNNER, id, mode);
  }

  /**
   * Begins tracking a browser intheCpaturedBrowsers collection.
   * @param rawId
   * @param name
   * @param version
   * @param os
   * @return
   */
  public SlaveBrowser captureBrowser(String rawId, String name, String version, String os) {

    BrowserInfo browserInfo = new BrowserInfo();

    final Integer id = parseBrowserId(rawId);
    browserInfo.setId(id);
    browserInfo.setName(name);
    browserInfo.setVersion(version);
    browserInfo.setOs(os);
    SlaveBrowser slave =
        new SlaveBrowser(new TimeImpl(), id.toString(), browserInfo, browserTimeout);

    capturedBrowsers.addSlave(slave);
    logger.info("Browser Captured: {} version {} ({})", new Object[] {name, version, id});
    return slave;
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
