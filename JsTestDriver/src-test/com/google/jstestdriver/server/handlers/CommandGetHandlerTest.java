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
package com.google.jstestdriver.server.handlers;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.TimeImpl;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandGetHandlerTest extends TestCase {

  public void testListBrowsers() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserInfo browserInfo = new BrowserInfo();
    browserInfo.setId(1);
    SlaveBrowser slave = new SlaveBrowser(new TimeImpl(), "1", browserInfo, SlaveBrowser.TIMEOUT);

    capturedBrowsers.addSlave(slave);
    CommandGetHandler handler = new CommandGetHandler(null, null, new Gson(), capturedBrowsers);

    assertEquals("[{\"id\":1}]", handler.listBrowsers());
  }
}