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
public class CommandServletTest extends TestCase {

  public void testListBrowsers() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserInfo browserInfo = new BrowserInfo();
    browserInfo.setId(1);
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), "1", browserInfo);

    capturedBrowsers.addSlave(slave);
    CommandServlet servlet = new CommandServlet(capturedBrowsers);

    assertEquals("[{\"id\":1}]", servlet.listBrowsers());
  }

//  public void testExtractFileArrayFromJsonString() throws Exception {
//    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
//    CommandServlet servlet = new CommandServlet(capturedBrowsers);
//
//    Set<String> files =
//        servlet.extractFiles("this.loadTest([\"file1\", \"file2\"], this.boundCreateScript)");
//
//    assertEquals(2, files.size());
//    boolean file1WasHere = false;
//    boolean file2WasHere = false;
//    Iterator<String> iterator = files.iterator();
//
//    while (iterator.hasNext()) {
//      String next = iterator.next();
//
//      if (next.equals("file1")) {
//        file1WasHere = true;
//      } else if (next.equals("file2")) {
//        file2WasHere = true;
//      }
//    }
//    assertTrue(file1WasHere);
//    assertTrue(file2WasHere);
//  }
}
