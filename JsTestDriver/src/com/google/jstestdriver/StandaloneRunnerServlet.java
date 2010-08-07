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
import com.google.jstestdriver.SlaveBrowser.CommandResponse;
import com.google.jstestdriver.output.DefaultListener;
import com.google.jstestdriver.output.TestResultListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class StandaloneRunnerServlet extends HttpServlet  {
  
  private static final Logger LOGGER =
      LoggerFactory.getLogger(StandaloneRunnerServlet.class);

  private static final long serialVersionUID = 8525889760512657635L;

  private final Gson gson = new Gson();
  private final BrowserHunter browserHunter;
  private final FilesCache cache;
  private final StandaloneRunnerFilesFilter filter;
  private final SlaveResourceService service;
  private final ConcurrentMap<String, Thread> reportingThreads = new ConcurrentHashMap<String, Thread>();
  private final TestResultListener listener = new DefaultListener(System.out, true);
  private final TestResultGenerator testResultGenerator = new TestResultGenerator();

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

    if (req.getPathInfo().endsWith("StandaloneRunner.html")) {
      if (browserHunter.isBrowserCaptured(id)) {
        browserHunter.freeBrowser(id);
      }
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

  public void service(String userAgent, String path, final String id) {
    UserAgentParser parser = new UserAgentParser();

    parser.parse(userAgent);
    final SlaveBrowser slaveBrowser =
        browserHunter.captureBrowser(id, parser.getName(), parser.getVersion(), parser.getOs());
    Set<String> filesToload = filter.filter(path, cache);
    LinkedList<FileSource> filesSources = new LinkedList<FileSource>();

    for (String f : filesToload) {
      filesSources.add(new FileSource("/test/" + f, -1));
    }
    int size = filesSources.size();

    for (int i = 0; i < size; i += CommandTask.CHUNK_SIZE) {
      LinkedList<String> loadFilesParameters = new LinkedList<String>();
      List<FileSource> chunkedFileSources =
          filesSources.subList(i, Math.min(i + CommandTask.CHUNK_SIZE, size));

      loadFilesParameters.add(gson.toJson(chunkedFileSources));
      loadFilesParameters.add("true");
      slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.LOADTEST,
          loadFilesParameters)));
    }
    LinkedList<String> runAllTestsParameters = new LinkedList<String>();

    runAllTestsParameters.add("false");
    runAllTestsParameters.add("false");
    runAllTestsParameters.add("1");
    slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.RUNALLTESTS,
        runAllTestsParameters)));

    if (LOGGER.isDebugEnabled() && !reportingThreads.containsKey(id)) {
      final Thread thread = new Thread(new Runnable() {
        public void run() {
          try {
            while (true) {
              CommandResponse commandResponse = slaveBrowser.getResponse();
              if (commandResponse != null) {
                final Response response = commandResponse.getResponse();
                response.setBrowser(slaveBrowser.getBrowserInfo());
                switch(response.getResponseType()) {
                  case TEST_RESULT:
                    for (TestResult result : testResultGenerator.getTestResults(response)) {
                      listener.onTestComplete(result);
                    }
                    break;
                  case FILE_LOAD_RESULT:
                    LoadedFiles files = gson.fromJson(response.getResponse(),
                                                      response.getGsonType());
                    for (FileResult result : files.getLoadedFiles()) {
                      listener.onFileLoad(slaveBrowser.getBrowserInfo().toString(),
                                          result);
                    }
                    break;
                }
              }
              Thread.sleep(1000);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
      reportingThreads.put(id, thread);
      thread.setDaemon(true);
      thread.start();
    }
  }
}
