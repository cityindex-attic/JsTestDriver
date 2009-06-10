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

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ServerStartupAction implements ObservableAction {

  private final int port;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache preloadedFilesCache;
  private JsTestDriverServer server;
  private List<Observer> observerList = new LinkedList<Observer>();

  public ServerStartupAction(int port, CapturedBrowsers capturedBrowsers,
      FilesCache preloadedFilesCache) {
    this.port = port;
    this.capturedBrowsers = capturedBrowsers;
    this.preloadedFilesCache = preloadedFilesCache;
  }

  public JsTestDriverServer getServer() {
    return server;
  }

  public void run() {
    server = new JsTestDriverServer(port, capturedBrowsers, preloadedFilesCache);
    for (Observer o : observerList) {
      server.addObserver(o);
    }
    server.start();
  }

  public void addObservers(List<Observer> observers) {
    observerList.addAll(observers);
  }
}
