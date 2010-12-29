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

import java.util.Map;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.jstestdriver.browser.BrowserFileSet;
import com.google.jstestdriver.browser.BrowserIdStrategy;
import com.google.jstestdriver.runner.RunnerType;
import com.google.jstestdriver.server.handlers.CaptureHandler;
import com.google.jstestdriver.servlet.fileset.BrowserFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileCheck;
import com.google.jstestdriver.servlet.fileset.ServerFileUpload;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetRequestHandlerTest extends TestCase {
  public void testServerCheckFileAction() throws Exception {
    final String fileOne = "one.js";
    final String fileTwo = "two.js";
    final Map<String, FileInfo> fileMap = Maps.newHashMap();
    fileMap.put(fileOne, createFile(fileOne, 1));
    fileMap.put(fileTwo, createFile(fileTwo, 1));

    final ServerFileCheck serverFileCheck =
        new ServerFileCheck(new FilesCache(fileMap), new FileSetCacheStrategy());

    assertEquals(
        Sets.newHashSet(createFile(fileOne, 3)),
        serverFileCheck.handle(null,
            Lists.newArrayList(createFile(fileOne, 3), createFile(fileTwo, 1))));
  }

  FileInfo createFile(String path, long timestamp) {
    return new FileInfo(path, timestamp, -1, false, false, null);
  }

  FileInfo createFile(FileInfo info, long timestamp) {
    return new FileInfo(info.getFilePath(), timestamp, -1, false, false, null);
  }

  public void testBrowserCheckAction() throws Exception {
    final String fileOne = "one.js";
    final String fileTwo = "two.js";
    final String fileThree = "three.js";


    final CapturedBrowsers browsers = new CapturedBrowsers(new BrowserIdStrategy(new MockTime(0)));
    final BrowserInfo browserInfo = new BrowserInfo();
    browserInfo.setName("firefox");
    final SlaveBrowser browser =
        new SlaveBrowser(new MockTime(0), "1", browserInfo, 100, null, CaptureHandler.QUIRKS,
            RunnerType.CLIENT);
    browser.addFiles(Lists.newArrayList(createFile(fileOne, 1), createFile(fileTwo, 1),
        createFile(fileThree, 1)));
    browsers.addSlave(browser);
    final BrowserFileCheck browserFileCheck = new BrowserFileCheck(new FileSetCacheStrategy());

    assertEquals(
        new BrowserFileSet(Lists.newArrayList(createFile(fileOne, 3)),
            Lists.newArrayList(createFile(fileThree, 3))), browserFileCheck.handle(browser,
            Lists.newArrayList(createFile(fileOne, 3), createFile(fileTwo, 1))));
  }

  public void testUploadFilesToServer() throws Exception {
    final String fileOne = "one.js";
    final String fileTwo = "two.js";
    final Map<String, FileInfo> expected = Maps.newHashMap();
    final Map<String, FileInfo> actual = Maps.newHashMap();
    expected.put(fileOne, createFile(fileOne, 1));
    expected.put(fileTwo, createFile(fileTwo, 1));

    final ServerFileUpload serverFileUpload = new ServerFileUpload(new FilesCache(actual));

    serverFileUpload.handle(null,
        Lists.newArrayList(createFile(fileOne, 3), createFile(fileTwo, 1)));

    assertEquals(expected, actual);
  }
}
