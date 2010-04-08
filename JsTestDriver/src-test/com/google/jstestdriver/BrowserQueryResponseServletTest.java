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
import com.google.jstestdriver.Response.ResponseType;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserQueryResponseServletTest extends TestCase {

  private final Gson gson = new Gson();
  private final ByteArrayOutputStream out = new ByteArrayOutputStream();
  private final PrintWriter writer = new PrintWriter(out);

  public void testGetDataFromJsPuppetServer() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo(), SlaveBrowser.TIMEOUT);
    String data = "hello";

    slave.createCommand(data);
    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers, null, null);

    servlet.service(id, null, null, null, writer);
    assertEquals(data, out.toString());
  }

  public void testSettingResponseForACommand() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo(), SlaveBrowser.TIMEOUT);

    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers, null, null);
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
    servlet.service(id, null, gson.toJson(response), "true", writer);
    assertEquals("BrowserCommand", out.toString());
    assertEquals(response, slave.getResponse().getResponse());
  }

  public void testSimulatePollTimeoutDequeueNullCommand() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo(), SlaveBrowser.TIMEOUT);

    slave.setDequeueTimeout(0L, TimeUnit.NANOSECONDS);
    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers, null, null);

    servlet.service(id, null, null, null, writer);
    assertEquals("noop", out.toString());
  }

  public void testConnectionHeartBeat() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    MockTime time = new MockTime(42L);
    SlaveBrowser slave = new SlaveBrowser(time, id, new BrowserInfo(), SlaveBrowser.TIMEOUT);
    String data = "hello";

    slave.createCommand(data);
    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers, null, null);

    servlet.service(id, null, null, null, writer);
    assertEquals(42L, slave.getLastHeartBeat().getMillis());
  }

  public void testBrowserIsNotSlave() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserQueryResponseServlet servlet =
        new BrowserQueryResponseServlet(capturedBrowsers, null, null);

    servlet.service("1", null, "response", "", writer);
    assertEquals(0, out.toString().length());
  }

  public void testDoNotGetCommandIfNotLastResponse() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo(), SlaveBrowser.TIMEOUT);

    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet = new BrowserQueryResponseServlet(browsers, null, null);
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

  public void testFilesLoadedAreAddedToTheBrowserFileSet() throws Exception {
    CapturedBrowsers browsers = new CapturedBrowsers();
    String id = "1";
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, new BrowserInfo(), SlaveBrowser.TIMEOUT);

    browsers.addSlave(slave);
    BrowserQueryResponseServlet servlet =
        new BrowserQueryResponseServlet(browsers, null, new ForwardingMapper());
    List<FileResult> fileResults = new LinkedList<FileResult>();

    fileResults.add(new FileResult(new FileSource("/test/filename1.js", 123), true, ""));
    fileResults.add(new FileResult(new FileSource("/test/filename2.js", 456), true, ""));
    fileResults.add(new FileResult(new FileSource("/test/filename3.js", 789), true, ""));
    slave.createCommand("awaitingResponse");
    slave.dequeueCommand();
    slave.createCommand("BrowserCommand");
    Response response = new Response();

    response.setType(ResponseType.FILE_LOAD_RESULT.name());
    response.setResponse(gson.toJson(new LoadedFiles(fileResults)));
    servlet.service("1", null, gson.toJson(response), "", writer);

    Set<FileInfo> fileInfos = slave.getFileSet();

    assertEquals(3, fileInfos.size());
    Iterator<FileInfo> iterator = fileInfos.iterator();

    FileInfo info1 = iterator.next();

    assertEquals("filename1.js", info1.getFileName());
    assertEquals(123, info1.getTimestamp());

    FileInfo info2 = iterator.next();

    assertEquals("filename2.js", info2.getFileName());
    assertEquals(456, info2.getTimestamp());

    FileInfo info3 = iterator.next();

    assertEquals("filename3.js", info3.getFileName());
    assertEquals(789, info3.getTimestamp());
  }
}
