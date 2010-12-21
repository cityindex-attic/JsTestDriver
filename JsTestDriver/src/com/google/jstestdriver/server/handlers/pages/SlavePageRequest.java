/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.server.handlers.pages;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.runner.RunnerType;
import com.google.jstestdriver.server.handlers.CaptureHandler;
import com.google.jstestdriver.util.HtmlWriter;

/**
 * Wrapper for the HttpServletRequest object with Page specific logic.
 *
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class SlavePageRequest {

  public static final String PAGE = "page";
  public static final String ID = "id";
  public static final String MODE = "mode";
  public static final String TIMEOUT = "timeout";
  public static final String UPLOAD_SIZE = "upload_size";

  private static final Logger logger =
      LoggerFactory.getLogger(SlavePageRequest.class);

  private final Map<String, String> parameters;
  private final HttpServletRequest request;
  private final HandlerPathPrefix prefix;
  private final CapturedBrowsers browsers;

  public SlavePageRequest(Map<String, String> parameters,
      HttpServletRequest request,
      HandlerPathPrefix prefix,
      CapturedBrowsers browsers) {
    this.parameters = parameters;
    this.request = request;
    this.prefix = prefix;
    this.browsers = browsers;
  }

  public HtmlWriter writeDTD(HtmlWriter writer) throws IOException {
    if ("strict".equals(parameters.get(MODE))) {
      writer.writeStrictDtd();
    } else {
      writer.writeQuirksDtd();
    }
    return writer;
  }
  
  public String createCaptureUrl(RunnerType type) {
    return prefix.prefixPath(String.format("/capture/%s/%s/%s/%s/%s/%s",
        CaptureHandler.RUNNER_TYPE,
        type,
        MODE,
        parameters.get(MODE),
        UPLOAD_SIZE,
        parameters.get(UPLOAD_SIZE)));
  }

  public String createCaptureUrl() {
    return createCaptureUrl(
        RunnerType.valueOf(
            parameters.get(CaptureHandler.RUNNER_TYPE).toUpperCase()));
  }

  public String createPageUrl(PageType page) {
    String url = prefix.prefixPath(String.format("/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s",
        "slave",
        ID,
        parameters.get(ID),
        CaptureHandler.RUNNER_TYPE,
        parameters.get(CaptureHandler.RUNNER_TYPE),
        MODE,
        parameters.get(MODE),
        PAGE,
        page,
        UPLOAD_SIZE,
        parameters.get(UPLOAD_SIZE)));
    logger.trace("creating new url: {} for {}", url, page);
    return url;
  }
  
  public SlaveBrowser getBrowser() {
    String id = parameters.get(ID);
    if (id == null) {
      return null;
    }
    return browsers.getBrowser(id);
  }
  
  public PageType getPageType() {
    return PageType.valueOf(parameters.get(PAGE).toUpperCase());
  }

  public String getUserAgent() {
    return request.getHeader("User-Agent");
  }

  @Override
  public String toString() {
    return "SlavePageRequest [parameters=" + parameters + "]";
  }
}
