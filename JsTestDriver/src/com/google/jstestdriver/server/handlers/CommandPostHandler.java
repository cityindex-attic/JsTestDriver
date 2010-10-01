// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileSource;
import com.google.jstestdriver.JsonCommand;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
class CommandPostHandler implements RequestHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      CommandPostHandler.class);

  private final HttpServletRequest request;
  private final Gson gson;
  private final CapturedBrowsers capturedBrowsers;

  @Inject
  public CommandPostHandler(
      HttpServletRequest request,
      Gson gson,
      CapturedBrowsers capturedBrowsers) {
    this.request = request;
    this.gson = gson;
    this.capturedBrowsers = capturedBrowsers;
  }

  public void handleIt() throws IOException {
    service(request.getParameter("id"), request.getParameter("data"));
  }

  public void service(String id, String data) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);

    data = translateUrls(data);
    browser.createCommand(data);
  }

  private String translateUrls(String data) {
    JsonCommand command = gson.fromJson(data, JsonCommand.class);

    if (command.getCommand().equals(JsonCommand.CommandType.LOADTEST.getCommand())) {
      List<String> parameters = command.getParameters();
      String fileSourcesList = parameters.get(0);
      List<FileSource> fileSources =
          gson.fromJson(fileSourcesList, new TypeToken<List<FileSource>>() {}.getType());
      parameters.remove(0);
      parameters.add(0, gson.toJson(fileSources));
      return gson.toJson(command);
    }
    return data;
  }

}