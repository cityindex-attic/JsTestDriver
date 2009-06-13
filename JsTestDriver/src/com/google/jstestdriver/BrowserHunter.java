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

  private static final Logger logger = LoggerFactory.getLogger(BrowserHunter.class.getName());

  private final CapturedBrowsers capturedBrowsers;

  public BrowserHunter(CapturedBrowsers capturedBrowsers) {
    this.capturedBrowsers = capturedBrowsers;    
  }

  public SlaveBrowser captureBrowser(String name, String version, String os) {
    String id = capturedBrowsers.getUniqueId();
    BrowserInfo browserInfo = new BrowserInfo();

    browserInfo.setId(Integer.valueOf(id));
    browserInfo.setName(name);
    browserInfo.setVersion(version);
    browserInfo.setOs(os);
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, browserInfo);

    capturedBrowsers.addSlave(slave);
    logger.info("Browser Captured: {} version {} ({})", new Object[] {name, version, id});
    return slave;
  }

  public String getCaptureUrl(String id) {
    return String.format("/slave/%s/RemoteConsoleRunner.html", id);
  }
}
