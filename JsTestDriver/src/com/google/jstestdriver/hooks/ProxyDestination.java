// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.hooks;

/**
 * Provides a destination address in the form of a URL {@link String}, e.g.
 * "http://localhost:55555", for the JsTestDriver server to forward unhandled
 * requests.  Useful for system tests that interact with a server environment.
 *
 * @author rdionne@google.com (Robert Dionne)
 * @deprecated Please update your jsTestDriver.conf with a proxy spec instead.
 */
public interface ProxyDestination {

  /**
   * @return the address of the server-under-test
   */
  String getDestinationAddress();
}
