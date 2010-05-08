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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Need to make it nicer, maybe use a template system...
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class HomeServlet extends HttpServlet {

  private static final long serialVersionUID = -5726088138365911141L;

  private final CapturedBrowsers capturedBrowsers;

  public HomeServlet(CapturedBrowsers capturedBrowsers) {
    this.capturedBrowsers = capturedBrowsers;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    PrintWriter writer = resp.getWriter();

    if ("/".equals(req.getRequestURI())) {
      resp.setContentType("text/html");
      service(writer);
    }
  }

  public void service(PrintWriter writer) {
    writer.write("<html><head><title>JsTestDriver</title></head><body>");
    writer.write("<a href=\"/capture\">Capture This Browser</a><br/>");
    writer.write("<a href=\"/capture?strict\">Capture This Browser in strict mode</a><br/>");
    writer.write("<p><strong>Captured Browsers:</strong></p>");
    for (SlaveBrowser browser : capturedBrowsers.getSlaveBrowsers()) {
      writer.write("<p>");
      BrowserInfo info = browser.getBrowserInfo();
      writer.write("Id: " + info.getId() + "<br/>");
      writer.write("Name: " + info.getName() + "<br/>");
      writer.write("Version: " + info.getVersion() + "<br/>");
      writer.write("Operating System: " + info.getOs() + "<br/>");
      if (browser.getCommandRunning() != null) {
        writer.write("Currently running " + browser.getCommandRunning() + "<br/>");
      } else {
        writer.write("Currently waiting...<br/>");
      }
      writer.write("<p>");
      writer.write("Loaded files:<ul>");
      for (FileInfo fileInfo : browser.getFileSet()) {
        writer.write("<li>");
        writer.write(fileInfo.getFileName());
        writer.write("</li>");
      }
      writer.write("</ul>");
    }
    writer.write("</p></body></html>");
    writer.flush();
  }
}
