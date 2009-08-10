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

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;

/**
 * Tests for {@link com.google.jstestdriver.IDEPluginActionBuilder}
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IDEPluginActionBuilderTest extends TestCase {
  File tmpDir;

  @Override
  protected void setUp() throws Exception {
    tmpDir = File.createTempFile("test", "JsTestDriver");
    tmpDir.delete();
    tmpDir.mkdir();
    tmpDir.deleteOnExit();
  }

  public void testExample() throws Exception {
    Reader configReader = new StringReader("load:\n- blash.js\nserver:\n -http://foo");
    ConfigurationParser configParser = new ConfigurationParser(tmpDir, configReader);

    IDEPluginActionBuilder builder = new IDEPluginActionBuilder(configParser, "http://address",
        new ResponseStreamFactoryStub());
    builder.install(new AbstractModule(){
                     @Override
                     protected void configure() {
                       bind(Server.class).to(MyServer.class);
                     }
                   });
    builder.addAllTests();
    ActionRunner runner = builder.build();

    runner.runActions();
  }

  private final class ResponseStreamFactoryStub implements ResponseStreamFactory {
    public ResponseStream getDryRunActionResponseStream() {
      return null;
    }

    public ResponseStream getEvalActionResponseStream() {
      return null;
    }

    public ResponseStream getResetActionResponseStream() {
      return null;
    }

    public ResponseStream getRunTestsActionResponseStream(String browserId) {
      return null;
    }
  }

  static class MyServer implements Server {

    private Gson gson = new Gson();

    public String fetch(String url) {
      BrowserInfo browserInfo = new BrowserInfo();

      browserInfo.setId(1);
      browserInfo.setName("name");
      browserInfo.setOs("os");
      browserInfo.setVersion("version");
      return gson.toJson(Arrays.asList(browserInfo));
    }

    public String post(String url, Map<String, String> params) {
      return null;
    }

    public String startSession(String baseUrl, String id) {
      return null;
    }

    public void stopSession(String baseUrl, String id, String sessionId) {
    }
  }
}
