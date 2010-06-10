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
import java.io.FileWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.util.Providers;
import com.google.jstestdriver.output.MultiTestResultListener;
import com.google.jstestdriver.output.TestResultListener;
import com.google.jstestdriver.output.XmlPrinter;
import com.google.jstestdriver.output.XmlPrinterImpl;

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
    File file = new File(tmpDir, "blash.js");
    new FileWriter(file).append("a = 1;").close();
  }

  public void testExample() throws Exception {
    Reader configReader = new StringReader("load:\n- blash.js\nserver:\n -http://foo");
    ConfigurationParser configParser =
        new ConfigurationParser(tmpDir, configReader, new DefaultPathRewriter());

    IDEPluginActionBuilder builder = new IDEPluginActionBuilder(configParser, "http://localhost:42242",
        new ResponseStreamFactoryStub(), new File("."));
    builder.install(new AbstractModule(){
                     @Override
                     protected void configure() {
                       bind(Server.class).to(MyServer.class);
                       bind(XmlPrinter.class).to(XmlPrinterImpl.class);
                       bind(TestResultListener.class).toInstance(
                           new MultiTestResultListener(Collections.<TestResultListener>emptySet()));
                     }
                   });
    builder.addAllTests();
    ActionRunner runner = builder.build();
    assertNotNull(runner);
    //runner.runActions();
    // TODO(corysmith): fix the fact this test is all integration.
    // Without a server. :P
  }

  public void testInstallModuleOverwritingResolvedJustInTimeInjection() throws Exception {
    Reader configReader = new StringReader("load:\n- blash.js\nserver:\n -http://foo");
    ConfigurationParser configParser =
        new ConfigurationParser(tmpDir, configReader, new DefaultPathRewriter());

    IDEPluginActionBuilder builder = new IDEPluginActionBuilder(configParser, "http://address",
        new ResponseStreamFactoryStub(), new File("."));
    builder.install(new AbstractModule(){
                     @Override
                     protected void configure() {
                       bind(Server.class).to(MyServer.class);
                       bind(XmlPrinter.class).to(XmlPrinterImpl.class);
                       bind(TestResultListener.class)
                           .toProvider(Providers.<TestResultListener>of(null));
                     }
                   });
    builder.addAllTests();
    builder.build();
    configReader.reset();

    builder.install(new AbstractModule(){
      @Override
      protected void configure() {
        bind(FileLoader.class).to(MyFileLoader.class);
      }
    });
    builder.build();
  }

  private final class ResponseStreamFactoryStub implements ResponseStreamFactory {
    private final class NullResponseStream implements ResponseStream {
      public void stream(Response response) {
        // TODO Auto-generated method stub

      }

      public void finish() {
        // TODO Auto-generated method stub

      }
    }

    public ResponseStream getDryRunActionResponseStream() {
      return new NullResponseStream();
    }

    public ResponseStream getEvalActionResponseStream() {
      return new NullResponseStream();
    }

    public ResponseStream getResetActionResponseStream() {
      return new NullResponseStream();
    }

    public ResponseStream getRunTestsActionResponseStream(String browserId) {
      return new NullResponseStream();
    }
  }

  static class MyFileLoader implements FileLoader {

    public List<FileInfo> loadFiles(
    		Collection<FileInfo> filesToLoad, File basePath, boolean shouldReset) {
      return new LinkedList<FileInfo>(filesToLoad);
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
