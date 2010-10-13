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
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.servlet.fileset.FileSetRequestHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class FileSetPostHandler implements RequestHandler {

  private final Gson gson = new Gson();
  private final HttpServletRequest request;
  private final HttpServletResponse response;

  private final CapturedBrowsers capturedBrowsers;

  private final List<FileSetRequestHandler<?>> handlers;

  @Inject
  public FileSetPostHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      CapturedBrowsers capturedBrowsers,
      List<FileSetRequestHandler<?>> handlers) {
    this.request = request;
    this.response = response;
    this.capturedBrowsers = capturedBrowsers;
    this.handlers = handlers;
  }

  public void handleIt() throws IOException {
    final FileSetRequestHandler<?> handler = handlerFromAction(request);
    response.getOutputStream().print(gson.toJson(handler.handle(browserFromId(request.getParameter("id")),
        fileInfosFromData(request.getParameter("data")))));
  }

  private Collection<FileInfo> fileInfosFromData(String data) {
    return gson.fromJson(data, new TypeToken<Collection<FileInfo>>() {}.getType());
  }

  private SlaveBrowser browserFromId(String id) {
    if (id == null) {
      return null;
    }
    return capturedBrowsers.getBrowser(id);
  }

  private FileSetRequestHandler<?> handlerFromAction(HttpServletRequest req) {
    final String action = req.getParameter("action");
    for (FileSetRequestHandler<?> handler : handlers) {
      if (handler.canHandle(action)) {
        return handler;
      }
    }
    throw new IllegalArgumentException("unknown action");
  }
}