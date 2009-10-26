// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.FileSetServlet.FileSetCacheStrategy;

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
  
  private static final FileSetCacheStrategy strategy = new FileSetCacheStrategy();
  Set<FileInfo> currentFiles = new HashSet<FileInfo>();
  private Gson gson = new Gson();

  @SuppressWarnings("unused") //@#$@% Checked exceptions.
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    String fileSetString = req.getParameter("fileSet");
    Collection<FileInfo> newFiles =
      gson.fromJson(fileSetString, new TypeToken<Collection<FileInfo>>() {}.getType());
    gson.toJson(strategy.createExpiredFileSet(newFiles, currentFiles), resp.getWriter());
    // updates the currentFiles, as FileInfo hashes by name.
    for (FileInfo fileInfo : newFiles) {
      currentFiles.remove(fileInfo);
      currentFiles.add(fileInfo);
    }
  }
}
