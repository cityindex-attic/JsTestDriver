// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.jstestdriver.annotations.ResponseWriter;
import com.google.jstestdriver.requesthandlers.ProxyConfiguration;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyConfigurationHandler implements RequestHandler {

  private static final Logger logger =
      LoggerFactory.getLogger(ProxyConfigurationHandler.class);

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final PrintWriter responseWriter;
  private final JsonParser parser;
  private final ProxyConfiguration proxyConfiguration;

  @Inject
  public ProxyConfigurationHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      @ResponseWriter PrintWriter responseWriter,
      JsonParser parser,
      ProxyConfiguration proxyConfiguration) {
    this.request = request;
    this.response = response;
    this.responseWriter = responseWriter;
    this.parser = parser;
    this.proxyConfiguration = proxyConfiguration;
  }

  public void handleIt() throws IOException {
    try {
      proxyConfiguration.updateConfiguration(parser.parse(request.getReader()).getAsJsonArray());
    } catch (ServletException e) {
      logger.error("Error configuring proxy {}", e);
    }
  }
}
