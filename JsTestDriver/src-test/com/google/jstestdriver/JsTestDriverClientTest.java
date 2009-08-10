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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.inject.Provider;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverClientTest extends TestCase {

  public static class ResponseStreamFactoryStub implements ResponseStreamFactory {

    private ResponseStream stream;

    public void setResponseStream(ResponseStream stream) {
      this.stream = stream;
    }

    public ResponseStream getEvalActionResponseStream() {
      return stream;
    }

    public ResponseStream getLoadLibrariesActionResponseStream() {
      return stream;
    }

    public ResponseStream getLoadTestsActionResponseStream() {
      return stream;
    }

    public ResponseStream getResetActionResponseStream() {
      return stream;
    }

    public ResponseStream getDryRunActionResponseStream() {
      return stream;
    }

    public ResponseStream getRunTestsActionResponseStream(String testId) {
      return stream;
    }
  }

  public static class FakeResponseStream implements ResponseStream {

    private Response response;

    public void finish() {
    }

    public void stream(Response response) {
      this.response = response;
    }

    public Response getResponse() {
      return response;
    }
  }

  public void testSendCommand() throws Exception {
    MockServer server = new MockServer();
    server.expect("http://localhost/heartbeat?id=1", "OK");
    server.expect("http://localhost/fileSet?POST?{id=1, fileSet=[]}", "");
    server.expect("http://localhost/cmd?POST?{data={\"command\":\"execute\","
        + "\"parameters\":[\"cmd\"]}, id=1}", "");
    server.expect("http://localhost/cmd?id=1", "{\"response\":"
        + "{\"response\":\"1\",\"browser\":{\"name\":\"browser1\"},"
        + "\"error\":\"error1\",\"executionTime\":3},\"last\":true}");

    server.expect("http://localhost/heartbeat?id=2", "OK");
    server.expect("http://localhost/fileSet?POST?{id=2, fileSet=[]}", "");
    server.expect("http://localhost/cmd?POST?{data={\"command\":\"execute\","
        + "\"parameters\":[\"cmd\"]}, id=2}", "");
    server.expect("http://localhost/cmd?id=2", "{\"response\":"
        + "{\"response\":\"2\",\"browser\":{\"name\":\"browser2\"},"
        + "\"error\":\"error2\",\"executionTime\":6},\"last\":true}");

    JsTestDriverClient client = new JsTestDriverClientImpl(new CommandTaskFactory(
        new DefaultFileFilter(), null, new Provider<HeartBeatManager>() {
          public HeartBeatManager get() {
            return new HeartBeatManagerStub();
          }
        }), new LinkedHashSet<FileInfo>(),
        "http://localhost", server);
    FakeResponseStream stream = new FakeResponseStream();

    client.eval("1", stream, "cmd");

    Response response = stream.getResponse();

    assertEquals("1", response.getResponse());
    assertEquals("browser1", response.getBrowser().getName());
    assertEquals("error1", response.getError());
    assertEquals(3L, response.getExecutionTime());

    client.eval("2", stream, "cmd");
    response = stream.getResponse();
    assertEquals("2", stream.getResponse().getResponse());
    assertEquals("browser2", response.getBrowser().getName());
    assertEquals("error2", response.getError());
    assertEquals(6L, response.getExecutionTime());
  }

  public void testGetListOfClients() throws Exception {
    MockServer server = new MockServer();
    server.expect("http://localhost/cmd?listBrowsers", "["
        + "{\"id\":0, \"name\":\"name0\", \"version\":\"ver0\", \"os\":\"os0\"},"
        + "{\"id\":1, \"name\":\"name1\", \"version\":\"ver1\", \"os\":\"os1\"}]");
    JsTestDriverClient client = new JsTestDriverClientImpl(new CommandTaskFactory(
        new DefaultFileFilter(), null, new Provider<HeartBeatManager>() {
          public HeartBeatManager get() {
            return new HeartBeatManagerStub();
          }
        }), new LinkedHashSet<FileInfo>(),
        "http://localhost", server);
    Collection<BrowserInfo> browsersCollection = client.listBrowsers();
    List<BrowserInfo> browsers = new ArrayList<BrowserInfo>(browsersCollection);

    assertEquals(2, browsers.size());
    BrowserInfo browser0 = browsers.get(0);

    assertTrue(browser0.getId().equals(0));
    assertEquals("name0", browser0.getName());
    assertEquals("ver0", browser0.getVersion());
    assertEquals("os0", browser0.getOs());

    BrowserInfo browser1 = browsers.get(1);

    assertTrue(browser1.getId().equals(1));
    assertEquals("name1", browser1.getName());
    assertEquals("ver1", browser1.getVersion());
    assertEquals("os1", browser1.getOs());
  }

  public void testRunATest() throws Exception {
    MockServer server = new MockServer();

    server.expect("http://localhost/heartbeat?id=1", "OK");
    server.expect("http://localhost/fileSet?POST?{id=1, fileSet=[]}", "");
    server.expect("http://localhost/cmd?POST?{data={\"command\":\"runAllTests\","
        + "\"parameters\":[\"false\",\"false\"]}, id=1}", "");
    server.expect("http://localhost/cmd?id=1", "{\"response\":{\"response\":\"PASSED\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error2\",\"executionTime\":123},"
        + "\"last\":true}");
    JsTestDriverClient client = new JsTestDriverClientImpl(new CommandTaskFactory(
        new DefaultFileFilter(), null, new Provider<HeartBeatManager>() {
          public HeartBeatManager get() {
            return new HeartBeatManagerStub();
          }
        }), new LinkedHashSet<FileInfo>(),
        "http://localhost", server);
    FakeResponseStream stream = new FakeResponseStream();

    client.runAllTests("1", stream, false);

    assertEquals("PASSED", stream.getResponse().getResponse());
  }

  public void testRunOneTest() throws Exception {
    MockServer server = new MockServer();

    server.expect("http://localhost/heartbeat?id=1", "OK");
    server.expect("http://localhost/fileSet?POST?{id=1, fileSet=[]}", "");
    server.expect("http://localhost/cmd?POST?{data={\"command\":\"runTests\","
        + "\"parameters\":[\"[\\\"testCase.testFoo\\\",\\\"testCase.testBar\\\"]\",\"false\"]}, id=1}",
        "");
    server.expect("http://localhost/cmd?id=1", "{\"response\":{\"response\":\"PASSED\","
        + "\"browser\":{\"name\":\"browser\"},\"error\":\"error2\",\"executionTime\":123},"
        + "\"last\":true}");
    JsTestDriverClient client = new JsTestDriverClientImpl(new CommandTaskFactory(
        new DefaultFileFilter(), null, new Provider<HeartBeatManager>() {
          public HeartBeatManager get() {
            return new HeartBeatManagerStub();
          }
        }), new LinkedHashSet<FileInfo>(),
        "http://localhost", server);
    FakeResponseStream stream = new FakeResponseStream();

    ArrayList<String> tests = new ArrayList<String>();

    tests.add("testCase.testFoo");
    tests.add("testCase.testBar");
    client.runTests("1", stream, tests, false);

    assertEquals("PASSED", stream.getResponse().getResponse());
  }

  /*
   * public void testRegisterCommand() throws Exception { MockServer server =
   * new MockServer();
   * 
   * server.expect("http://localhost/cmd?listBrowsers", "[" +
   * "{\"id\":1, \"name\":\"name0\", \"version\":\"ver0\", \"os\":\"os0\"}]");
   * server.expect("http://localhost/fileSet?POST?{id=1, fileSet=[]}", "");
   * server
   * .expect("http://localhost/cmd?POST?{data={\"command\":\"registerCommand\","
   * + "\"parameters\":[\"cool\",\"function() {}\"]}, id=1}", "");
   * server.expect("http://localhost/cmd?id=1",
   * "{\"response\":{\"response\":\"Command cool registered.\"," +
   * "\"browser\":{\"name\":\"browser\"},\"error\":\"error\",\"executionTime\":123},"
   * + "\"last\":true}"); JsPuppetClient client = new JsPuppetClientImpl(new
   * LinkedHashSet<FileInfo>(), "http://localhost", server);
   * FakeResponseStreamFactory factory = new FakeResponseStreamFactory();
   * FakeResponseStream stream = new FakeResponseStream();
   * 
   * factory.setResponseStream(stream); client.registerCommand("1", factory,
   * "cool", "function() {}");
   * 
   * assertEquals("Command cool registered.",
   * stream.getResponse().getResponse()); }
   */
}
