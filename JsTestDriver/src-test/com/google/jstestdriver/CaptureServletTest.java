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
package com.google.jstestdriver;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureServletTest extends TestCase {

  public void testRequestingCaptureUrlCausesRedirectToUniqueURL() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    File tmpDir =
      File.createTempFile("test", "JsTestDriver", new File(System.getProperty("java.io.tmpdir")));

    tmpDir.delete();
    tmpDir.mkdir();
    tmpDir.deleteOnExit();
    CaptureServlet servlet = new CaptureServlet("Test.file", browsers);
    String redirectURL = servlet.serviceRemoteConsoleRunner("name", "version", "os");

    assertEquals("/slave/1/RemoteConsoleRunner.html", redirectURL);
    SlaveBrowser browser = browsers.getBrowser("1");

    assertNotNull(browser);
    BrowserInfo browserInfo = browser.getBrowserInfo();

    assertNotNull(browserInfo);
    assertEquals("name", browserInfo.getName());
    assertEquals("version", browserInfo.getVersion());
    assertEquals("os", browserInfo.getOs());
  }

  public void testServeStaticCapturePage() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String location = getClass().getPackage().getName().replace(".", "/");
    CaptureServlet servlet = new CaptureServlet(String.format("%s/Test.file", location), browsers);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    servlet.serviceStaticPage(out);
    assertTrue(out.toString().length() > 0);
    out.close();
  }
}
