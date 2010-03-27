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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class HomeServletTest extends TestCase {

  public void testDisplayInfo() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserInfo browserInfo = new BrowserInfo();

    browserInfo.setId(1);
    browserInfo.setName("browser");
    browserInfo.setOs("OS");
    browserInfo.setVersion("1.0");
    SlaveBrowser slave = new SlaveBrowser(new MockTime(0), "1", browserInfo);

    capturedBrowsers.addSlave(slave);
    HomeServlet servlet = new HomeServlet(capturedBrowsers);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    PrintWriter writer = new PrintWriter(stream);

    servlet.service(writer);
    assertEquals("<html><head><title>JsTestDriver</title></head><body>"
        + "<a href=\"/capture\">Capture This Browser</a><br/>"
        + "<a href=\"/capture?strict\">Capture This Browser in strict mode</a>"
        + "<br/><p>Captured Browsers:<br/><p>Id: 1<br/>Name: browser<br/>"
        + "Version: 1.0<br/>Operating System: OS<br/>Currently waiting..."
        + "<br/></p></p></body></html>", stream.toString());
  }
}
