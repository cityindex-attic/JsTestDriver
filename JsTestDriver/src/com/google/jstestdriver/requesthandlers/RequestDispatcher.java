// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * A @RequestScoped object that dispatches the request to the appropriate
 * {@link RequestHandler} whose {@link RequestMatcher} matches first.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class RequestDispatcher {

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final List<RequestMatcher> matchers;
  private final Map<RequestMatcher, Provider<RequestHandler>> handlerProviders;
  private final UnsupportedMethodErrorSender errorSender;

  @Inject
  public RequestDispatcher(
      HttpServletRequest request,
      HttpServletResponse response,
      List<RequestMatcher> matchers,
      Map<RequestMatcher, Provider<RequestHandler>> handlerProviders,
      UnsupportedMethodErrorSender errorSender) {
    this.request = request;
    this.response = response;
    this.matchers = matchers;
    this.handlerProviders = handlerProviders;
    this.errorSender = errorSender;
  }

  /**
   * Dispatches the request to the {@link RequestHandler} associated with the
   * first matching {@link RequestMatcher} based on the request's HTTP method
   * and URI.
   * 
   * @throws IOException
   */
  public void dispatch() throws IOException {
    try {
      HttpMethod method = HttpMethod.valueOf(request.getMethod());
      String uri = request.getRequestURI();
      boolean pathMatched = false;
      for (RequestMatcher matcher : matchers) {
        if (matcher.uriMatches(uri)) {
          pathMatched = true;
          if (matcher.methodMatches(method)) {
            handlerProviders.get(matcher).get().handleIt();
            return;
          }
        }
      }
      if (pathMatched) {
        errorSender.methodNotAllowed();
      } else {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found.");
      }
    } catch (IllegalArgumentException e) {
      errorSender.methodNotAllowed();
    }
  }
}
