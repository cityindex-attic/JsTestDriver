// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A @RequestScoped object that dispatches the request to the appropriate
 * {@link RequestHandler} whose {@link RequestMatcher} matches first.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class RequestDispatcher {
  private static final Logger logger =
      LoggerFactory.getLogger(RequestDispatcher.class);

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final List<RequestMatcher> matchers;
  private final Map<RequestMatcher, Provider<RequestHandler>> handlerProviders;
  private ProxyConfiguration proxyConfiguration;
  private final UnsupportedMethodErrorSender errorSender;

  @Inject
  public RequestDispatcher(
      HttpServletRequest request,
      HttpServletResponse response,
      List<RequestMatcher> matchers,
      Map<RequestMatcher, Provider<RequestHandler>> handlerProviders,
      ProxyConfiguration proxyConfiguration,
      UnsupportedMethodErrorSender errorSender) {
    this.request = request;
    this.response = response;
    this.matchers = matchers;
    this.handlerProviders = handlerProviders;
    this.proxyConfiguration = proxyConfiguration;
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
            logger.trace("handling {} {}", uri, request);
            handlerProviders.get(matcher).get().handleIt();
            return;
          }
        }
      }
      for (RequestMatcher matcher : proxyConfiguration.getMatchers()) {
        if (matcher.uriMatches(uri)) {
          pathMatched = true;
          if (matcher.methodMatches(method)) {
            logger.trace("proxying {} {}", uri, request);
            proxyConfiguration.getRequestHandler(matcher).handleIt();
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
      logger.error("Error in request {}", e);
      errorSender.methodNotAllowed();
    } catch (Exception e) {
      logger.error("Error in request {}", e);
    }
  }
}
