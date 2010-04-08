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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetServletTest extends TestCase {

  private final Gson gson = new Gson();
  private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
  private final PrintWriter writer = new PrintWriter(baos);

  public void testAddFilesNameAndDataToMap() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    capturedBrowsers.addSlave(new SlaveBrowser(new MockTime(0), "1", new BrowserInfo(), SlaveBrowser.TIMEOUT));
    Map<String, FileInfo> files = new HashMap<String, FileInfo>(); 
    FilesCache filesCache = new FilesCache(files);
    FileSetServlet fileSetServlet =
        new FileSetServlet(capturedBrowsers, filesCache);

    fileSetServlet.uploadFiles("1", "[ { 'file': 'dummy.js', 'data': 'some data' }," +
        "{ 'file': 'dummytoo.js', 'data': 'some more data' }]");
    assertEquals(2, filesCache.getFilesNumber());
    assertEquals("some data", filesCache.getFileContent("dummy.js"));
    assertEquals("some more data", filesCache.getFileContent("dummytoo.js"));
  }

  public void testDoNotReloadAlreadyLoadedFilesWithSameTimestamp() throws Exception {
    CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
    BrowserInfo browserInfo = new BrowserInfo();

    browserInfo.setName("firefox");
    SlaveBrowser slave = new SlaveBrowser(new MockTime(0), "1", browserInfo, SlaveBrowser.TIMEOUT);

    capturedBrowsers.addSlave(slave);
    Map<String, FileInfo> files = new HashMap<String, FileInfo>();
    FilesCache filesCache = new FilesCache(files);
    FileSetServlet fileSetServlet = new FileSetServlet(capturedBrowsers, filesCache);
    List<FileInfo> fileInfos = new LinkedList<FileInfo>();

    fileInfos.add(new FileInfo("filename1.js", 123, false, false, null));
    fileInfos.add(new FileInfo("filename2.js", 456, false, false, null));
    fileInfos.add(new FileInfo("filename3.js", 789, false, false, null));
    fileInfos.add(new FileInfo("filename4.js", 101112, false, false, null));
    fileSetServlet.checkFileSet(gson.toJson(fileInfos), "1", writer);

    Collection<FileInfo> response =
        gson.fromJson(baos.toString(), new TypeToken<Collection<FileInfo>>() {}.getType());

    baos.reset();

    assertEquals(4, response.size());
    Iterator<FileInfo> iterator = response.iterator();

    FileInfo info1 = iterator.next();
    assertEquals("filename1.js", info1.getFileName());
    assertEquals(123, info1.getTimestamp());

    FileInfo info2 = iterator.next();
    assertEquals("filename2.js", info2.getFileName());
    assertEquals(456, info2.getTimestamp());

    FileInfo info3 = iterator.next();
    assertEquals("filename3.js", info3.getFileName());
    assertEquals(789, info3.getTimestamp());

    FileInfo info4 = iterator.next();
    assertEquals("filename4.js", info4.getFileName());
    assertEquals(101112, info4.getTimestamp());

    slave.addFiles(response);
    
    List<FileInfo> updatedFileInfos = new LinkedList<FileInfo>();

    updatedFileInfos.add(new FileInfo("filename1.js", 123, false, false, null));
    updatedFileInfos.add(new FileInfo("filename2.js", 456, false, false, null));
    updatedFileInfos.add(new FileInfo("filename3.js", 131415, false, false, null));
    updatedFileInfos.add(new FileInfo("filename4.js", 101112, false, false, null));
    fileSetServlet.checkFileSet(gson.toJson(updatedFileInfos), "1", writer);
    Collection<FileInfo> updatedResponse =
      gson.fromJson(baos.toString(), new TypeToken<Collection<FileInfo>>() {}.getType());

    baos.reset();

    assertEquals(1, updatedResponse.size());
    Iterator<FileInfo> updatedIterator = updatedResponse.iterator();

    FileInfo updatedInfo = updatedIterator.next();

    assertEquals("filename3.js", updatedInfo.getFileName());
    assertEquals(131415, updatedInfo.getTimestamp());    
  }
}
