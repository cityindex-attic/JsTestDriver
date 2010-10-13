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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class HeartbeatServlet extends HttpServlet {

  private static final long serialVersionUID = 5484417807218095115L;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatServlet.class);

  private final CapturedBrowsers capturedBrowsers;
  
  private final Gson gson = new Gson();

  public HeartbeatServlet(CapturedBrowsers capturedBrowsers) {
    this.capturedBrowsers = capturedBrowsers;
  }

  /**
   * Used by the client to know if the browser is alive.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter writer = resp.getWriter();
    String id = req.getParameter("id");
    if (id != null) { 
      SlaveBrowser browser = capturedBrowsers.getBrowser(id);
      if (browser != null) {
        LOGGER.debug("heartbeat " + browser);
        if (!browser.isAlive()) {
          capturedBrowsers.removeSlave(id);
          writer.write("DEAD:" + gson.toJson(browser.getBrowserInfo()));
        } else {
          writer.write("OK");
        }
      } else {
        LOGGER.debug("heartbeat " + id + "with no browser.");
        writer.write("DEAD: can't find browser.");
      }
    } else {
      LOGGER.debug("no heartbeat, no browser.");
      writer.write("DEAD");
    }
    writer.flush();
  }

  /**
   * Used by the browser to report if it is still alive.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    PrintWriter writer = resp.getWriter();

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
