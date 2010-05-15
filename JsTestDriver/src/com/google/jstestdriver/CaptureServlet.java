/*
 * Copyright 2008 Google Inc.
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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * "Captures" a browser by redirecting it to RemoteConsoleRunner url, and adds
 * it to the CapturedBrowsers collection.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureServlet extends HttpServlet {

  private static final long serialVersionUID = -6565114508964010323L;

  public static final String STRICT = "strict";
  public static final String QUIRKS = "quirks";

  private final BrowserHunter browserHunter;

  public CaptureServlet(BrowserHunter browserHunter) {
    this.browserHunter = browserHunter;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String mode = req.getParameter(STRICT) != null ? STRICT : QUIRKS;
    String id = req.getParameter("id");

    resp.sendRedirect(service(req.getHeader("User-Agent"), mode, id));
  }

  public String service(String userAgent, String mode, String id) {
    UserAgentParser parser = new UserAgentParser();

    parser.parse(userAgent);
    SlaveBrowser slaveBrowser =
      browserHunter.captureBrowser(id, parser.getName(), parser.getVersion(), parser.getOs());

    return browserHunter.getCaptureUrl(slaveBrowser.getId(), mode);
  }
}
