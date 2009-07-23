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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.jstestdriver.JsTestDriverClientTest.FakeResponseStream;
import com.google.jstestdriver.JsonCommand.CommandType;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTaskTest extends TestCase {
  private final String baseUrl = "http://localhost/";
  private final Gson gson = new Gson();

  public void testConvertJsonResponseToObject() throws Exception {
    MockServer server = new MockServer();

    server.expect(baseUrl + "heartbeat?id=1", "OK");
    server.expect(baseUrl + "fileSet?POST?{id=1, fileSet=[]}", "");
    server.expect(baseUrl + "cmd?POST?{data={mooh}, id=1}", "");
    server.expect(baseUrl + "cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    LinkedHashSet<FileInfo> files = new LinkedHashSet<FileInfo>();
    CommandTask task = createCommandTask(server, files, files, params, stream, null);

    task.run();
    Response response = stream.getResponse();

    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }

  public void testUploadFiles() throws Exception {
    MockServer server = new MockServer();
    FileInfo fileInfo = new FileInfo("foo.js", 1232, false, false, null);

    server.expect(baseUrl + "heartbeat?id=1", "OK");
    server.expect(baseUrl + "fileSet?POST?{id=1, fileSet=" + gson.toJson(Arrays.asList(fileInfo))
        + "}", "");
    server.expect(baseUrl + "cmd?POST?{data={mooh}, id=1}", "");
    server.expect(baseUrl + "cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    MockFileLoader fileReader = new MockFileLoader();
    fileReader.addExpectation(fileInfo, "foobar");
    CommandTask task = createCommandTask(server, new LinkedHashSet<FileInfo>(Arrays
        .asList(fileInfo)), new LinkedHashSet<FileInfo>(), params, stream, fileReader);

    task.run();
    Response response = stream.getResponse();

    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }

  public void testUploadServeOnlyFiles() throws Exception {
    MockServer server = new MockServer();

    // test file data.
    FileInfo loadInfo = new FileInfo("foo.js", 1232, false, false, null);
    String loadInfoContents = "foobar";

    FileInfo serveInfo = new FileInfo("foo2.js", 1232, false, true, null);
    String serveInfoContents = "foobar2";
    List<FileInfo> fileSet = Arrays.asList(loadInfo, serveInfo);

    // server expects
    server.expect(baseUrl + "heartbeat?id=1", "OK");
    server.expect(baseUrl + "fileSet?POST?{id=1, fileSet=" + gson.toJson(fileSet) + "}", gson
        .toJson(fileSet));

    JsonCommand cmd = new JsonCommand(CommandType.RESET, Collections.<String> emptyList());
    Map<String, String> resetParams = new LinkedHashMap<String, String>();
    resetParams.put("id", "1");
    resetParams.put("data", gson.toJson(cmd));

    server.expect(baseUrl + "cmd?POST?" + resetParams, "");
    server.expect(baseUrl + "cmd?id=1", "ignore");
    server.expect(baseUrl
        + "fileSet?POST?{id=1, data="
        + gson.toJson(Arrays.asList(new FileInfo(loadInfo.getFileName(), loadInfo.getTimestamp(),
            loadInfo.isPatch(), loadInfo.isServeOnly(), loadInfoContents), new FileInfo(serveInfo
            .getFileName(), serveInfo.getTimestamp(), serveInfo.isPatch(), serveInfo.isServeOnly(),
            serveInfoContents))) + "}", "");

    String url = baseUrl
        + "cmd?POST?"
        + createLoadCommandString("1", CommandType.LOADTEST, Arrays.asList(CommandTask
            .fileInfoToFileSource(loadInfo)));
    server.expect(url, "ignore");
    server.expect(baseUrl + "cmd?id=1", "{\"response\":" + createLoadedFilesResponseString()
        + ", \"last\":true}");
    server.expect(baseUrl + "cmd?POST?{data={mooh}, id=1}", "");
    server.expect(baseUrl + "cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    MockFileLoader fileReader = new MockFileLoader();
    fileReader.addExpectation(loadInfo, loadInfoContents);
    fileReader.addExpectation(serveInfo, serveInfoContents);
    CommandTask task = createCommandTask(server, new LinkedHashSet<FileInfo>(fileSet),
        new LinkedHashSet<FileInfo>(Arrays.asList(serveInfo)), params, stream, fileReader);

    task.run();
    Response response = stream.getResponse();

    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }

  private String createLoadedFilesResponseString() {
    Response response = new Response();
    LoadedFiles loadedFiles = new LoadedFiles();
    loadedFiles.setMessage("");
    response.setResponse(gson.toJson(loadedFiles));
    return gson.toJson(response);
  }

  private String createLoadCommandString(String id, CommandType command,
      List<FileSource> filesToLoad) {
    Map<String, String> loadFileParams = new LinkedHashMap<String, String>();
    loadFileParams.put("id", id);
    List<String> loadParameters = new LinkedList<String>();
    loadParameters.add(gson.toJson(filesToLoad));
    loadParameters.add("false");
    loadFileParams.put("data", gson.toJson(new JsonCommand(CommandType.LOADTEST, loadParameters)));
    return loadFileParams.toString();
  }

  private CommandTask createCommandTask(MockServer server, LinkedHashSet<FileInfo> files,
      LinkedHashSet<FileInfo> serveFiles, Map<String, String> params, FakeResponseStream stream,
      MockFileLoader fileLoader) {
    CommandTask task = new CommandTask(new ActionFactory.ActionFactoryFileFilter(), stream, files,
        "http://localhost", server, params, new HeartBeatManagerStub(), fileLoader);
    return task;
  }

  private class MockFileLoader implements FileLoader {
    private HashMap<FileInfo, String> expected = new HashMap<FileInfo, String>();

    public void addExpectation(FileInfo file, String contents) {
      expected.put(file, contents);
    }

    public List<FileInfo> loadFiles(Set<FileInfo> filesToLoad, boolean shouldReset) {
      List<FileInfo> loaded = new LinkedList<FileInfo>();
      for (FileInfo info : filesToLoad) {
        assertTrue("File " + info + " was not found in " + expected.keySet(), expected
            .containsKey(info));
        loaded.add(new FileInfo(info.getFileName(), info.getTimestamp(), info.isPatch(), info
            .isServeOnly(), expected.get(info)));
      }
      return loaded;
    }
  }
}
