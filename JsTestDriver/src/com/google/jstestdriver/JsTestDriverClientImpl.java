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
import com.google.jstestdriver.JsonCommand.CommandType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class JsTestDriverClientImpl implements JsTestDriverClient {
  
  private static final List<String> EMPTY_ARRAYLIST = new ArrayList<String>();

  private final Gson gson = new Gson();
  private final Set<FileInfo> fileSet;
  private final String baseUrl;
  private final Server server;

  public JsTestDriverClientImpl(Set<FileInfo> fileSet, String baseUrl, Server server) {
    this.fileSet = fileSet;
    this.baseUrl = baseUrl;
    this.server = server;
  }

  public Collection<BrowserInfo> listBrowsers() {
    return gson.fromJson(server.fetch(baseUrl + "/cmd?listBrowsers"),
        new TypeToken<Collection<BrowserInfo>>() {}.getType());
  }

  private void sendCommand(String id, ResponseStream stream, String cmd) {
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", cmd);
    params.put("id", id);
    new CommandTask(stream, fileSet, baseUrl, server, params).run();
  }

  public void eval(String id, ResponseStreamFactory factory, String cmd) {
    List<String> parameters = new ArrayList<String>();

    parameters.add(cmd);
    JsonCommand jsonCmd = new JsonCommand(CommandType.EXECUTE, parameters);

    sendCommand(id, factory.getEvalActionResponseStream(), gson.toJson(jsonCmd));
  }

  public void runAllTests(String id, ResponseStreamFactory factory, boolean captureConsole) {
    List<String> parameters = new ArrayList<String>();

    parameters.add(String.valueOf(captureConsole));
    JsonCommand cmd = new JsonCommand(CommandType.RUNALLTESTS, parameters);

    sendCommand(id, factory.getRunTestsActionResponseStream(), gson.toJson(cmd));
  }

  public void reset(String id, ResponseStreamFactory factory) {
    JsonCommand cmd = new JsonCommand(CommandType.RESET, EMPTY_ARRAYLIST);

    sendCommand(id, factory.getResetActionResponseStream(), gson.toJson(cmd));
  }

  public void registerCommand(String id, ResponseStreamFactory factory, String name,
      String function) {
    List<String> parameters = new ArrayList<String>();

    parameters.add(name);
    parameters.add(function);
    JsonCommand cmd = new JsonCommand(CommandType.REGISTERCOMMAND, parameters);
    CountDownLatch latch = new CountDownLatch(1);

    sendCommand(id, null, gson.toJson(cmd));
  }

  public void runTests(String id, ResponseStreamFactory factory, List<TestCase> tests,
      boolean captureConsole) {
    List<String> parameters = new ArrayList<String>();

    parameters.add(gson.toJson(tests));
    parameters.add(String.valueOf(captureConsole));
    JsonCommand cmd = new JsonCommand(CommandType.RUNTESTS, parameters);

    sendCommand(id, factory.getRunTestsActionResponseStream(), gson.toJson(cmd));
  }
}
