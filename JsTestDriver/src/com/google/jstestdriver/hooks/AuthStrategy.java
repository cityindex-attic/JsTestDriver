// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.hooks;

import java.util.List;

import javax.servlet.http.Cookie;

/**
 * A strategy for supplying an {@link com.google.jstestdriver.server.handlers.AuthHandler} with {@link Cookie}s.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public interface AuthStrategy {

  /**
   * @return a {@link List} of {@link Cookie}s to pre-authenticate asynchronous
   * tests that send network requests
   *
   * TODO(rdionne): Add an AuthInput class that {@link AuthStrategy} can accept
   * from {@link com.google.jstestdriver.server.handlers.AuthHandler} for providing nuanced {@link Cookie}s.
   */
  List<Cookie> getCookies();
}
