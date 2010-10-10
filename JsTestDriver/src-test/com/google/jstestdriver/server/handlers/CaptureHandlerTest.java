/*
 * Copyright 2008 Google Inc.
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
package com.google.jstestdriver.server.handlers;

import junit.framework.TestCase;

import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.browser.BrowserHunter;
import com.google.jstestdriver.model.NullPathPrefix;
import com.google.jstestdriver.runner.RunnerType;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureHandlerTest extends TestCase {

  public void testRedirectQuirksUrl() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    CaptureHandler handler = new CaptureHandler(
        null, null, new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT, new NullPathPrefix()));

    assertEquals("/slave/1/RemoteConsoleRunnerquirks.html", handler.service("Chrome/2.0",
        CaptureHandler.QUIRKS, null, RunnerType.CLIENT_CONTROLLED, null));
  }

  public void testRedirectStrictUrl() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    CaptureHandler handler = new CaptureHandler(
        null, null, new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT, new NullPathPrefix()));

    assertEquals("/slave/1/RemoteConsoleRunnerstrict.html", handler.service("Chrome/2.0",
        CaptureHandler.STRICT, null, RunnerType.CLIENT_CONTROLLED, null));
  }

  public void testRedirectStrictUrlWithId() throws Exception {
    String id = "5";
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    CaptureHandler handler = new CaptureHandler(
        null, null, new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT, new NullPathPrefix()));

    assertEquals("/slave/" + id + "/RemoteConsoleRunnerstrict.html", handler.service("Chrome/2.0",
        CaptureHandler.STRICT, id, RunnerType.CLIENT_CONTROLLED, null));
  }
}