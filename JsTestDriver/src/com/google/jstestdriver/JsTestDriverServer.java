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

import java.util.Observable;
import java.util.Set;
import java.util.Timer;

import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.jstestdriver.browser.BrowserReaper;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.hooks.ProxyDestination;
import com.google.jstestdriver.server.JettyModule;
import com.google.jstestdriver.server.handlers.JstdHandlersModule;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverServer extends Observable {
  private static final Logger logger =
      LoggerFactory.getLogger(JsTestDriverServer.class);

  private Server server;

  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache filesCache;
  private final long browserTimeout;
  private final ProxyDestination destination;
  private final Set<AuthStrategy> authStrategies;

  private Timer timer;

  public JsTestDriverServer(int port,
                            CapturedBrowsers capturedBrowsers,
                            FilesCache preloadedFilesCache,
                            long browserTimeout,
                            ProxyDestination destination,
                            Set<AuthStrategy> authStrategies) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = preloadedFilesCache;
    this.browserTimeout = browserTimeout;
    this.destination = destination;
    this.authStrategies = authStrategies;
    initServer();
  }

  private void initServer() {
    // TODO(corysmith): replace this with Guice injection
    server = Guice.createInjector(
        new JettyModule(port),
        new JstdHandlersModule(
            capturedBrowsers, filesCache, browserTimeout, authStrategies, destination))
                .getInstance(Server.class);

    // TODO(rdionne): Remove this sub-injector and all 'new' statements in this file.
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
