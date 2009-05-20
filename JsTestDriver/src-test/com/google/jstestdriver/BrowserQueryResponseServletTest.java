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

import com.google.gson.Gson;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserQueryResponseServletTest extends TestCase {

  private ByteArrayOutputStream out = new ByteArrayOutputStream();
  private PrintWriter writer = new PrintWriter(out);

  public void testGetDataFromJsPuppetServer() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo());
    String data = "hello";

    slave.createCommand(data);
    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers);

    servlet.service(id, null, null, null, writer);
    assertEquals(data, out.toString());
  }

  public void testSettingResponseForACommand() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo());

    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers);
    slave.createCommand("awaitingResponse");
    slave.dequeueCommand();
    slave.createCommand("BrowserCommand");
    Gson gson = new Gson();
    Response response = new Response();

    response.setResponse("response");
    BrowserInfo browserInfo = new BrowserInfo();

    browserInfo.setId(1);
    browserInfo.setName("browser");
    browserInfo.setOs("OS");
    browserInfo.setVersion("version");
    response.setBrowser(browserInfo);
    servlet.service(id, null, gson.toJson(response), "", writer);
    assertEquals("BrowserCommand", out.toString());
    assertEquals("{\"response\":\"response\",\"browser\":{\"id\":1,\"name\":\"browser\"," +
    		"\"version\":\"version\",\"os\":\"OS\"},\"error\":\"\",\"executionTime\":0}",
    		slave.getResponse().getResponse());
  }

  public void testSimulatePollTimeoutDequeueNullCommand() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo());

    slave.setDequeueTimeout(0L, TimeUnit.NANOSECONDS);
    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers);

    servlet.service(id, null, null, null, writer);
    assertEquals("noop", out.toString());
  }

  public void testConnectionHeartBeat() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    MockTime time = new MockTime(42L);
    SlaveBrowser slave = new SlaveBrowser(time, id, new BrowserInfo());
    String data = "hello";

    slave.createCommand(data);
    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers);

    servlet.service(id, null, null, null, writer);
    assertEquals(42L, slave.getLastHeartBeat().getMillis());
  }

  public void testBrowserIsNotSlave() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(capturedBrowsers);

    servlet.service("1", null, "response", "", writer);
    assertEquals(0, out.toString().length());
  }

  public void testDoNotGetCommandIfNotLastResponse() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo());

    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers);
    slave.createCommand("awaitingResponse");
    slave.dequeueCommand();
    slave.createCommand("BrowserCommand");
    Gson gson = new Gson();
    Response response = new Response();

    response.setResponse("response");
    BrowserInfo browserInfo = new BrowserInfo();

    browserInfo.setId(1);
    browserInfo.setName("browser");
    browserInfo.setOs("OS");
    browserInfo.setVersion("version");
    response.setBrowser(browserInfo);
    servlet.service(id, null, gson.toJson(response), null, writer);
    assertEquals("noop", out.toString());    
  }
}
