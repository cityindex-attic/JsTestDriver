// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.inject.Inject;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.mortbay.resource.Resource;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * A {@link RequestHandler} that provides browsers with the JSTD favicon.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class FaviconHandler implements RequestHandler {

  private static final String IMAGE_PNG = "image/png";
  private static final String JS_TEST_DRIVER_PNG = "/com/google/jstestdriver/JsTestDriver.png";
  
  private final HttpServletResponse response;
  private Resource favicon;

  @Inject
  public FaviconHandler(HttpServletResponse response) {
    this.response = response;
  }

  public void handleIt() throws IOException {
    response.setContentType(IMAGE_PNG);
    if (favicon == null) {
      favicon = Resource.newClassPathResource(JS_TEST_DRIVER_PNG);
    }
    favicon.writeTo(response.getOutputStream(), 0, favicon.length());
  }
}
