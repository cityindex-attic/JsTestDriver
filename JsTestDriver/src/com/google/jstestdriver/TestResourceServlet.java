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
package com.google.jstestdriver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class TestResourceServlet extends HttpServlet {

  private static final long serialVersionUID = 3689888178162608893L;

  private static final String TIME_IN_THE_PAST = "Sat, 22 Sep 1984 00:00:00 GMT";
  private final FilesCache filesCache;

  public TestResourceServlet(FilesCache filesCache) {
    this.filesCache = filesCache;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setHeader("Pragma", "no-cache");
    resp.setHeader("Cache-Control", "private, no-cache, no-store, max-age=0, must-revalidate");
    resp.setHeader("Expires", TIME_IN_THE_PAST);
    resp.setHeader("Content-Type", "text/plain");
    service(req.getPathInfo().substring(1) /* remove the first / */, resp.getWriter());
  }

  public void service(String fileName, PrintWriter writer) {
    String data = filesCache.getFileContent(fileName);

    if (data != null) {
      writer.write(data);
    }
    writer.flush();
  }
}
