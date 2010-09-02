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
package com.google.jstestdriver.server.handlers;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.Lock;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.annotations.ResponseWriter;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetGetHandler implements RequestHandler {

  private static final int HEARTBEAT_TIMEOUT = 2000;

  private final Gson gson = new Gson();
  private final HttpServletRequest request;
  private final PrintWriter writer;

  private final CapturedBrowsers capturedBrowsers;

  // Shared with the TestResourceServlet
  private final FilesCache filesCache;

  @Inject
  public FileSetGetHandler(
      HttpServletRequest request,
      @ResponseWriter PrintWriter writer,
      CapturedBrowsers capturedBrowsers,
      FilesCache filesCache) {
    this.request = request;
    this.writer = writer;
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
  }

  public void handleIt() throws IOException {
    String id = request.getParameter("id");
    String session = request.getParameter("session");
    String sessionId = request.getParameter("sessionId");

    if (session == null && sessionId != null) {
      sessionHeartBeat(id, sessionId);
    } else {
      if (session.equals("start")) {
        startSession(id, writer);
      } else if (session.equals("stop")) {
        stopSession(id, sessionId, writer);
      }
    }
  }

  private void sessionHeartBeat(String id, String sessionId) {
    Lock lock = capturedBrowsers.getBrowser(id).getLock();

    if (lock.getSessionId().equals(sessionId)) {
      lock.setLastHeartBeat(new Date().getTime());
    } else {
      // who are you??
    }
  }

  public void stopSession(String id, String sessionId, PrintWriter writer) {
    Lock lock = capturedBrowsers.getBrowser(id).getLock();

    try {
      lock.unlock(sessionId);
    } finally {
      writer.flush();
    }
  }

  public void startSession(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Lock lock = browser.getLock();
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
}