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

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class SlaveResourceServlet extends HttpServlet {

  private final SlaveResourceService service;

  public SlaveResourceServlet(SlaveResourceService service) {
    this.service = service;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    service(req.getPathInfo(), resp.getOutputStream());
  }

  public void service(String pathInfo, OutputStream out) throws IOException {
    service.serve(stripId(pathInfo), out);
  }

  private final static Pattern PATHWITHOUTID = Pattern.compile("/.*?(/.*)$");

  public static String stripId(String path) {
    Matcher match = PATHWITHOUTID.matcher(path);

    if (match.find()) {
      return match.group(1);
    }
    throw new IllegalArgumentException(path);
  }
}
