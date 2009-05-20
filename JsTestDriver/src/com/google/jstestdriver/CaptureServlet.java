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

import static org.mortbay.resource.Resource.newClassPathResource;

import org.mortbay.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureServlet extends HttpServlet {

  private final Logger logger = LoggerFactory.getLogger(CaptureServlet.class.getName());

  private final String resourceLocation;
  private final CapturedBrowsers browsers;

  public CaptureServlet(String baseResourceLocation, CapturedBrowsers browsers) {
    this.resourceLocation = baseResourceLocation;
    this.browsers = browsers;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (req.getParameterMap().isEmpty()) {
      serviceStaticPage(resp.getOutputStream());
    } else {
      String userAgent = req.getHeader("User-Agent");
      resp.sendRedirect(serviceRemoteConsoleRunner(userAgent, req.getParameter("version"),
          req.getParameter("os")));
    }
  }

  public String serviceRemoteConsoleRunner(String name, String version, String os) {
    String id = browsers.getUniqueId();
    BrowserInfo browserInfo = new BrowserInfo();

    browserInfo.setId(Integer.valueOf(id));
    browserInfo.setName(name);
    browserInfo.setVersion(version);
    browserInfo.setOs(os);
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), id, browserInfo);

    browsers.addSlave(slave);
    logger.info("Browser Captured: {} version {} ({})", new Object[] {name, version, id});
    return String.format("/slave/%s/RemoteConsoleRunner.html", id);
  }

  public void serviceStaticPage(OutputStream out) throws IOException {
    Resource resource = newClassPathResource(resourceLocation);

    if (resource == null) {
      throw new IllegalArgumentException("Resource doesn't exist");
    }
    resource.writeTo(out, 0, resource.length());
  }
}
