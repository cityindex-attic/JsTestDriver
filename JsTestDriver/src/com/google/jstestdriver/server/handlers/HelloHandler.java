// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.inject.Inject;
import com.google.jstestdriver.annotations.ResponseWriter;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class HelloHandler implements RequestHandler {

  private final PrintWriter writer;

  @Inject
  public HelloHandler(@ResponseWriter PrintWriter writer) {
    this.writer = writer;
  }

  public void handleIt() throws IOException {
    writer.write("hello");
    writer.flush();
  }
}
