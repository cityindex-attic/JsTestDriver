// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver;

import com.google.jstestdriver.model.ConcretePathPrefix;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.model.NullPathPrefix;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.servlet.ServletHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class ProxyHandlerTest extends TestCase {

  private IMocksControl control;
  private Request request;
  private Response response;
  private ServletHandler handler;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    control = EasyMock.createControl();
    handler = control.createMock(ServletHandler.class);
    request = control.createMock(Request.class);
    response = control.createMock(Response.class);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    control.verify();
  }

  public void testHandleRootPathWithoutPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler(null, handler);

    control.replay();

    proxy.handle("/", request, response, 0);
  }

  public void testHandleMatchedPathWithoutPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler(null, handler);

    EasyMock.expect(handler.matchesPath("/capture")).andReturn(true);

    control.replay();

    proxy.handle("/capture", request, response, 0);
  }

  public void testHandleUnmatchedPathWithoutPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler(null, handler);

    EasyMock.expect(handler.matchesPath("/home")).andReturn(false);
    EasyMock.expect(request.getRequestURI()).andReturn("/home");
    /* expect */ request.setRequestURI("/jstd/proxy/home");

    control.replay();

    proxy.handle("/home", request, response, 0);
  }

  public void testHandleRootPathWithPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler("jstd", handler);

    EasyMock.expect(handler.matchesPath("/")).andReturn(false);
    EasyMock.expect(request.getRequestURI()).andReturn("/");
    /* expect */ request.setRequestURI("/jstd/proxy/");

    control.replay();

    proxy.handle("/", request, response, 0);
  }

  public void testHandlePrefixedRootPathWithPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler("jstd", handler);

    EasyMock.expect(handler.matchesPath("/jstd/")).andReturn(true);

    control.replay();

    proxy.handle("/jstd/", request, response, 0);
  }

  public void testHandleMatchedPathWithPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler("jstd", handler);

    EasyMock.expect(handler.matchesPath("/jstd/capture")).andReturn(true);

    control.replay();

    proxy.handle("/jstd/capture", request, response, 0);
  }

  public void testHandleUnmatchedPathWithPathPrefix() throws Exception {
    ProxyHandler proxy = buildProxyHandlerWithServletHandler("jstd", handler);

    EasyMock.expect(handler.matchesPath("/home")).andReturn(false);
    EasyMock.expect(request.getRequestURI()).andReturn("/home");
    /* expect */ request.setRequestURI("/jstd/proxy/home");

    control.replay();

    proxy.handle("/home", request, response, 0);
  }

  private ProxyHandler buildProxyHandlerWithServletHandler(
      String prefix,
      final ServletHandler handler) {
    return new ProxyHandler(prefix == null ?
        new NullPathPrefix() : new ConcretePathPrefix(prefix)) {
      @Override
      protected void dispatchRequest(String target,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     int dispatch) {}
      @Override
      protected ServletHandler getServletHandler() {
        return handler;
      }
    };
  }
}
