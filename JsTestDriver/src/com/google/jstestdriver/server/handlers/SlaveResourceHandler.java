// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.inject.Inject;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
class SlaveResourceHandler implements RequestHandler {

  private final static Pattern PATHWITHOUTID = Pattern.compile("/slave/.*?(/.*)$");

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final SlaveResourceService service;

  @Inject
  public SlaveResourceHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      SlaveResourceService service) {
    this.request = request;
    this.response = response;
    this.service = service;
  }

  public void handleIt() throws IOException {
    try {
      service.serve(stripId(request.getRequestURI()), response.getOutputStream());
    } catch (IllegalArgumentException e) {
      System.out.println(e);
    }
  }

  public static String stripId(String path) {
    Matcher match = PATHWITHOUTID.matcher(path);

    if (match.find()) {
      return match.group(1);
    }
    throw new IllegalArgumentException(path);
  }
}
