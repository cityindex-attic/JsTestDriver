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

  private final ForwardingMapper forwardingMapper;

  private static final ThreadLocal<HttpServletRequest> threadLocal =
      new ThreadLocal<HttpServletRequest>();

  public ForwardingServlet(ForwardingMapper forwardingMapper) {
    this.forwardingMapper = forwardingMapper;
  }

  @Override
  public void service(ServletRequest req, ServletResponse resp) throws ServletException,
      IOException {
    try {
      HttpServletRequest request = (HttpServletRequest) req;

      threadLocal.set(request);
      super.service(req, resp);
    } finally {
      threadLocal.remove();
    }
  }

  @Override
  protected URL proxyHttpURL(String scheme, String serverName, int serverPort, String uri)
      throws MalformedURLException {
    HttpServletRequest request = threadLocal.get();

    return getForwardingUrl(scheme, serverName, serverPort, uri, request.getHeader("Referer"));
  }

  public URL getForwardingUrl(String scheme, String serverName, int serverPort, String uri,
      String referer) throws MalformedURLException {
    String forwardTo = forwardingMapper.getForwardTo(uri);

    if (forwardTo != null) {
      return new URL(forwardTo);
    }
    String incomingUrl = String.format("%s://%s:%d%s", scheme, serverName, serverPort, uri);
    URL refererUrl = new URL(referer);

    forwardTo = forwardingMapper.getForwardTo(refererUrl.getPath());
    if (forwardTo != null) {
      URL forwardUrl = new URL(forwardTo);
      StringBuilder sb = new StringBuilder();

      sb.append(forwardUrl.getProtocol());
      sb.append("://");
      sb.append(forwardUrl.getHost());
      int port = forwardUrl.getPort();

      if (port != -1) {
        sb.append(":");
        sb.append(Integer.toString(port));
      }
      String baseUrl = sb.toString();
      String finalUrl = baseUrl + uri;

      forwardingMapper.addForwardingMapping(incomingUrl, baseUrl);
      return new URL(finalUrl);
    } else {
      String baseUrl = forwardingMapper.getForwardTo(referer);

      forwardingMapper.addForwardingMapping(incomingUrl, baseUrl);
      return new URL(baseUrl + uri);
    }
  }
}
