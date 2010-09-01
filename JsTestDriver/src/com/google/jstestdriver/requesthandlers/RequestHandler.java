// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import java.io.IOException;

/**
 * An interface for any object that handles an HTTP request.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public interface RequestHandler {

  /**
   * Handle the HTTP request.
   *
   * @throws IOException
   */
  void handleIt() throws IOException;
}
