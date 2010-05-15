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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.jstestdriver.JsonCommand.CommandType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
@Singleton
public class JsTestDriverClientImpl implements JsTestDriverClient {

  private final Gson gson = new Gson();

  private final CommandTaskFactory commandTaskFactory;
  private final Set<FileInfo> fileSet;
  private final String baseUrl;
  private final Server server;

  private final Boolean debug;

  @Inject
  public JsTestDriverClientImpl(CommandTaskFactory commandTaskFactory,
                                @Named("fileSet") Set<FileInfo> fileSet,
                                @Named("server") String baseUrl,
                                Server server,
                                @Named("debug") Boolean debug) {
    this.commandTaskFactory = commandTaskFactory;
    this.fileSet = fileSet;
    this.baseUrl = baseUrl;
    this.server = server;
    this.debug = debug;
  }

  public Collection<BrowserInfo> listBrowsers() {
    return gson.fromJson(server.fetch(baseUrl + "/cmd?listBrowsers"),
        new TypeToken<Collection<BrowserInfo>>() {}.getType());
  }

  public String getNextBrowserId() {
    return server.fetch(baseUrl + "/cmd?nextBrowserId");
  }

  private void sendCommand(String id, ResponseStream stream, String cmd, boolean uploadFiles) {
    Map<String, String> params = new LinkedHashMap<String, String>();

    params.put("data", cmd);
    params.put("id", id);
    CommandTask task = commandTaskFactory.getCommandTask(stream, fileSet, baseUrl,
        server, params, uploadFiles);

    task.run();
  }

  public void eval(String id, ResponseStream responseStream, String cmd) {
    List<String> parameters = new LinkedList<String>();

    parameters.add(cmd);
    JsonCommand jsonCmd = new JsonCommand(CommandType.EXECUTE, parameters);

    sendCommand(id, responseStream, gson.toJson(jsonCmd), false);
  }

  public void runAllTests(String id, ResponseStream responseStream, boolean captureConsole) {
    List<String> parameters = new LinkedList<String>();

    parameters.add(String.valueOf(captureConsole));
    parameters.add("false");
    parameters.add(debug ? "1":""); // The json serialization of 0,
    // false as strings evals to true on the js side. so, "" it is.
    JsonCommand cmd = new JsonCommand(CommandType.RUNALLTESTS, parameters);

    sendCommand(id, responseStream, gson.toJson(cmd), true);
  }

  public void reset(String id, ResponseStream responseStream) {
    JsonCommand cmd = new JsonCommand(CommandType.RESET, Collections.<String>emptyList());

    sendCommand(id, responseStream, gson.toJson(cmd), false);
  }

  public void runTests(String id, ResponseStream responseStream, List<String> tests,
      boolean captureConsole) {
    List<String> parameters = new LinkedList<String>();

    parameters.add(gson.toJson(tests));
    parameters.add(String.valueOf(captureConsole));
    parameters.add(debug ? "1":""); // The json serialization of 0,
    // false as strings evals to true on the js side. so, "" it is.
    JsonCommand cmd = new JsonCommand(CommandType.RUNTESTS, parameters);

    sendCommand(id, responseStream, gson.toJson(cmd), true);
  }

  public void dryRun(String id, ResponseStream responseStream) {
    JsonCommand cmd = new JsonCommand(CommandType.DRYRUN, Collections.<String>emptyList());

    sendCommand(id, responseStream, gson.toJson(cmd), true);
  }

  public void dryRunFor(String id, ResponseStream responseStream, List<String> expressions) {
    List<String> parameters = new LinkedList<String>();

    parameters.add(gson.toJson(expressions));
    JsonCommand cmd = new JsonCommand(CommandType.DRYRUNFOR, parameters);

    sendCommand(id, responseStream, gson.toJson(cmd), true);
  }
}
