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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.Lock;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.annotations.ResponseWriter;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class FileSetGetHandler implements RequestHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(FileSetGetHandler.class);

  private static final int HEARTBEAT_TIMEOUT = 2000;

  private final HttpServletRequest request;
  private final PrintWriter writer;

  private final CapturedBrowsers capturedBrowsers;

  @Inject
  public FileSetGetHandler(
      HttpServletRequest request,
      @ResponseWriter PrintWriter writer,
      CapturedBrowsers capturedBrowsers) {
    this.request = request;
    this.writer = writer;
    this.capturedBrowsers = capturedBrowsers;
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
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    if (browser == null) {
      logger.error("heartbeat to a dead session");
      return;
    }
    Lock lock = browser.getLock();

    if (lock.getSessionId().equals(sessionId)) {
      lock.setLastHeartBeat(new Date().getTime());
    } else {
      // who are you??
      logger.error("unknown client with {}", id);
    }
  }

  public void stopSession(String id, String sessionId, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Lock lock = browser.getLock();

    try {
      lock.unlock(sessionId);
      browser.clearCommandRunning();
    } finally {
      writer.flush();
    }
  }

  public void startSession(String id, PrintWriter writer) {
    logger.debug("trying to start session for {}", id);
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Lock lock = browser.getLock();
    String sessionId = UUID.randomUUID().toString();

    if (lock.tryLock(sessionId)) {
      logger.debug("got session lock {} for {}", sessionId, id);
      writer.write(sessionId);
    } else {
      logger.debug("checking session status for {}", id);
      // session is probably stalled
      if ((!browser.isCommandRunning() && browser.peekCommand() == null) ||
          ((System.currentTimeMillis() - lock.getLastHeartBeat()) > HEARTBEAT_TIMEOUT)) {
        logger.debug("forcing unlock for {}", id);
        lock.forceUnlock();
        SlaveBrowser slaveBrowser = capturedBrowsers.getBrowser(id);

        slaveBrowser.clearCommandRunning();
        slaveBrowser.clearResponseQueue();
//        filesCache.clear();
        writer.write(lock.tryLock(sessionId) ? sessionId : "FAILED");
      } else {
        logger.debug("session unvailable for {}", id);
        writer.write("FAILED");
      }
    }
    writer.flush();
  }
}