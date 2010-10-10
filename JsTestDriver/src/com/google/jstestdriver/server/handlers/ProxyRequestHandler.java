// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.servlet.ProxyServlet;

import com.google.inject.Inject;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * A {@link RequestHandler} that forwards all "/jstd/proxy/*" requests to
 * a server-under-test after first stripping the "/jstd/proxy/" prefix from
 * the request's URI.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class ProxyRequestHandler implements RequestHandler {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final ProxyServlet.Transparent proxy;

  @Inject
  public ProxyRequestHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      ProxyServlet.Transparent proxy) {
    this.request = request;
    this.response = response;
    this.proxy = proxy;
  }

  public void handleIt() throws IOException {
    try {
      proxy.service(request, response);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
  }
}
