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
package com.google.jstestdriver.server.handlers;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.FileSource;
import com.google.jstestdriver.FileUploader;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.JsonCommand;
import com.google.jstestdriver.JsonCommand.CommandType;
import com.google.jstestdriver.LoadedFiles;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.SlaveBrowser.CommandResponse;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.StandaloneRunnerFilesFilter;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class StandaloneRunnerHandler implements RequestHandler {

  public static final String STANDALONE_RUNNER_HTML = "StandaloneRunner.html";

  private static final Logger LOGGER =
      LoggerFactory.getLogger(StandaloneRunnerHandler.class);

  private final Gson gson = new Gson();
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final CapturedBrowsers capturedBrowsers;
  private final FilesCache cache;
  private final StandaloneRunnerFilesFilter filter;
  private final SlaveResourceService service;
  private final ConcurrentMap<SlaveBrowser, Thread> reportingThreads;
  private final TestResultGenerator testResultGenerator = new TestResultGenerator();

  @Inject
  public StandaloneRunnerHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      CapturedBrowsers capturedBrowsers,
      FilesCache cache,
      StandaloneRunnerFilesFilter filter,
      SlaveResourceService service,
      ConcurrentMap<SlaveBrowser, Thread> reportingThreads) {
    this.request = request;
    this.response = response;
    this.capturedBrowsers = capturedBrowsers;
    this.cache = cache;
    this.filter = filter;
    this.service = service;
    this.reportingThreads = reportingThreads;
  }

  private final static Pattern ID = Pattern.compile("/(\\d+)/.*");

  public void handleIt() throws IOException {
    // TODO(rdionne): replace with SlaveResourceHandler#stripId()
    String path = SlaveResourceHandler.stripId(request.getRequestURI());
    try {
      final SlaveBrowser browser = getBrowserFromUrl(request.getPathInfo());
      // return the resources first.
      service.serve(path, response.getOutputStream());
      // start test running
      if (request.getPathInfo().endsWith(STANDALONE_RUNNER_HTML)) {
        service(browser, path);
      }
    } catch (IllegalArgumentException e) {
      LOGGER.warn("Invalid ID in: {}", request.getPathInfo());
      // re-capture browser as standalone
      response.sendRedirect("/capture/runnertype/STANDALONE/timeout/3600000");
    }
  }

  public SlaveBrowser getBrowserFromUrl(String pathInfo) {
    Matcher match = ID.matcher(pathInfo);
    if (match.find()) {
      final SlaveBrowser browser = capturedBrowsers.getBrowser(match.group(1));
      if (browser != null) {
        return browser;
      }
    }
    throw new IllegalArgumentException(pathInfo);
  }

  public void service(final SlaveBrowser slaveBrowser, final String path) {

    Set<String> filesToload = filter.filter(path, cache);
    LinkedList<FileSource> filesSources = new LinkedList<FileSource>();

    for (String f : filesToload) {
      filesSources.add(new FileSource("/test/" + f, -1));
    }
    final int size = filesSources.size();

    for (int i = 0; i < size; i += FileUploader.CHUNK_SIZE) {
      LinkedList<String> loadFilesParameters = new LinkedList<String>();
      List<FileSource> chunkedFileSources =
          filesSources.subList(i, Math.min(i + FileUploader.CHUNK_SIZE, size));

      loadFilesParameters.add(gson.toJson(chunkedFileSources));
      loadFilesParameters.add("true");
      slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.LOADTEST,
          loadFilesParameters)));
    }
    LinkedList<String> runAllTestsParameters = new LinkedList<String>();

    runAllTestsParameters.add("false");
    runAllTestsParameters.add("false");
    runAllTestsParameters.add("0");
    slaveBrowser.createCommand(gson.toJson(new JsonCommand(CommandType.RUNALLTESTS,
        runAllTestsParameters)));

    if (LOGGER.isDebugEnabled() && !reportingThreads.containsKey(slaveBrowser)) {
      final Thread thread = new Thread(new Runnable() {
        public void run() {
          String runnerId = slaveBrowser.getBrowserInfo().toUniqueString();
          final long testStart = System.currentTimeMillis();
          int loaded = 0;
          while (true) {
            CommandResponse commandResponse = slaveBrowser.getResponse();
            if (commandResponse != null) {
              final Response response = commandResponse.getResponse();
              response.setBrowser(slaveBrowser.getBrowserInfo());

              switch (response.getResponseType()) {
                case TEST_RESULT:
                  final Collection<TestResult> testResults =
                      testResultGenerator.getTestResults(response);
                  boolean failed = false;
                  for (TestResult result : testResults) {
                    if (result.getResult() != Result.passed) {
                      failed = true;
                      LOGGER.trace("{}: {} {}.{}: \n{}",
                          new Object[] {runnerId, result.getResult(), result.getTestCaseName(),
                              result.getTestName(), result.getMessage()});
                    } else {
                      LOGGER.trace("{}: passed {}{}",
                          new Object[] {runnerId, result.getTestCaseName(), result.getTestName()});
                    }
                  }
                  LOGGER.debug("{}: result {} {}s", new Object[] {runnerId, !failed,
                      ((System.currentTimeMillis() - testStart) / 1000)});
                  break;
                case FILE_LOAD_RESULT:
                  LoadedFiles files =
                      gson.fromJson(response.getResponse(), response.getGsonType());
                  for (FileResult result : files.getLoadedFiles()) {
                    if (result.isSuccess()) {
                      loaded++;
                    } else {
                      LOGGER.debug("{}: failed to load {}",
                          new Object[] {runnerId, result.getFileSource().getFileSrc()});
                    }
                  }
                  LOGGER.debug("{}: loaded {} files of {} @ {}s", new Object[] {runnerId,
                      loaded, size, ((System.currentTimeMillis() - testStart) / 1000)});
                  break;
                case LOG:
                  LOGGER.debug("{}: test time {}s {}",
                      new Object[] {runnerId, ((System.currentTimeMillis() - testStart) / 1000),
                          response.getResponse()});
                  break;
              }
            }
          }
        }
      });
      reportingThreads.put(slaveBrowser, thread);
      thread.setDaemon(true);
      thread.start();
    }
  }
}