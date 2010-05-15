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

import com.google.jstestdriver.browser.BrowserReaper;
import com.google.jstestdriver.servlet.BrowserLoggingServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Timer;

import javax.servlet.Servlet;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServer extends Observable {
  private static final Logger logger =
      LoggerFactory.getLogger(JsTestDriverServer.class);

  private final Server server = new Server();

  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private Context context;

  private final long browserTimeout;

  private Timer timer;

  public JsTestDriverServer(int port,
                            CapturedBrowsers capturedBrowsers,
                            FilesCache preloadedFilesCache,
                            URLTranslator urlTranslator,
                            URLRewriter urlRewriter,
                            long browserTimeout) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = preloadedFilesCache;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.browserTimeout = browserTimeout;
    initJetty(this.port);
    initServlets();
  }

  private void initServlets() {
    ForwardingMapper forwardingMapper = new ForwardingMapper();
    // TODO(corysmith): replace this with Guice injection
    addServlet("/", new HomeServlet(capturedBrowsers));
    addServlet("/hello", new HelloServlet());
    addServlet("/heartbeat", new HeartbeatServlet(capturedBrowsers));
    addServlet("/capture", new CaptureServlet(new BrowserHunter(
      capturedBrowsers, browserTimeout)));
    addServlet("/runner/*", new StandaloneRunnerServlet(new BrowserHunter(
      capturedBrowsers, browserTimeout), filesCache, new StandaloneRunnerFilesFilterImpl(),
      new SlaveResourceService(SlaveResourceService.RESOURCE_LOCATION)));
    addServlet("/slave/*", new SlaveResourceServlet(new SlaveResourceService(
      SlaveResourceService.RESOURCE_LOCATION)));
    addServlet("/cmd", new CommandServlet(capturedBrowsers, urlTranslator,
      urlRewriter, forwardingMapper));
    addServlet("/query/*", new BrowserQueryResponseServlet(capturedBrowsers,
      urlTranslator, forwardingMapper));
    addServlet("/fileSet", new FileSetServlet(capturedBrowsers, filesCache));
    addServlet("/cache", new FileCacheServlet());
    addServlet("/log", new BrowserLoggingServlet());
    addServlet("/test/*", new TestResourceServlet(filesCache));
    addServlet("/forward/*", new ForwardingServlet(forwardingMapper,
      "localhost", port));
  }

  private void addServlet(String url, Servlet servlet) {
    context.addServlet(new ServletHolder(servlet), url);
  }

  private void initJetty(int port) {
    SocketConnector connector = new SocketConnector();

    connector.setPort(port);
    server.addConnector(connector);
    context = new Context(server, "/", Context.SESSIONS);
    context.setMaxFormContentSize(Integer.MAX_VALUE);
  }

  public void start() {
    try {
      // TODO(corysmith): Move this to the constructor when we are injecting everything.
      timer = new Timer(true);
      timer.schedule(new BrowserReaper(capturedBrowsers), 3000, 3000);
      server.start();
      setChanged();
      notifyObservers(Event.STARTED);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      timer.cancel();
      server.stop();
      setChanged();
      notifyObservers(Event.STOPPED);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public enum Event {
    STARTED, STOPPED
  }
}
