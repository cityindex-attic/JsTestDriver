// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class UnsupportedMethodErrorSenderTest extends TestCase {

  private HttpServletResponse response;

  @Override
  protected void setUp() throws Exception {
    response = createMock(HttpServletResponse.class);
  }

  @Override
  protected void tearDown() throws Exception {
    verify(response);
  }

  public void testHttpOnePointZero() throws Exception {
    expectErrorCode(HttpServletResponse.SC_BAD_REQUEST, "HTTP 1.0");
  }

  public void testHttpOnePointOne() throws Exception {
    expectErrorCode(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP 1.1");
  }

  private void expectErrorCode(int errorCode, String httpVersion) throws IOException {
    UnsupportedMethodErrorSender sender =
        new UnsupportedMethodErrorSender(HttpMethod.GET,  httpVersion, response);

    /*expect*/ response.sendError(eq(errorCode), contains(HttpMethod.GET.name()));

    replay(response);
    sender.methodNotAllowed();
  }
}
