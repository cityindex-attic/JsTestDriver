// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.inject.Inject;
import com.google.jstestdriver.model.HandlerPathPrefix;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.HandlerWrapper;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.servlet.ProxyServlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Divides the URI space of the JsTestDriver server in two: those requests
 * handled by JsTestDriver and those forwarded to a server-under-test. Does so
 * by prefixing unrecognized URIs with "/proxy" so that they may be handled by
 * the {@link ProxyServlet.Transparent} bound in the {@link ServletHandler}.
 *
 * TODO(rdionne): Write a unit test for this class after I add the EasyMock
 * library. {@link HttpServletRequest} is not easily mockable by hand.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyHandler extends HandlerWrapper {

  public static final String ROOT = "/";

  public static final String PROXY_PREFIX = "/proxy";

  private final HandlerPathPrefix prefix;

  @Inject
  public ProxyHandler(HandlerPathPrefix prefix) {
    this.prefix = prefix;
  }

  /**
   * Mutates the {@link HttpServletRequest}'s URI path, prefixing it with the
   * proxy path.
   *
   * Visible for testing.
   */
  protected void addPrefixToRequestUri(HttpServletRequest request) {
    ((Request) request).setRequestURI(
        prefix.prefixPath(PROXY_PREFIX + request.getRequestURI(), "jstd"));
  }

  /**
   * @return the {@code target} URI prefixed with the proxy path.
   */
  private String addPrefixToTarget(String target) {
    return prefix.prefixPath(PROXY_PREFIX + target, "jstd");
  }

  /**
   * Dispatches the request to the {@link ServletHandler}.
   *
   * Visible for testing.
   */
  protected void dispatchRequest(String target,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 int dispatch)
      throws IOException,
             ServletException {
    super.handle(target, request, response, dispatch);
  }

  @Override
  public void handle(String target,
                     HttpServletRequest request,
                     HttpServletResponse response,
                     int dispatch)
      throws IOException,
             ServletException {
    if (!isHandledByJsTestDriverServer(target)) {
      addPrefixToRequestUri(request);
      target = addPrefixToTarget(target);
    }
    dispatchRequest(target, request, response, dispatch);
  }

  /**
   * Visible for testing.
   */
  protected ServletHandler getServletHandler() {
    return ((Context) getHandler()).getServletHandler();
  }

  /**
   * @return true if the JsTestDriver server handles this {@code target} URI.
   *
   * Visible for testing.
   */
  boolean isHandledByJsTestDriverServer(String target) {
    return prefix.prefixPath(target).equals(ROOT) || getServletHandler().matchesPath(target);
  }
}
