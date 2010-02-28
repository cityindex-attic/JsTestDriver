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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides a filecache for clients to determine if a file has changed since they last ran.
 * @author corysmith@google.com (Cory Smith)
 */
public class FileCacheServlet extends HttpServlet {

  private static final long serialVersionUID = -285793401666562019L;
  private final FileSetCacheStrategy strategy = new FileSetCacheStrategy();
  Set<FileInfo> currentFiles = new HashSet<FileInfo>();
  private Gson gson = new Gson();

  @Override
  protected void doPost(HttpServletRequest req,
                        HttpServletResponse resp) throws ServletException,
      IOException {
    String fileSetString = req.getParameter("fileSet");
    Collection<FileInfo> newFiles =
      gson.fromJson(fileSetString,
          new TypeToken<Collection<FileInfo>>() {}.getType());
    resp.getWriter().write(
        gson.toJson(strategy.createExpiredFileSet(newFiles, currentFiles)));
    // updates the currentFiles, as FileInfo hashes by name.
    for (FileInfo fileInfo : newFiles) {
      currentFiles.remove(fileInfo);
      currentFiles.add(fileInfo);
    }
  }
}
