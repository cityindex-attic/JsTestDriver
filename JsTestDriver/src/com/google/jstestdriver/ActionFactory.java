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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import com.google.inject.Inject;

/**
 * Produces instances of Actions, so they can have observers, and other stuff.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */

public class ActionFactory {

  Map<Class<?>, List<Observer>> observers = new HashMap<Class<?>, List<Observer>>();
  private final CommandTaskFactory commandTaskFactory;

  @Inject
  public ActionFactory(CommandTaskFactory commandTaskFactory) {
    this.commandTaskFactory = commandTaskFactory;
  }

  public JsTestDriverClient getJsTestDriverClient(Set<FileInfo> files, String serverAddress) {
    return new JsTestDriverClientImpl(commandTaskFactory, files, serverAddress, new HttpServer());
  }

  public ServerStartupAction getServerStartupAction(Integer port,
      CapturedBrowsers capturedBrowsers, FilesCache preloadedFilesCache) {
    ServerStartupAction serverStartupAction = new ServerStartupAction(port, capturedBrowsers,
        preloadedFilesCache);
    if (observers.containsKey(CapturedBrowsers.class)) {
      for (Observer o : observers.get(CapturedBrowsers.class)) {
        capturedBrowsers.addObserver(o);
      }
    }
    if (observers.containsKey(ServerStartupAction.class)) {
      serverStartupAction.addObservers(observers.get(ServerStartupAction.class));
    }
    return serverStartupAction;
  }

  public void registerListener(Class<?> clazz, Observer observer) {
    if (!observers.containsKey(clazz)) {
      observers.put(clazz, new LinkedList<Observer>());
    }
    observers.get(clazz).add(observer);
  }

  public static class ActionFactoryFileFilter implements JsTestDriverFileFilter {
    public String filterFile(String content, boolean reload) {
      return content;
    }

    public List<String> resolveFilesDeps(String file) {
      return Collections.singletonList(file);
    }
  }
}
