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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.BrowserCaptureEvent.Event;
import com.google.jstestdriver.servlet.fileset.FileSetRequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetServlet extends HttpServlet implements Observer {

  private static final long serialVersionUID = -5224290018208979639L;
  private static final int HEARTBEAT_TIMEOUT = 2000;

  private final Gson gson = new Gson();
  private final Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();

  private final CapturedBrowsers capturedBrowsers;

  // Shared with the TestResourceServlet
  private final FilesCache filesCache;

  private final List<FileSetRequestHandler<?>> handlers;

  public FileSetServlet(CapturedBrowsers capturedBrowsers, FilesCache filesCache, List<FileSetRequestHandler<?>> handlers) {
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
    this.capturedBrowsers.addObserver(this);
    this.handlers = handlers;
  }

  // TODO(corysmith): Extract the session logic to it's own servlet.
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    String session = req.getParameter("session");
    String sessionId = req.getParameter("sessionId");

    if (session == null && sessionId != null) {
      sessionHeartBeat(id, sessionId);
    } else {
      if (session.equals("start")) {
        startSession(id, resp.getWriter());
      } else if (session.equals("stop")) {
        stopSession(id, sessionId, resp.getWriter());
      }
    }
  }

  private void sessionHeartBeat(String id, String sessionId) {
    Lock lock = locks.get(id);

    if (lock.getSessionId().equals(sessionId)) {
      lock.setLastHeartBeat(new Date().getTime());
    } else {
      // who are you??
    }
  }

  public void stopSession(String id, String sessionId, PrintWriter writer) {
    Lock lock = locks.get(id);

    try {
      lock.unlock(sessionId);
    } finally {
      writer.flush();
    }
  }

  public void startSession(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Lock lock = locks.get(id);
    String sessionId = UUID.randomUUID().toString();

    if (lock.tryLock(sessionId)) {
      writer.write(sessionId);
    } else {
      // session is probably stalled
      if ((!browser.isCommandRunning() && browser.peekCommand() == null) ||
          ((new Date().getTime() - lock.getLastHeartBeat()) > HEARTBEAT_TIMEOUT)) {
        lock.forceUnlock();
        SlaveBrowser slaveBrowser = capturedBrowsers.getBrowser(id);

        slaveBrowser.clearCommandRunning();
        slaveBrowser.clearResponseQueue();
        filesCache.clear();
        writer.write(lock.tryLock(sessionId) ? sessionId : "FAILED");
      } else {
        writer.write("FAILED");
      }
    }
    writer.flush();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    final FileSetRequestHandler<?> handler = handlerFromAction(req);
    resp.getOutputStream().print(gson.toJson(handler.handle(browserFromId(req.getParameter("id")),
        fileInfosFromData(req.getParameter("data")))));
  }

  private Collection<FileInfo> fileInfosFromData(String data) {
    return gson.fromJson(data, new TypeToken<Collection<FileInfo>>() {}.getType());
  }

  private SlaveBrowser browserFromId(String id) {
    if (id == null) {
      return null;
    }
    return capturedBrowsers.getBrowser(id);
  }

  private FileSetRequestHandler<?> handlerFromAction(HttpServletRequest req) {
    final String action = req.getParameter("action");
    for (FileSetRequestHandler<?> handler : handlers) {
      if (handler.canHandle(action)) {
        return handler;
      }
    }
    throw new IllegalArgumentException("unknown action");
  }


  // TODO(corysmith): Remove this and add the Lock to the SlaveBrowser.
  public void update(Observable o, Object arg) {
    BrowserCaptureEvent captureEvent = (BrowserCaptureEvent) arg;
    if (captureEvent.event == Event.CONNECTED) {
      locks.put(captureEvent.getBrowser().getId(), new Lock());
    }
  }
}
