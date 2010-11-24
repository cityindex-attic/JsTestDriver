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

import static com.google.jstestdriver.server.handlers.CaptureHandler.RUNNER_TYPE;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.FileSource;
import com.google.jstestdriver.FileUploader;
import com.google.jstestdriver.FilesCache;
import com.google.jstestdriver.JsonCommand;
import com.google.jstestdriver.LoadedFiles;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.SlaveResourceService;
import com.google.jstestdriver.StreamMessage;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.JsonCommand.CommandType;
import com.google.jstestdriver.TestResult.Result;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.runner.RunnerType;
import com.google.jstestdriver.server.handlers.pages.Page;
import com.google.jstestdriver.server.handlers.pages.PageType;
import com.google.jstestdriver.server.handlers.pages.SlavePageRequest;
import com.google.jstestdriver.util.HtmlWriter;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class StandaloneRunnerHandler implements RequestHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(StandaloneRunnerHandler.class);

  private final Gson gson = new Gson();
  private final SlavePageRequest request;
  private final HttpServletResponse response;
  private final FilesCache cache;
  private final ConcurrentMap<SlaveBrowser, Thread> reportingThreads;
  private final TestResultGenerator testResultGenerator = new TestResultGenerator();

  private final Map<PageType, Page> pages;

  private final HandlerPathPrefix prefix;

  @Inject
  public StandaloneRunnerHandler(
      SlavePageRequest request,
      HttpServletResponse response,
      FilesCache cache,
      SlaveResourceService service,
      ConcurrentMap<SlaveBrowser, Thread> reportingThreads,
      Map<PageType, Page> pages,
      HandlerPathPrefix prefix) {
    this.request = request;
    this.response = response;
    this.cache = cache;
    this.reportingThreads = reportingThreads;
    this.pages = pages;
    this.prefix = prefix;
  }

  public void handleIt() throws IOException {
    final SlaveBrowser browser = request.getBrowser();
    if (browser == null) {
      LOGGER.warn("Invalid ID in: {}", request);
      // re-capture browser as standalone
      response.sendRedirect("/capture/" + RUNNER_TYPE + "/" + RunnerType.STANDALONE.toString() + "/timeout/3600000");
    } else {
      browser.heartBeat();
      response.setContentType(MimeTypes.TEXT_HTML_UTF_8);
      final HtmlWriter writer = new HtmlWriter(response.getWriter(), prefix);
      request.writeDTD(writer);
      pages.get(request.getPageType()).render(writer, request);
      // start test running
      service(browser);
    }
  }

  public void service(final SlaveBrowser slaveBrowser) {

    Set<String> filesToload = cache.getAllFileNames();
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
            StreamMessage message = slaveBrowser.getResponse();
            if (message != null) {
              final Response response = message.getResponse();
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