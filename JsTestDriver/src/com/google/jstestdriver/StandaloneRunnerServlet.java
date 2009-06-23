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
import com.google.jstestdriver.JsonCommand.CommandType;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class StandaloneRunnerServlet  extends HttpServlet  {

  private final Gson gson = new Gson();
  private final BrowserHunter browserHunter;
  private final FilesCache cache;
  private final StandaloneRunnerFilesFilter filter;
  private final SlaveResourceService service;

  public StandaloneRunnerServlet(BrowserHunter browserHunter, FilesCache cache,
      StandaloneRunnerFilesFilter filter, SlaveResourceService service) {
    this.browserHunter = browserHunter;
    this.cache = cache;
    this.filter = filter;
    this.service = service;
  }

  private final static Pattern ID = Pattern.compile("/(\\d+)/.*");

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String path = SlaveResourceServlet.stripId(req.getPathInfo());
    String id = getIdFromUrl(req.getPathInfo());

    if (!browserHunter.isBrowserCaptured(id)) {
      service(req.getHeader("User-Agent"), path, id);
    }
    service.serve(path, resp.getOutputStream());
  }

  public static String getIdFromUrl(String pathInfo) {
    Matcher match = ID.matcher(pathInfo);

    if (match.find()) {
      return match.group(1);
    }
    throw new IllegalArgumentException(pathInfo);
  }

  public void service(String userAgent, String path, String id) {
    UserAgentParser parser = new UserAgentParser();

    parser.parse(userAgent);
    SlaveBrowser slaveBrowser =
        browserHunter.captureBrowser(id, parser.getName(), parser.getVersion(), parser.getOs());
    LinkedList<String> loadFilesParameters = new LinkedList<String>();
    LinkedList<String> runAllTestsParameters = new LinkedList<String>();
    LinkedList<FileSource> filesSources = new LinkedList<FileSource>();
    Set<String> filesToload = filter.filter(path, cache);

    for (String f : filesToload) {
      filesSources.add(new FileSource("/test/" + f, -1));
    }
    loadFilesParameters.add(gson.toJson(filesSources));
    loadFilesParameters.add("true");
    slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.LOADTEST,
        loadFilesParameters)));
    runAllTestsParameters.add("false");
    runAllTestsParameters.add("true");
    slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.RUNALLTESTS,
        runAllTestsParameters)));
  }
}
