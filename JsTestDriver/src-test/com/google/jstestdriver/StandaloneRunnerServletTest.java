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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class StandaloneRunnerServletTest extends TestCase {

  public void testCaptureAddFilesToLoadAndRun() throws Exception {
    Map<String, FileInfo> files = new LinkedHashMap<String, FileInfo>();

    files.put("file1.js", new FileInfo("file1.js", 30, false, false, "content1"));
    files.put("file2.js", new FileInfo("file2.js", 5, false, false, "content2"));
    files.put("file3.js", new FileInfo("file3.js", 53, false, false, "content3"));
    files.put("file4.js", new FileInfo("file4.js", 1, false, false, "content4"));
    FilesCache cache = new FilesCache(files);
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    StandaloneRunnerServlet runnerServlet =
        new StandaloneRunnerServlet(new BrowserHunter(capturedBrowsers, SlaveBrowser.TIMEOUT), cache,
            new StandaloneRunnerFilesFilterImpl(), new SlaveResourceService(""));
    runnerServlet.service("Chrome/2.0", "/runner", "1");
    SlaveBrowser slaveBrowser = capturedBrowsers.getBrowser("1");

    assertNotNull(slaveBrowser.peekCommand());
    Command cmd = slaveBrowser.dequeueCommand();

    assertNotNull(cmd);
    assertEquals("{\"command\":\"loadTest\"," +
    		"\"parameters\":[\"[{\\\"fileSrc\\\":\\\"/test/file1.js\\\",\\\"timestamp\\\":-1}," +
    		"{\\\"fileSrc\\\":\\\"/test/file2.js\\\",\\\"timestamp\\\":-1}," +
    		"{\\\"fileSrc\\\":\\\"/test/file3.js\\\",\\\"timestamp\\\":-1}," +
    		"{\\\"fileSrc\\\":\\\"/test/file4.js\\\",\\\"timestamp\\\":-1}]\",\"true\"]}",
    		cmd.getCommand());

    assertNotNull(slaveBrowser.peekCommand());
    cmd = slaveBrowser.dequeueCommand();
    assertNotNull(cmd);
    assertEquals("{\"command\":\"runAllTests\",\"parameters\":[\"false\",\"true\"]}",
        cmd.getCommand());
  }
}
