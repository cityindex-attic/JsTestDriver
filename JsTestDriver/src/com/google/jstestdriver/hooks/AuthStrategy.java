// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.hooks;

import javax.servlet.http.Cookie;

import java.util.List;

/**
 * A strategy for supplying an {@link AuthServlet} with {@link Cookie}s.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public interface AuthStrategy {

  /**
   * @return a {@link List} of {@link Cookie}s to pre-authenticate asynchronous
   * tests that send network requests
   *
   * TODO(rdionne): Add an AuthInput class that {@link AuthStrategy} can accept
   * from {@link AuthServlet} for providing nuanced {@link Cookie}s.
   */
  List<Cookie> getCookies();
}
