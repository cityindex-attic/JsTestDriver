// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestParameters;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.annotations.ResponseWriter;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Used by the browser to report if it is still alive.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class HeartbeatPostHandler implements RequestHandler {

  private final CapturedBrowsers capturedBrowsers;
  private final Map<String, String[]> parameters;
  private final PrintWriter writer;

  @Inject
  public HeartbeatPostHandler(
      CapturedBrowsers capturedBrowsers,
      @RequestParameters Map<String, String[]> parameters,
      @ResponseWriter PrintWriter writer) {
    this.capturedBrowsers = capturedBrowsers;
    this.parameters = parameters;
    this.writer = writer;
  }

  public void handleIt() throws IOException {
    String[] ids = parameters.get("id");
    if (ids != null && ids[0] != null) {
      String id = ids[0];
      SlaveBrowser browser = capturedBrowsers.getBrowser(id);

      if (browser != null) {
        browser.heartBeat();
        if (browser.getCommandRunning() == null) {
          writer.write("Waiting...");
        } else {
          writer.write("Running: " + browser.getCommandRunning());
        }
      } else {
        writer.write("UNKNOWN");
      }
      writer.flush();
    }
  }
}