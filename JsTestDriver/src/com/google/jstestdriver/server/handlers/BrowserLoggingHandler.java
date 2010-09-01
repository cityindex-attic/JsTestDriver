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
package com.google.jstestdriver.server.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.jstestdriver.protocol.BrowserLog;
import com.google.jstestdriver.requesthandlers.RequestHandler;

import org.mortbay.jetty.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Allows the browser to log state directly to the application log.
 * @author corbinrsmith@google.com
 */
class BrowserLoggingHandler implements RequestHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(BrowserLoggingHandler.class);

  final private Gson gson = new Gson();

  private final HttpServletRequest request;
  private final HttpServletResponse response;

  @Inject
  public BrowserLoggingHandler(
      HttpServletRequest request,
      HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  public void handleIt() throws IOException {
    try{
      Collection<BrowserLog> logs =
        gson.fromJson(request.getParameter("logs"),
            new TypeToken<Collection<BrowserLog>>() {}.getType());
      for (BrowserLog log : logs) {
        Logger logger = LoggerFactory.getLogger(log.getSource());
        switch(log.getLevel()) {
          case 1:
            logger.trace(log.getMessage());
            break;
          case 2:
            logger.debug(log.getMessage());
            break;
          case 3:
            logger.info(log.getMessage());
            break;
          case 4:
            logger.warn(log.getMessage());
            break;
          case 5:
            logger.error(log.getMessage());
            break;
          default:
            throw new RuntimeException("Unknown logging level:" + log.getLevel());
        }
      }
      response.setStatus(HttpStatus.ORDINAL_200_OK);
    } catch (RuntimeException e) {
      response.setStatus(HttpStatus.ORDINAL_500_Internal_Server_Error);
      logger.error("Error during BrowserLog write.", e);
    } finally {
      response.getOutputStream().flush();
    }
  }
}