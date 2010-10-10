/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.server.handlers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.google.jstestdriver.SlaveResourceService;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class SlaveResourceHandlerTest extends TestCase {

  public void testIdChoppedOffFromThePath() throws Exception {
    String location = getClass().getPackage().getName().replace(".", "/");
    HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
    HttpServletResponse response = EasyMock.createMock(HttpServletResponse.class);
    SlaveResourceHandler handler = new SlaveResourceHandler(request, response, new SlaveResourceService(location));
    OutputStream oStream = new ByteArrayOutputStream();
    ServletOutputStream servletOutputStream = buildServletOutputStream(oStream);

    EasyMock.expect(request.getPathInfo()).andReturn("/XXX/Test.file");
    EasyMock.expect(response.getOutputStream()).andReturn(servletOutputStream);

    EasyMock.replay(request, response);

    handler.handleIt();
    assertTrue(oStream.toString().length() > 0);
    oStream.close();

    EasyMock.verify(request, response);
  }

  public void testStripOffId() throws Exception {
    assertEquals("/B/C", SlaveResourceHandler.stripId("/X/B/C"));
  }

  private ServletOutputStream buildServletOutputStream(final OutputStream oStream) {
    return new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        oStream.write(b);
      }
    };
  }
}