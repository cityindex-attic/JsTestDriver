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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.jstestdriver.JsTestDriverClientTest.FakeResponseStream;
import com.google.jstestdriver.JsonCommand.CommandType;
import com.google.jstestdriver.browser.BrowserFileSet;
import com.google.jstestdriver.hooks.FileInfoScheme;
import com.google.jstestdriver.model.JstdTestCase;
import com.google.jstestdriver.util.NullStopWatch;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTaskTest extends TestCase {
  private final String baseUrl = "http://localhost/";
  private final Gson gson = new Gson();

  public void testConvertJsonResponseToObject() throws Exception {
    MockServer server = new MockServer();

    server.expect(baseUrl + "heartbeat?id=1", "OK");
    server.expect(baseUrl + "fileSet?POST?{data=[], action=serverFileCheck}", "[]");
    server.expect(baseUrl + "fileSet?POST?{id=1, data=[], action=browserFileCheck}", gson.toJson(new BrowserFileSet()));
    server.expect(baseUrl + "cmd?POST?{data={mooh}, id=1}", "");
    server.expect(baseUrl + "cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    CommandTask task =
        createCommandTask(server, params, stream, new MockFileLoader(), true);

    task.run(new JstdTestCase(Collections.<FileInfo>emptyList(), Collections.<FileInfo>emptyList()));
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
    server.expect(baseUrl + "fileSet?POST?{data=" + gson.toJson(Arrays.asList(fileInfo))
        + ", action=serverFileCheck}", "[]");
    server.expect(baseUrl + "fileSet?POST?{id=1, data=" + gson.toJson(Arrays.asList(fileInfo))
      + ", action=browserFileCheck}", gson.toJson(new BrowserFileSet()));
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
    CommandTask task = createCommandTask(server, params,
        stream, fileReader, true);

    task.run(new JstdTestCase(Collections.<FileInfo>emptyList(), Lists.newArrayList(fileInfo)));
    Response response = stream.getResponse();

    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }

  public void testUploadServeOnlyFiles() throws Exception {
    MockServer server = new MockServer();

    // test file data.
    FileInfo loadInfo = new FileInfo("foo.js", 0, false, false, null);
    String loadInfoContents = "foobar";

    FileInfo serveInfo = new FileInfo("foo2.js", 0, false, true, null);
    String serveInfoContents = "foobar2";
    List<FileInfo> fileSet = Arrays.asList(loadInfo, serveInfo);

    final BrowserFileSet browserFileSet =
        new BrowserFileSet(fileSet, Lists.<FileInfo>newArrayList());

    // server expects
    server.expect(baseUrl + "heartbeat?id=1", "OK");
    server.expect(baseUrl + "fileSet?POST?{id=1, data=" + gson.toJson(Arrays.asList(loadInfo)) + ", action=browserFileCheck}",
        gson.toJson(browserFileSet));
    server.expect(baseUrl + "fileSet?POST?{data=" + gson.toJson(fileSet) + ", action=serverFileCheck}",
      "[]");

    JsonCommand cmd = new JsonCommand(CommandType.RESET, Collections.<String>emptyList());
    Map<String, String> resetParams = new LinkedHashMap<String, String>();
    resetParams.put("id", "1");
    resetParams.put("data", gson.toJson(cmd));

    server.expect(baseUrl + "cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    server.expect(
        baseUrl
        + "fileSet?POST?{data="
        + gson.toJson(Arrays.asList(
          new FileInfo(loadInfo.getFilePath(), loadInfo.getTimestamp(),
            loadInfo.isPatch(), loadInfo.isServeOnly(), loadInfoContents),
          new FileInfo(serveInfo.getFilePath(), serveInfo.getTimestamp(), serveInfo.isPatch(),
                serveInfo.isServeOnly(), serveInfoContents))) + ", action=serverFileCheck}", "");

    String url =
        baseUrl + "cmd?POST?" + createLoadCommandString("1", CommandType.LOADTEST,
            Arrays.asList(fileInfoToFileSource(loadInfo)));
    server.expect(url, "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    server.expect(baseUrl + "cmd?id=1",
        "{\"response\":" + createLoadedFilesResponseString() + ", \"last\":true}");
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
    CommandTask task = createCommandTask(server, params, stream, fileReader, true);

    task.run(new JstdTestCase(Collections.<FileInfo>emptyList(), fileSet));
    Response response = stream.getResponse();

    assertEquals("{\"loadedFiles\":[]}", response.getResponse());
    assertEquals(null, response.getBrowser().getName());
    assertEquals("", response.getError());
    assertEquals(0, response.getExecutionTime());
  }

  private String createLoadedFilesResponseString() {
    Response response = new Response();
    LoadedFiles loadedFiles = new LoadedFiles();

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

  private CommandTask createCommandTask(MockServer server, Map<String, String> params, FakeResponseStream stream,
      MockFileLoader fileLoader, boolean upload) {
    CommandTask task = new CommandTask(new DefaultFileFilter(), stream, "http://localhost",
        server, params, fileLoader, upload,
        new NullStopWatch(), ImmutableSet.<FileInfoScheme>of(new HttpFileInfoScheme()));
    return task;
  }

  private FileSource fileInfoToFileSource(FileInfo info) {
    if (info.getFilePath().startsWith("http://")) {
      return new FileSource(info.getFilePath(), info.getTimestamp());
    }
    return new FileSource("/test/" + info.getFilePath(), info.getTimestamp());
  }

}
