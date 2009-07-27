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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserQueryResponseServlet extends HttpServlet {

  private static final long serialVersionUID = 995720234973219411L;

  private static final String NOOP = "noop";

  private final Gson gson = new Gson();

  private final CapturedBrowsers browsers;

  public BrowserQueryResponseServlet(CapturedBrowsers browsers) {
    this.browsers = browsers;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    service(req.getPathInfo().substring(1), req.getParameter("start"), req.getParameter("response"),
        req.getParameter("done"), resp.getWriter());
  }

  public void service(String id, String start, String response, String done, PrintWriter writer) {
    SlaveBrowser browser = browsers.getBrowser(id);

    if (browser != null) {
      browser.heartBeat();
      if (response != null && browser.isCommandRunning()) {
        Response res = gson.fromJson(response, Response.class);

        if (res.getResponse().contains("\"errorFiles\":")) {
          LoadedFiles loadedFiles = gson.fromJson(res.getResponse(), LoadedFiles.class);
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
        }
        boolean isLast = done != null;

        browser.addResponse(response, isLast);
        if (!isLast) {
          writer.print(NOOP);
          writer.flush();
          return;
        }
      }
      Command command = null;

      if (start != null) {
        browser.resetFileSet();
        Command commandRunning = browser.getCommandRunning();

        if (commandRunning != null) {
          JsonCommand jsonCommand =
            gson.fromJson(commandRunning.getCommand(), JsonCommand.class);

          if (jsonCommand.getCommand().equals(JsonCommand.CommandType.RESET.getCommand())) {
            command = browser.getLastDequeuedCommand();
          }
        }
      } else {
        command = browser.dequeueCommand();
      }
      writer.print(command != null ? command.getCommand() : NOOP);
    }
    writer.flush();
  }
}
