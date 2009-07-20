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

import com.google.gson.Gson;
import com.google.jstestdriver.SlaveBrowser.CommandResponse;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandServlet extends HttpServlet {

  private static final long serialVersionUID = 7210927357890630427L;

  private final CapturedBrowsers capturedBrowsers;

  public CommandServlet(CapturedBrowsers browsers) {
    this.capturedBrowsers = browsers;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (req.getParameter("listBrowsers") != null) {
      resp.getWriter().write(listBrowsers());
    } else {
      streamResponse(req.getParameter("id"), resp.getWriter());
    }
    resp.getWriter().flush();
  }

  public void streamResponse(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    CommandResponse cmdResponse = browser.getResponse();
    String response = "{ 'last':" + cmdResponse.isLast() + ", 'response':" +
        cmdResponse.getResponse() + " }";

    writer.write(response);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    service(req.getParameter("id"), req.getParameter("data"), resp.getWriter());
  }

  public String listBrowsers() {
    return new Gson().toJson(capturedBrowsers.getBrowsers());
  }

  public void service(String id, String data, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    browser.createCommand(data);
  }
}
