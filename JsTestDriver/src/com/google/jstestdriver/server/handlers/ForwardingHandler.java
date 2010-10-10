// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.jstestdriver.ForwardingMapper;
import com.google.jstestdriver.ForwardingServlet;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * A {@link RequestHandler} that forwards requests to "/forward/*" according
 * to the {@link ForwardingMapper} mappings between referrer and destination.
 * Relies upon {@link ForwardingServlet} for this logic.
 *
 * TODO(rdionne): Learn what uses the behavior of the ForwardingServlet and
 * deduplicate with the ProxyRequestHandler.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class ForwardingHandler implements RequestHandler {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final ForwardingServlet servlet;

  @Inject
  public ForwardingHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      ForwardingServlet servlet) {
    this.request = request;
    this.response = response;
    this.servlet = servlet;
  }

  public void handleIt() throws IOException {
    try {
      servlet.service(request, response);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
  }
}
