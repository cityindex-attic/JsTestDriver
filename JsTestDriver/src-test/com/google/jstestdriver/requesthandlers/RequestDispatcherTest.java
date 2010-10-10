// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.util.Providers;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class RequestDispatcherTest extends TestCase {

  private IMocksControl control;
  
  private HttpServletRequest request;
  private HttpServletResponse response;

  private RequestMatcher one;
  private RequestMatcher two;

  private RequestHandler handlerOne;
  private RequestHandler handlerTwo;

  private UnsupportedMethodErrorSender sender;
  
  private RequestDispatcher dispatcher;

  @Override
  protected void setUp() throws Exception {
    control = EasyMock.createControl();

    request = control.createMock(HttpServletRequest.class);
    response = control.createMock(HttpServletResponse.class);
    
    one = new RequestMatcher(HttpMethod.GET, "/one/two");
    two = new RequestMatcher(HttpMethod.POST, "/a/*");

    handlerOne = control.createMock(RequestHandler.class);
    handlerTwo = control.createMock(RequestHandler.class);

    sender = control.createMock(UnsupportedMethodErrorSender.class);
    
    dispatcher = new RequestDispatcher(
        request,
        response,
        ImmutableList.of(one, two),
        ImmutableMap.of(
            one, Providers.of(handlerOne),
            two, Providers.of(handlerTwo)),
        sender);
  }

  public void testDispatch_GET() throws Exception {
    expect(request.getMethod()).andReturn("GET");
    expect(request.getRequestURI()).andReturn("/one/two").anyTimes();
    /*expect*/ handlerOne.handleIt();

    control.replay();

    dispatcher.dispatch();

    control.verify();
  }

  public void testDispatch_POST() throws Exception {
    expect(request.getMethod()).andReturn("POST");
    expect(request.getRequestURI()).andReturn("/a/b").anyTimes();
    /*expect*/ handlerTwo.handleIt();

    control.replay();

    dispatcher.dispatch();

    control.verify();
  }

  public void testDispatch_POST_methodNotAllowed() throws Exception {
    expect(request.getMethod()).andReturn("POST");
    expect(request.getRequestURI()).andReturn("/one/two").anyTimes();
    /*expect*/ sender.methodNotAllowed();

    control.replay();

    dispatcher.dispatch();

    control.verify();
  }

  public void testDispatch_GET_methodNotAllowed() throws Exception {
    expect(request.getMethod()).andReturn("GET");
    expect(request.getRequestURI()).andReturn("/a/b").anyTimes();
    /*expect*/ sender.methodNotAllowed();

    control.replay();

    dispatcher.dispatch();

    control.verify();
  }

  public void testDispatch_unsupportedMethod() throws Exception {
    expect(request.getMethod()).andReturn("YOUR_MOM");
    /*expect*/ sender.methodNotAllowed();

    control.replay();

    dispatcher.dispatch();

    control.verify();
  }

  public void testDispatch_GET_notFound() throws Exception {
    expect(request.getMethod()).andReturn("GET");
    expect(request.getRequestURI()).andReturn("/nothing").anyTimes();
    /*expect*/ response.sendError(eq(HttpServletResponse.SC_NOT_FOUND), (String) anyObject());

    control.replay();

    dispatcher.dispatch();

    control.verify();
  }
}
