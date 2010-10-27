// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.BrowserPanic;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.StreamMessage;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class CommandGetHandler implements RequestHandler {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final Gson gson;
  private final CapturedBrowsers capturedBrowsers;

  @Inject
  public CommandGetHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      Gson gson,
      CapturedBrowsers capturedBrowsers) {
    this.request = request;
    this.response = response;
    this.gson = gson;
    this.capturedBrowsers = capturedBrowsers;
  }

  public void handleIt() throws IOException {
    if (request.getParameter("listBrowsers") != null) {
      response.getWriter().write(listBrowsers());
    } else if (request.getParameter("nextBrowserId") != null) {
      response.getWriter().write(capturedBrowsers.getUniqueId());
    } else {
      streamResponse(request.getParameter("id"), response.getWriter());
    }
    response.getWriter().flush();
  }

  public String listBrowsers() {
    return gson.toJson(capturedBrowsers.getBrowsers());
  }

  private void streamResponse(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    writer.write(gson.toJson(substituteBrowserInfo(getResponse(browser))));
  }

  private StreamMessage getResponse(SlaveBrowser browser) {
    StreamMessage cmdResponse = null;

    while (cmdResponse == null) {
      if (!browser.isAlive()) {
        SlaveBrowser deadBrowser = capturedBrowsers.getBrowser(browser.getId());
        capturedBrowsers.removeSlave(browser.getId());
        Response response = new Response();

        BrowserInfo browserInfo = deadBrowser.getBrowserInfo();
        response.setBrowser(browserInfo);
        response.setResponse(
            gson.toJson(
                new BrowserPanic(browserInfo,
                    String.format("Browser unresponsive since %s during %s",
                        browser.getLastHeartBeat(),
                        browser.getCommandRunning()))));
        response.setType(BrowserPanic.TYPE_NAME);
        return new StreamMessage(true, response);
      }
      cmdResponse = browser.getResponse();
    }
    return cmdResponse;
  }

  private StreamMessage substituteBrowserInfo(StreamMessage cmdResponse) {
    Response response = cmdResponse.getResponse();

      SlaveBrowser slaveBrowser =
          capturedBrowsers.getBrowser(response.getBrowser().getId().toString());
    if (slaveBrowser != null) {
      response.setBrowser(slaveBrowser.getBrowserInfo());
    } else {
      BrowserInfo nullBrowserInfo = new BrowserInfo();
      nullBrowserInfo.setId(response.getBrowser().getId());
      nullBrowserInfo.setName("unknown browser");
      nullBrowserInfo.setVersion("unknown version");
      nullBrowserInfo.setOs("unknown os");
      response.setBrowser(nullBrowserInfo);
    }
    return new StreamMessage(cmdResponse.isLast(), response);
  }
}
