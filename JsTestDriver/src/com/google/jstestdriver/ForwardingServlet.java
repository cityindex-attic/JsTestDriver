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

//  private static class HttpServletRequestWrapperCustomReferer extends HttpServletRequestWrapper {
//
//    private final String customReferer;
//    private final String host;
//
//    public HttpServletRequestWrapperCustomReferer(HttpServletRequest request,
//        String customReferer, String host) {
//      super(request);
//      this.customReferer = customReferer;
//      this.host = host;
//    }
//
//    @Override
//    public String getHeader(String name) {
//      if (name.equals("Referer") || name.equals("referer")) {
//        return customReferer;
//      }
//      if (name.equals("Host") || name.equals("host")) {
//        return host;
//      }
//      return super.getHeader(name);
//    }
//  }

  @Override
  public void service(ServletRequest req, ServletResponse resp) throws ServletException,
      IOException {
    try {
      HttpServletRequest request = (HttpServletRequest) req;
//      String uri=request.getRequestURI();
//
//      if (request.getQueryString()!=null) {
//        uri += "?"+request.getQueryString();
//      }
//      String referer = request.getHeader("Referer");
//
//      URL forwardingUrl =
//          getForwardingUrl(request.getScheme(), request.getServerName(), request.getServerPort(),
//              uri, request.getParameter("jstdid"), referer);
//      HttpServletRequest wrappedRequest =
//          new HttpServletRequestWrapperCustomReferer(request, transformReferer(forwardingUrl,
//              referer), forwardingUrl.getAuthority());

      threadLocal.set(request);
      super.service(req, resp);
    } finally {
      threadLocal.remove();
    }
  }

//  private String transformReferer(URL forwardingUrl, String referer) throws MalformedURLException {
//    if (referer == null) {
//      return null;
//    }
//    URL refererUrl = new URL(referer);
//    StringBuilder sb = new StringBuilder();
//
//    sb.append(forwardingUrl.getProtocol());
//    sb.append("://");
//    sb.append(forwardingUrl.getHost());
//    int port = refererUrl.getPort();
//
//    if (port != -1) {
//      sb.append(":");
//      sb.append(Integer.toString(port));
//    }
//    sb.append(refererUrl.getFile());
//
//    return sb.toString();
//  }

  @Override
  protected URL proxyHttpURL(String scheme, String serverName, int serverPort, String uri)
      throws MalformedURLException {
//    return threadLocal.get();
    HttpServletRequest request = threadLocal.get();

    return getForwardingUrl(scheme, serverName, serverPort, uri, request.getParameter("jstdid"),
        request.getHeader("Referer"));
  }

  public URL getForwardingUrl(String scheme, String serverName, int serverPort, String uri,
      String jstdid, String referer) throws MalformedURLException {
    String incomingUrl = String.format("%s://%s:%d%s", scheme, serverName, serverPort, uri);

    if (jstdid != null) {
      return new URL(forwardingMapper.getForwardTo(jstdid));
    }
    URL refererUrl = new URL(referer);
    URLQueryParser urlQueryParser = new URLQueryParser(refererUrl.getQuery());

    urlQueryParser.parse();
    jstdid = urlQueryParser.getParameter("jstdid");
    if (jstdid != null) {
      String forwardTo = forwardingMapper.getForwardTo(jstdid);
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
