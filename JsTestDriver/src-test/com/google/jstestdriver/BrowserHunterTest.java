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

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserHunterTest extends TestCase {

  public void testCaptureAndGenerateUrlQuirks() throws Exception {
    BrowserHunter browserHunter = new BrowserHunter(new CapturedBrowsers());
    SlaveBrowser slaveBrowser = browserHunter.captureBrowser("name", "version", "os");
    BrowserInfo browserInfo = slaveBrowser.getBrowserInfo();

    assertEquals("name", browserInfo.getName());
    assertEquals("version", browserInfo.getVersion());
    assertEquals("os", browserInfo.getOs());
    assertEquals("/slave/1/RemoteConsoleRunnerquirks.html", browserHunter.getCaptureUrl(
        slaveBrowser.getId(), CaptureServlet.QUIRKS));
  }

  public void testCaptureAndGenerateUrlStrict() throws Exception {
    BrowserHunter browserHunter = new BrowserHunter(new CapturedBrowsers());
    SlaveBrowser slaveBrowser = browserHunter.captureBrowser("name", "version", "os");
    BrowserInfo browserInfo = slaveBrowser.getBrowserInfo();

    assertEquals("name", browserInfo.getName());
    assertEquals("version", browserInfo.getVersion());
    assertEquals("os", browserInfo.getOs());
    assertEquals("/slave/1/RemoteConsoleRunnerstrict.html", browserHunter.getCaptureUrl(
        slaveBrowser.getId(), CaptureServlet.STRICT));
  }
}
