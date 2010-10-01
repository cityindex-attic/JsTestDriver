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

import org.mortbay.servlet.ProxyServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ForwardingServlet extends ProxyServlet.Transparent {

  private static final Logger logger = LoggerFactory.getLogger(ForwardingServlet.class);

  private static final int SKIP_FORWARD_INDEX = "/forward/".length();

  public ForwardingServlet(String defaultServer, int port) {
    super("forward", defaultServer, port);
  }

  @SuppressWarnings("unused")
  @Override
  public void service(ServletRequest req, ServletResponse resp) throws ServletException,
      IOException {
    HttpServletRequest request = (HttpServletRequest) req;
    super.service(req, resp);
  }

  @Override
  protected URL proxyHttpURL(String scheme, String serverName, int serverPort, String uri)
      throws MalformedURLException {
    return getForwardingUrl(scheme, serverName, serverPort, uri);
  }

  public URL getForwardingUrl(String scheme, String serverName, int serverPort, String uri)
      throws MalformedURLException {
    // presumes that the first part of the string is the server to forward to.
    final String server = uri.substring(SKIP_FORWARD_INDEX);
    String forwardUri = String.format("%s://%s", scheme, server);
    logger.debug(forwardUri);
    return new URL(forwardUri);
  }
}
