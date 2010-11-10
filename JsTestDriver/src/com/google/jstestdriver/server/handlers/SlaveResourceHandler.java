// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.server.handlers.pages.Page;
import com.google.jstestdriver.server.handlers.pages.PageType;
import com.google.jstestdriver.server.handlers.pages.SlavePageRequest;
import com.google.jstestdriver.util.HtmlWriter;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
class SlaveResourceHandler implements RequestHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(SlaveResourceHandler.class);

  private final Map<PageType, Page> pages;

  private final SlavePageRequest request;

  private final HandlerPathPrefix prefix;

  private final HttpServletResponse response;


  @Inject
  public SlaveResourceHandler(
      HttpServletResponse response,
      Map<PageType, Page> pages,
      SlavePageRequest request,
      HandlerPathPrefix prefix) {
    this.response = response;
    this.pages = pages;
    this.request = request;
    this.prefix = prefix;
  }

  public void handleIt() throws IOException {
    logger.debug("Handling " + request);
    final HtmlWriter writer = new HtmlWriter(response.getWriter(), prefix);
    request.writeDTD(writer);
    pages.get(request.getPageType()).render(writer, request);
  }
}
