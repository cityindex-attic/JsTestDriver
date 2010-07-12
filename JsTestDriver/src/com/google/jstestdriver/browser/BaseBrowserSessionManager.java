// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.browser;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.HeartBeatManager;
import com.google.jstestdriver.Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the client portion of session for a given captured browser.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class BaseBrowserSessionManager implements BrowserSessionManager {
  private static final long WAIT_INTERVAL = 500L;
  private static final Logger logger = LoggerFactory.getLogger(BaseBrowserSessionManager.class);
  private final Server server;
  private final String baseUrl;
  private final HeartBeatManager heartBeatManager;

  @Inject
  public BaseBrowserSessionManager(Server server, @Named("server") String baseUrl,
      HeartBeatManager heartBeatManager) {
    this.server = server;
    this.baseUrl = baseUrl;
    this.heartBeatManager = heartBeatManager;
  }

  /**
   * {@inheritDoc}
   */
  public String startSession(String browserId) {
    String sessionId = server.startSession(baseUrl, browserId);

    if ("FAILED".equals(sessionId)) {
      while ("FAILED".equals(sessionId)) {
        try {
          Thread.sleep(WAIT_INTERVAL);
        } catch (InterruptedException e) {
          logger.error("Could not create session for browser: " + browserId);
          throw new RuntimeException("Can't start a session on the server!" + browserId);
        }
        sessionId = server.startSession(baseUrl, browserId);
      }
    }

    heartBeatManager.startTimer();
    heartBeatManager.startHeartBeat(baseUrl, browserId, sessionId);
    return sessionId;
  }

  /**
   * {@inheritDoc}
   */
  public void stopSession(String sessionId, String browserId) {
    heartBeatManager.cancelTimer();
    // TODO(corysmith): Is the browserId necessary?
    server.stopSession(baseUrl, browserId, sessionId);
  }
}