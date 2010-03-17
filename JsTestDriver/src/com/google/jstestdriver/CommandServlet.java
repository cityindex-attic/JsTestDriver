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
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.SlaveBrowser.CommandResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandServlet extends HttpServlet {

  private static final long serialVersionUID = 7210927357890630427L;
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandServlet.class);

  private final Gson gson = new Gson();

  private final CapturedBrowsers capturedBrowsers;
  private final URLTranslator urlTranslator;
  private final URLRewriter urlRewriter;
  private final ForwardingMapper forwardingMapper;

  public CommandServlet(CapturedBrowsers browsers, URLTranslator urlTranslator,
      URLRewriter urlRewriter, ForwardingMapper forwardingMapper) {
    this.capturedBrowsers = browsers;
    this.urlTranslator = urlTranslator;
    this.urlRewriter = urlRewriter;
    this.forwardingMapper = forwardingMapper;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (req.getParameter("listBrowsers") != null) {
      resp.getWriter().write(listBrowsers());
    } else {
      streamResponse(req.getParameter("id"), resp.getWriter());
    }
    resp.getWriter().flush();
  }

  private CommandResponse getResponse(SlaveBrowser browser) {
    CommandResponse cmdResponse = null;

    while (cmdResponse == null) {
      if (!browser.isAlive()) {
        SlaveBrowser deadBrowser = capturedBrowsers.getBrowser(browser.getId());
        capturedBrowsers.removeSlave(browser.getId());
        Response response = new Response();

        response.setBrowser(deadBrowser.getBrowserInfo());
        response.setResponse(
            gson.toJson(
                new BrowserPanic(deadBrowser.getBrowserInfo())));
        response.setType(BrowserPanic.TYPE_NAME);

        return new CommandResponse(response, true);
      }
      cmdResponse = browser.getResponse();
    }
    return cmdResponse;
  }

  private void substituteBrowserInfo(CommandResponse cmdResponse) {
    Response response = cmdResponse.getResponse();

      SlaveBrowser slaveBrowser =
          capturedBrowsers.getBrowser(response.getBrowser().getId().toString());
    if (slaveBrowser != null) {
      response.setBrowser(slaveBrowser.getBrowserInfo());
    } else {
      BrowserInfo nullBrowserInfo = new BrowserInfo();
      nullBrowserInfo.setId(response.getBrowser().getId());
      nullBrowserInfo.setName("unknown browser");
      nullBrowserInfo.setVersion("unknown version");
      nullBrowserInfo.setOs("unknown os");
      response.setBrowser(nullBrowserInfo);
    }
    cmdResponse.setResponse(response);
  }

  public void streamResponse(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    CommandResponse cmdResponse = getResponse(browser);

    substituteBrowserInfo(cmdResponse);
    StreamMessage response =
        new StreamMessage(cmdResponse.isLast(), cmdResponse.getResponse());

    writer.write(gson.toJson(response));
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    service(req.getParameter("id"), req.getParameter("data"));
  }

  public String listBrowsers() {
    return gson.toJson(capturedBrowsers.getBrowsers());
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
