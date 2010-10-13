// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.inject.Inject;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.annotations.ResponseWriter;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * Need to make it nicer, maybe use a template system...
 *
 * TODO(rdionne): Pull in Soy.  Will be non-trivial due to common deps
 * with potentially different versions.
 *
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class HomeHandler implements RequestHandler {

  private final CapturedBrowsers capturedBrowsers;
  private final HttpServletResponse response;
  private final PrintWriter writer;

  @Inject
  public HomeHandler(
      CapturedBrowsers capturedBrowsers,
      HttpServletResponse response,
      @ResponseWriter PrintWriter writer) {
    this.capturedBrowsers = capturedBrowsers;
    this.response = response;
    this.writer = writer;
  }

  public void handleIt() throws IOException {writer.write("<html><head><title>JsTestDriver</title></head><body>");
    response.setContentType("text/html");
    writer.write("<a href=\"/capture\">Capture This Browser</a><br/>");
    writer.write("<a href=\"/capture?strict\">Capture This Browser in strict mode</a><br/>");
    writer.write("<p><strong>Captured Browsers: (");
    writer.write(String.valueOf(capturedBrowsers.getSlaveBrowsers().size()));
    writer.write(")</strong></p>");
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
      writer.write("</p>");
      writer.flush();
    }
    writer.write("</body></html>");
    writer.flush();
  }
}
