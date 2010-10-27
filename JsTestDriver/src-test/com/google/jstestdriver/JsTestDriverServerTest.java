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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import junit.framework.TestCase;

import com.google.common.collect.ImmutableSet;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.hooks.FileInfoScheme;
import com.google.jstestdriver.model.NullPathPrefix;
import com.google.jstestdriver.util.NullStopWatch;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServerTest extends TestCase {

  private CapturedBrowsers browsers = new CapturedBrowsers();
  private JsTestDriverServerImpl server = new JsTestDriverServerImpl(4224, browsers,
      new FilesCache(new HashMap<String, FileInfo>()), SlaveBrowser.TIMEOUT,
      null, Collections.<AuthStrategy> emptySet(), new NullPathPrefix());

  @Override
  protected void tearDown() throws Exception {
    server.stop();
  }

  public void testServerLifecycle() throws Exception {
    server.start();
    URL url = new URL("http://localhost:4224/hello");
    assertEquals("hello", read(url.openStream()));
  }

  private String read(InputStream inputStream) throws IOException {
    StringBuilder builder = new StringBuilder();
    int ch;

    while ((ch = inputStream.read()) != -1) {
      builder.append((char) ch);
    }
    return builder.toString();
  }

  public void testStaticFiles() throws Exception {
    server.start();
    URL url = new URL("http://localhost:4224/slave/XXX/HeartbeatClientquirks.html");
    assertTrue(read(url.openStream()).length() > 0);
  }

  public void testCapture() throws Exception {
    server.start();
    URL captureUrl = new URL("http://localhost:4224/capture");
    assertTrue(read(captureUrl.openStream()).length() > 0);
    assertEquals(1, browsers.getBrowsers().size());
    assertEquals(1, browsers.getBrowsers().get(0).getId().intValue());
  }

  public void testCaptureWithId() throws Exception {
    server.start();
    URL captureUrl = new URL("http://localhost:4224/capture?id=5");
    assertTrue(read(captureUrl.openStream()).length() > 0);
    assertEquals(1, browsers.getBrowsers().size());
    assertEquals(5, browsers.getBrowsers().get(0).getId().intValue());
  }

  public void testListBrowsers() throws Exception {
    final NullStopWatch stopWatch = new NullStopWatch();
    JsTestDriverClient client = new JsTestDriverClientImpl(
        new CommandTaskFactory(
            new DefaultFileFilter(),
            null,
            null,
            stopWatch,
           ImmutableSet.<FileInfoScheme>of(new HttpFileInfoScheme()),
           new NullPathPrefix()),
        "http://localhost:4224",
        new HttpServer(),
        false,
        null);

    server.start();
    Collection<BrowserInfo> browsers = client.listBrowsers();
    assertEquals(0, browsers.size());
  }

  public void testShouldNotifyObserversOnServerStart() throws Exception {
    MockObserver o = new MockObserver();
    server.addObserver(o);
    server.start();
    assertSame(o.event, JsTestDriverServer.Event.STARTED);
    assertEquals(1, o.numCalls);
  }

  public void testShouldNotifyObserversOnServerStop() throws Exception {
    MockObserver o = new MockObserver();
    server.addObserver(o);
    server.start();
    server.stop();
    assertSame(o.event, JsTestDriverServer.Event.STOPPED);
    assertEquals(2, o.numCalls);
  }

  private static class MockObserver implements Observer {
    Object event;
    int numCalls = 0;
    public void update(Observable o, Object arg) {
      event = arg;
      numCalls++;
    }
  }
}
