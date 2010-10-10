// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.server.handlers;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.jstestdriver.hooks.AuthStrategy;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * A servlet that provides the slaved browsers with cookies.
 *
 * It uses {@link AuthStrategy} to obtain cookies and creates a 'Set-Cookie'
 * header on the HTTP response for each cookie. Use this servlet to
 * pre-authenticate asynchronous tests that send network requests.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class AuthHandler implements RequestHandler {

  private final HttpServletResponse response;
  private final Set<AuthStrategy> authStrategies;

  @Inject
  public AuthHandler(HttpServletResponse response, Set<AuthStrategy> authStrategies) {
    this.response = response;
    this.authStrategies = authStrategies;
  }

  public void handleIt() throws IOException {
    for (AuthStrategy strategy : authStrategies) {
      for (Cookie cookie : strategy.getCookies()) {
        response.addCookie(cookie);
      }
    }
  }
}