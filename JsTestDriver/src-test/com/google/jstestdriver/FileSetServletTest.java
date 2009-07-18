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

import java.util.HashMap;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetServletTest extends TestCase {

  public void testAddFilesNameAndDataToMap() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    capturedBrowsers.addSlave(new SlaveBrowser(new MockTime(0), "1", new BrowserInfo()));
    Map<String, FileData> files = new HashMap<String, FileData>(); 
    FilesCache filesCache = new FilesCache(files);
    FileSetServlet fileSetServlet =
        new FileSetServlet(capturedBrowsers, filesCache);

    fileSetServlet.uploadFiles("1", "[ { 'file': 'dummy.js', 'data': 'some data' }," +
        "{ 'file': 'dummytoo.js', 'data': 'some more data' }]");
    assertEquals(2, filesCache.getFilesNumber());
    assertEquals("some data", filesCache.getFileContent("dummy.js"));
    assertEquals("some more data", filesCache.getFileContent("dummytoo.js"));
  }
}
