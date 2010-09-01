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

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.jstestdriver.browser.BrowserReaper;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.hooks.ProxyDestination;
import com.google.jstestdriver.server.JettyModule;
import com.google.jstestdriver.server.handlers.JstdHandlersModule;
import com.google.jstestdriver.servlet.fileset.BrowserFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileUpload;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.servlet.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Set;
import java.util.Timer;

import javax.servlet.Servlet;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServer extends Observable {
  private static final Logger logger =
      LoggerFactory.getLogger(JsTestDriverServer.class);

  private final Server server = new Server();
  private Servlet handlerServlet;

  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private Context context;

  private final long browserTimeout;
  private final ProxyDestination destination;
  private final Set<AuthStrategy> authStrategies;

  private Timer timer;

  public JsTestDriverServer(int port,
                            CapturedBrowsers capturedBrowsers,
                            FilesCache preloadedFilesCache,
                            URLTranslator urlTranslator,
                            URLRewriter urlRewriter,
                            long browserTimeout,
                            ProxyDestination destination,
                            Set<AuthStrategy> authStrategies) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = preloadedFilesCache;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.browserTimeout = browserTimeout;
    this.destination = destination;
    this.authStrategies = authStrategies;
    initJetty(this.port);
    initServlets();
  }

  private void initServlets() {
    ForwardingMapper forwardingMapper = new ForwardingMapper();
    // TODO(corysmith): replace this with Guice injection
    
    handlerServlet = Guice.createInjector(
        new JettyModule(port),
        new JstdHandlersModule(
            capturedBrowsers, filesCache, forwardingMapper,
            browserTimeout, urlTranslator, urlRewriter, authStrategies))
                .getInstance(Servlet.class);

    addServlet("/", handlerServlet);
    addServlet("/cache", handlerServlet);
    addServlet("/capture/*", handlerServlet);
    addServlet("/cmd", handlerServlet);
    addServlet("/heartbeat", handlerServlet);
    addServlet("/hello", handlerServlet);
    addServlet("/jstd/auth", handlerServlet);
    addServlet("/log", handlerServlet);
    addServlet("/query/*", handlerServlet);
    addServlet("/runner/*", handlerServlet);
    addServlet("/slave/*", handlerServlet);

    // TODO(rdionne): Once all the servlets below are replaced with handlerServlet above,
    // remove this sub-injector and all 'new' statements in this file.
    //
    // Note: Fix HttpServletRequest#getPathInfo() provided by RequestHandlerServlet.
    
    final FileSetCacheStrategy strategy = new FileSetCacheStrategy();
    addServlet("/fileSet",
        new FileSetServlet(
            capturedBrowsers,
            filesCache,
            ImmutableList.of(
                new BrowserFileCheck(strategy),
                new ServerFileCheck(filesCache, strategy),
                new ServerFileUpload(filesCache))));
    addServlet("/test/*", new TestResourceServlet(filesCache));
    addServlet("/forward/*", new ForwardingServlet(forwardingMapper,
      "localhost", port));

    if (destination != null) {
      ServletHolder proxyHolder =
          new ServletHolder(new ProxyServlet.Transparent());
      proxyHolder.setInitParameter(
          "ProxyTo", destination.getDestinationAddress());
      proxyHolder.setInitParameter("Prefix", ProxyHandler.PROXY_PREFIX);
      addServlet(ProxyHandler.PROXY_PREFIX + "/*", proxyHolder);
    }
  }

  private void addServlet(String url, Servlet servlet) {
    context.addServlet(new ServletHolder(servlet), url);
  }

  private void addServlet(String url, ServletHolder servletHolder) {
    context.addServlet(servletHolder, url);
  }

  private void initJetty(int port) {
    SocketConnector connector = new SocketConnector();
    connector.setPort(port);
    server.addConnector(connector);

    ProxyHandler proxyHandler = new ProxyHandler();

    context = new Context(proxyHandler, "/", Context.SESSIONS);
    context.setMaxFormContentSize(Integer.MAX_VALUE);

    server.setHandler(proxyHandler);
  }

  public void start() {
    try {
      // TODO(corysmith): Move this to the constructor when we are injecting everything.
      timer = new Timer(true);
      timer.schedule(new BrowserReaper(capturedBrowsers), 3000, 3000);
      server.start();
      setChanged();
      notifyObservers(Event.STARTED);
      logger.debug("Starting the server.");
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
      logger.debug("Stopped the server.");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public enum Event {
    STARTED, STOPPED
  }
}
