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

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class TestResourceServletTest extends TestCase {

  private ByteArrayOutputStream out = new ByteArrayOutputStream();
  private PrintWriter writer = new PrintWriter(out);

  public void testEmptyReturnWhenFileNotPresent() throws Exception {
    TestResourceServlet servlet =
        new TestResourceServlet(new FilesCache(new HashMap<String, String>()));

    servlet.service("nothing", writer);
    assertEquals(0, out.toString().length());
  }

  public void testServeFile() throws Exception {
    Map<String, String> files = new HashMap<String, String>();

    files.put("dummy.js", "data");
    files.put("dummytoo.js", "more data");
    FilesCache filesCache = new FilesCache(files);
    TestResourceServlet resourceServlet = new TestResourceServlet(filesCache);

    resourceServlet.service("dummy.js", writer);
    assertEquals("data", out.toString());
    out.reset();
    resourceServlet.service("dummytoo.js", writer);
    assertEquals("more data", out.toString());
  }
}
