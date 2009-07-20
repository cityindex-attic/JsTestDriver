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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.jstestdriver.JsTestDriverClientTest.FakeResponseStream;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTaskTest extends TestCase {

  public void testConvertJsonResponseToObject() throws Exception {
    MockServer server = new MockServer();

    server.expect("http://localhost/heartbeat?id=1", "OK");
    server.expect("http://localhost/fileSet?POST?{id=1, fileSet=[]}", "");
    server.expect("http://localhost/cmd?POST?{data={mooh}, id=1}", "");
    server.expect("http://localhost/cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    CommandTask task = new CommandTask(new ActionFactory.ActionFactoryFileFilter(), stream,
        new LinkedHashSet<FileInfo>(), new LinkedHashSet<FileInfo>(),
        "http://localhost", server, params, null);

    task.run();
    Response response = stream.getResponse();

    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }

  public void testUploadFiles() throws Exception {
    MockServer server = new MockServer();
    Gson gson = new Gson();
    FileInfo fileInfo = new FileInfo("foo.js", 1232, false, false);

    server.expect("http://localhost/heartbeat?id=1", "OK");
    server.expect("http://localhost/fileSet?POST?{id=1, fileSet="
        + gson.toJson(Arrays.asList(fileInfo)) + "}", "");
    server.expect("http://localhost/cmd?POST?{data={mooh}, id=1}", "");
    server.expect("http://localhost/cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    MockFileReader fileReader = new MockFileReader();
    fileReader.addExpectation(fileInfo.getFileName(), "foobar");
    CommandTask task = new CommandTask(new ActionFactory.ActionFactoryFileFilter(), stream,
        new LinkedHashSet<FileInfo>(Arrays.asList(fileInfo)), new LinkedHashSet<FileInfo>(),
        "http://localhost", server, params, fileReader);

    task.run();
    Response response = stream.getResponse();

    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }
  
  public void testUploadServeOnlyFiles() throws Exception {
    MockServer server = new MockServer();
    Gson gson = new Gson();
    FileInfo loadInfo = new FileInfo("foo.js", 1232, false);
    FileInfo serveInfo = new FileInfo("foo2.js", 1232, false);
    
    server.expect("http://localhost/heartbeat?id=1", "OK");
    server.expect("http://localhost/fileSet?POST?{id=1, fileSet="
        + gson.toJson(Arrays.asList(loadInfo, serveInfo)) + "}", "");
    server.expect("http://localhost/cmd?POST?{data={mooh}, id=1}", "");
    server.expect("http://localhost/cmd?id=1", "{\"response\":{\"response\":\"response\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
        + "\"last\":true}");
    Map<String, String> params = new LinkedHashMap<String, String>();
    
    params.put("data", "{mooh}");
    params.put("id", "1");
    FakeResponseStream stream = new FakeResponseStream();
    MockFileReader fileReader = new MockFileReader();
    fileReader.addExpectation(loadInfo.getFileName(), "foobar");
    CommandTask task = new CommandTask(new ActionFactory.ActionFactoryFileFilter(), stream,
        new LinkedHashSet<FileInfo>(Arrays.asList(loadInfo)), new LinkedHashSet<FileInfo>(Arrays
            .asList(serveInfo)), "http://localhost", server, params, fileReader);

    task.run();
    Response response = stream.getResponse();
    
    assertEquals("response", response.getResponse());
    assertEquals("browser", response.getBrowser().getName());
    assertEquals("error", response.getError());
    assertEquals(123L, response.getExecutionTime());
  }
  
  private class MockFileReader implements FileReader {
    private HashMap<String, String> expected;

    public MockFileReader() {
      expected = new HashMap<String, String>();
    }

    public void addExpectation(String fileName, String contents) {
      expected.put(fileName, contents);
    }

    public String readFile(String file) {
      if (!expected.containsKey(file)) {
        Assert.fail(String.format("%s not found in %s", file, expected.keySet()));
      }
      return expected.get(file);
    }

  }
}
