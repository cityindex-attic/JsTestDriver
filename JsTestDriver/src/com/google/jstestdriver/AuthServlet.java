// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.inject.Provider;
import com.google.jstestdriver.hooks.AuthStrategy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

/**
 * A servlet that provides the slaved browsers with cookies.
 *
 * It uses {@link AuthStrategy} to obtain cookies and creates a 'Set-Cookie'
 * header on the HTTP response for each cookie. Use this servlet to
 * pre-authenticate asynchronous tests that send network requests.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class AuthServlet extends HttpServlet {

  private final Set<AuthStrategy> authStrategies;

  public AuthServlet(Set<AuthStrategy> authStrategies) {
    this.authStrategies = authStrategies;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    for (AuthStrategy strategy : authStrategies) {
      for (Cookie cookie : strategy.getCookies()) {
        resp.addCookie(cookie);
      }
    }
  }
}
