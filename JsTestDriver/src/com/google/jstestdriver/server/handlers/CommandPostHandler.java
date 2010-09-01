// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.FileSource;
import com.google.jstestdriver.ForwardingMapper;
import com.google.jstestdriver.JsonCommand;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.URLRewriter;
import com.google.jstestdriver.URLTranslator;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
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
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private final ForwardingMapper forwardingMapper;

  @Inject
  public CommandPostHandler(
      HttpServletRequest request,
      Gson gson,
      CapturedBrowsers capturedBrowsers,
      URLTranslator urlTranslator,
      URLRewriter urlRewriter,
      ForwardingMapper forwardingMapper) {
    this.request = request;
    this.gson = gson;
    this.capturedBrowsers = capturedBrowsers;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.forwardingMapper = forwardingMapper;
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

      for (FileSource fileSource : fileSources) {
        String fileSrc = fileSource.getFileSrc();

        if (fileSrc.startsWith("http://") || fileSrc.startsWith("https://")) {
          String url = urlRewriter.rewrite(fileSource.getFileSrc());

          if (url.startsWith("http://") || url.startsWith("https://")) {
            String translation = urlTranslator.getTranslation(url);

            if (translation == null) {
              try {
                urlTranslator.translate(url);
                translation = urlTranslator.getTranslation(url);
                forwardingMapper.addForwardingMapping(translation, url);
              } catch (MalformedURLException e) {
                LOGGER.warn("Could not translate URL: " + url
                    + " fallback to default URL, things will probably start to act weird...", e);
                translation = url;
              }
            }
            fileSource.setBasePath(url);
            fileSource.setFileSource(translation);
          } else {
            fileSource.setBasePath(url);
            fileSource.setFileSource(url);
          }
        }
      }
      parameters.remove(0);
      parameters.add(0, gson.toJson(fileSources));
      return gson.toJson(command);
    }
    return data;
  }

}