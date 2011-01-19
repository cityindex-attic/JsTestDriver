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

import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.hooks.ProxyDestination;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.model.NullPathPrefix;
import com.google.jstestdriver.model.RunData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observer;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ServerStartupAction implements ObservableAction {
  private static final Logger logger = LoggerFactory.getLogger(ServerStartupAction.class);
  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache preloadedFilesCache;
  private JsTestDriverServerImpl server;
  private List<Observer> observerList = new LinkedList<Observer>();
  private final long browserTimeout;
  private final ProxyDestination destination;
  private final Set<AuthStrategy> authStrategies;
  private final boolean preloadFiles;
  private final FileLoader fileLoader;
  private final HandlerPathPrefix handlerPrefix;

  /**
   * Exists for backwards compatibility.
   * @deprecated Use other constructor.
   */
  @Deprecated
  public ServerStartupAction(int port, CapturedBrowsers capturedBrowsers,
      FilesCache preloadedFilesCache, URLTranslator urlTranslator, URLRewriter urlRewriter) {
    this(port, capturedBrowsers, preloadedFilesCache, SlaveBrowser.TIMEOUT, null, Collections
        .<AuthStrategy>emptySet(), false, null, new NullPathPrefix());
  }

  public ServerStartupAction(
      int port,
      CapturedBrowsers capturedBrowsers,
      FilesCache preloadedFilesCache,
      long browserTimeout,
      ProxyDestination destination,
      Set<AuthStrategy> authStrategies,
      boolean preloadFiles,
      FileLoader fileLoader,
      HandlerPathPrefix handlerPrefix) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.preloadedFilesCache = preloadedFilesCache;
    this.browserTimeout = browserTimeout;
    this.destination = destination;
    this.authStrategies = authStrategies;
    this.preloadFiles = preloadFiles;
    this.fileLoader = fileLoader;
    this.handlerPrefix = handlerPrefix;
  }

  public JsTestDriverServer getServer() {
    return server;
  }

  public RunData run(RunData runData) {
    logger.info("Starting server on {}", port);

    if (preloadFiles) {
      logger.debug("Preloading files...", port);
      for (FileInfo fileInfo : fileLoader.loadFiles(runData.getFileSet(), false)) {
        preloadedFilesCache.addFile(fileInfo);
      }
    }

    server = new JsTestDriverServerImpl(
        port,
        capturedBrowsers,
        preloadedFilesCache,
        browserTimeout,
        destination,
        authStrategies,
        handlerPrefix);

    for (Observer o : observerList) {
      server.addObserver(o);
    }
    try {
      server.start();
      for (int i = 0; i < 3; i++) {
        if (server.isHealthy()) {
          return runData;
        }
        Thread.sleep(500); // wait for the server to come up.
      }
      throw new RuntimeException("Server never healthy on " + port);
    } catch (Exception e) {
      throw new RuntimeException("Error starting the server on " + port, e);
    }
  }

  public void addObservers(List<Observer> observers) {
    observerList.addAll(observers);
  }
}
