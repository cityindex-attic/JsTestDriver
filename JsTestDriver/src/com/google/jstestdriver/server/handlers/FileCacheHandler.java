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

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FileSetCacheStrategy;
import com.google.jstestdriver.requesthandlers.RequestHandler;

/**
 * Provides a filecache for clients to determine if a file has changed since they last ran.
 * @author corysmith@google.com (Cory Smith)
 */
class FileCacheHandler implements RequestHandler {
  
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final Gson gson;
  private final Set<FileInfo> currentFiles;
  private final FileSetCacheStrategy strategy;

  @Inject
  public FileCacheHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      Gson gson,
      Set<FileInfo> currentFiles,
      FileSetCacheStrategy strategy) {
    this.request = request;
    this.response = response;
    this.gson = gson;
    this.currentFiles = currentFiles;
    this.strategy = strategy;
  }

  public void handleIt() throws IOException {
    String fileSetString = request.getParameter("fileSet");
    Collection<FileInfo> newFiles =
      gson.fromJson(fileSetString,
          new TypeToken<Collection<FileInfo>>() {}.getType());
    response.getWriter().write(
        gson.toJson(strategy.createExpiredFileSet(newFiles, currentFiles)));
    // updates the currentFiles, as FileInfo hashes by name.
    for (FileInfo fileInfo : newFiles) {
      currentFiles.remove(fileInfo);
      currentFiles.add(fileInfo);
    }
  }
}