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
package com.google.jstestdriver.server.handlers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.jstestdriver.requesthandlers.RequestHandler;

public class QuitHandler implements RequestHandler {
  private final HttpServletResponse response;
  @Inject
  public QuitHandler(HttpServletResponse response) {
    this.response = response;
  }
  public void handleIt() throws IOException {
    PrintWriter writer = response.getWriter();
    writer.append("exiting");
    writer.flush();
    // TODO(corysmith): figure out how to stop with using System.exit
    System.exit(0);
  }
}
