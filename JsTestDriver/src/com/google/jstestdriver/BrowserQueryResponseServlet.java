/*
 * Copyright 2008 Google Inc.
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
import com.google.jstestdriver.protocol.BrowserStreamAcknowledged;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserQueryResponseServlet extends HttpServlet {
  private static final Logger logger =
      LoggerFactory.getLogger(BrowserQueryResponseServlet.class);

  private static final long serialVersionUID = 995720234973219411L;

  /** for something completely unrelated see: http://noop.googlecode.com/ */
  private static final String NOOP = "noop";

  private final Gson gson = new Gson();

  private final CapturedBrowsers browsers;
  private final URLTranslator urlTranslator;
  private final ForwardingMapper forwardingMapper;
  // TODO(corysmith): factor out a streaming session class. 
  private final ConcurrentMap<SlaveBrowser, List<String>> streamedResponses =
    new ConcurrentHashMap<SlaveBrowser, List<String>>();

  public BrowserQueryResponseServlet(CapturedBrowsers browsers, URLTranslator urlTranslator,
      ForwardingMapper forwardingMapper) {
    this.browsers = browsers;
    this.urlTranslator = urlTranslator;
    this.forwardingMapper = forwardingMapper;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    logger.trace("Browser Query Post:\n\tpath:{}\n\tresponse:{}\n\tdone:{}\n\tresponseId:{}",
        new Object[] {
          req.getPathInfo().substring(1),
          req.getParameter("response"),
          req.getParameter("done"),
          req.getParameter("responseId")
        });
    service(req.getPathInfo().substring(1),
            req.getParameter("response"),
            req.getParameter("done"),
            req.getParameter("responseId"),
            resp.getWriter());
  }

  public void service(String id,
                      String response,
                      String done,
                      String responseId,
                      PrintWriter writer) {
    SlaveBrowser browser = browsers.getBrowser(id);

    if (browser != null) {
      boolean isLast = Boolean.parseBoolean(done);
      serviceBrowser(response, isLast, responseId, writer, browser);
    } else {
      logger.warn("Unknown browser {}", id);
    }
    writer.flush();
  }

  private void serviceBrowser(String response, Boolean done, String responseId, PrintWriter writer,
      SlaveBrowser browser) {
    addResponseId(responseId, browser);
    browser.heartBeat();
    Command command = null;
    if (response != null && !"null".equals(response) && browser.isCommandRunning()) {
      Response res = gson.fromJson(response, Response.class);
      // TODO (corysmith): Replace this with polymorphism,
      // using the response type to create dispoable actions.
      switch (res.getResponseType()) {
        case FILE_LOAD_RESULT:
          LoadedFiles loadedFiles = gson.fromJson(res.getResponse(), res.getGsonType());
          Collection<FileResult> allLoadedFiles = loadedFiles.getLoadedFiles();
          if (!allLoadedFiles.isEmpty()) {
            LinkedHashSet<FileInfo> fileInfos = new LinkedHashSet<FileInfo>();
            Collection<FileSource> errorFiles = new LinkedHashSet<FileSource>();

            for (FileResult fileResult : allLoadedFiles) {
              FileSource fileSource = fileResult.getFileSource();

              if (fileResult.isSuccess()) {
                fileInfos.add(new FileInfo(fileSource.getBasePath(), fileSource.getTimestamp(),
                    false, false, null));
              } else {
                errorFiles.add(fileSource);
              }
            }
            browser.addFiles(fileInfos);
            if (errorFiles.size() > 0) {
              browser.removeFiles(errorFiles);
            }
          }
          break;
        // reset the browsers fileset.
        case RESET_RESULT:
          Command commandRunning = browser.getCommandRunning();

          if (commandRunning != null) {
            JsonCommand jsonCommand = gson.fromJson(commandRunning.getCommand(), JsonCommand.class);

            if (jsonCommand.getCommand().equals(JsonCommand.CommandType.RESET.getCommand())) {
              command = browser.getLastDequeuedCommand();
            }
          }
        //$FALL-THROUGH$
        case BROWSER_READY:
          browser.resetFileSet();
          urlTranslator.clear();
          forwardingMapper.clear();
          break;
      }
      logger.trace("Received:\n done: {} \n res:\n {}\n", new Object[] {done, res});
      browser.addResponse(res, done);
    }
    logger.debug("Got responseId: {} is done? {}", responseId, done);
    // TODO(corysmith): Refactoring the streaming into a separate layer.
    if (!done) { // we are still streaming, so we respond with the streaming
                 // acknowledge.
      // this is independent of receiving an actual response.
      writer.print(gson.toJson(new BrowserStreamAcknowledged(streamedResponses.get(browser))));
      writer.flush();
      return;
    } else {
      streamedResponses.clear();
    }
    if(command == null) {
     command = browser.dequeueCommand();
    }
    writer.print(command != null ? command.getCommand() : NOOP);
  }

  private void addResponseId(String responseId, SlaveBrowser browser) {
    if (!streamedResponses.containsKey(browser)) {
      streamedResponses.put(browser, new CopyOnWriteArrayList<String>());
    }
    if (responseId == null) {
      return;
    }
    streamedResponses.get(browser).add(responseId);
  }
}
