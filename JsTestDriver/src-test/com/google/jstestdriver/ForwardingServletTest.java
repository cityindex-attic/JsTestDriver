/*
 * Copyright 2009 Google Inc.
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
package com.google.jstestdriver;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ForwardingServletTest extends TestCase {

  public void testUrlHasIdAndNoReferer() throws Exception {
    ForwardingServlet servlet = new ForwardingServlet("localhost", 8080);

    assertEquals("http://www.google.com", servlet.getForwardingUrl("http", "server", 42,
        "/forward/www.google.com").toString());
  }


  public void testMultipleLevelUrlRequest() throws Exception {
    ForwardingServlet servlet = new ForwardingServlet("localhost", 8080);

    assertEquals("http://www.google.com", servlet.getForwardingUrl("http", "server", 42,
        "/forward/www.google.com").toString());
    assertEquals("http://www.google.com/some/stuff.js", servlet.getForwardingUrl("http",
        "server", 42, "/forward/www.google.com/some/stuff.js").toString());
    assertEquals("http://www.google.com/great/file.html", servlet.getForwardingUrl("http",
        "server", 42, "/forward/www.google.com/great/file.html")
        .toString());
    assertEquals("http://www.google.com/great/anotherfile.html", servlet.getForwardingUrl("http",
        "server", 42, "/forward/www.google.com/great/anotherfile.html").toString());
    assertEquals("http://www.google.com/mooh/meuh.png", servlet.getForwardingUrl("http", "server",
        42, "/forward/www.google.com/mooh/meuh.png").toString());
  }

  public void testUrlHasPort() throws Exception {
    ForwardingServlet servlet = new ForwardingServlet("localhost", 8080);

    assertEquals("http://mycool.server.com:873", servlet.getForwardingUrl("http", "server", 42,
        "/forward/mycool.server.com:873").toString());
  }  
}
