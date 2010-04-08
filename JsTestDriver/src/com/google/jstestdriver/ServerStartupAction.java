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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ServerStartupAction implements ObservableAction {
  private static final Logger logger = LoggerFactory.getLogger(ServerStartupAction.class);
  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache preloadedFilesCache;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private JsTestDriverServer server;
  private List<Observer> observerList = new LinkedList<Observer>();
  private final long browserTimeout;
  
  /**
   * @deprecated In favor of using the constructor that defines browser timeout.
   */
  @Deprecated
  public ServerStartupAction(int port,
                             CapturedBrowsers capturedBrowsers,
                             FilesCache preloadedFilesCache,
                             URLTranslator urlTranslator,
                             URLRewriter urlRewriter) {
    this(port,
         capturedBrowsers,
         preloadedFilesCache,
         urlTranslator,
         urlRewriter,
         SlaveBrowser.TIMEOUT);
    
  }

  public ServerStartupAction(int port,
                             CapturedBrowsers capturedBrowsers,
                             FilesCache preloadedFilesCache,
                             URLTranslator urlTranslator,
                             URLRewriter urlRewriter,
                             long browserTimeout) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.preloadedFilesCache = preloadedFilesCache;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.browserTimeout = browserTimeout;
  }

  public JsTestDriverServer getServer() {
    return server;
  }

  public void run() {
    logger.info("Starting server...");
    server =
        new JsTestDriverServer(port, capturedBrowsers, preloadedFilesCache, urlTranslator,
            urlRewriter, browserTimeout);
    for (Observer o : observerList) {
      server.addObserver(o);
    }
    try {
      server.start();
    } catch (Exception e) {
      throw new RuntimeException("Error starting the server on " + port, e);
    }
  }

  public void addObservers(List<Observer> observers) {
    observerList.addAll(observers);
  }
}
