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
import com.google.jstestdriver.JsonCommand.CommandType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles the communication of a command to the JsTestDriverServer from the
 * JsTestDriverClient.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTask {

  private static final List<String> EMPTY_ARRAYLIST = new ArrayList<String>();
  private static final long WAIT_INTERVAL = 500L;
  public static final int CHUNK_SIZE = 150;

  private final Gson gson = new Gson();

  private final JsTestDriverFileFilter filter;
  private final ResponseStream stream;
  private final Set<FileInfo> fileSet;
  private final String baseUrl;
  private final Server server;
  private final Map<String, String> params;
  private final HeartBeatManager heartBeatManager;
  private final FileLoader fileLoader;
  private final boolean upload;

  public CommandTask(JsTestDriverFileFilter filter, ResponseStream stream, Set<FileInfo> fileSet,
      String baseUrl, Server server, Map<String, String> params, HeartBeatManager heartBeatManager,
      FileLoader fileLoader, boolean upload) {
    this.filter = filter;
    this.stream = stream;
    this.fileSet = fileSet;
    this.baseUrl = baseUrl;
    this.server = server;
    this.params = params;
    this.heartBeatManager = heartBeatManager;
    this.fileLoader = fileLoader;
    this.upload = upload;
  }

  private String startSession() {
    String browserId = params.get("id");
    String sessionId = server.startSession(baseUrl, browserId);

    if (sessionId.equals("FAILED")) {
      while (sessionId.equals("FAILED")) {
        try {
          Thread.sleep(WAIT_INTERVAL);
        } catch (InterruptedException e) {
          System.err.println("Could not create session for browser: " + browserId);
          return "";
        }
        sessionId = server.startSession(baseUrl, browserId);
      }
    }
    return sessionId;
  }

  private void stopSession(String sessionId) {
    server.stopSession(baseUrl, params.get("id"), sessionId);
  }

  private boolean isBrowserAlive() {
    String alive = server.fetch(baseUrl + "/heartbeat?id=" + params.get("id"));

    if (!alive.equals("OK")) {
      System.err.println("The browser " + params.get("id") + " is not available anymore, "
          + "you might want to re-capture it");
      return false;
    }
    return true;
  }

  // TODO(corysmith): remove this function once FileInfo is used exclusively.
  // Hate static crap.
  public static FileSource fileInfoToFileSource(FileInfo info) {
    if (info.getFileName().startsWith("http://")) {
      return new FileSource(info.getFileName(), info.getTimestamp());
    }
    return new FileSource("/test/" + info.getFileName(), info.getTimestamp());
  }

  private void uploadFileSet() {
    Map<String, String> fileSetParams = new LinkedHashMap<String, String>();

    fileSetParams.put("id", params.get("id"));
    fileSetParams.put("fileSet", gson.toJson(fileSet));
    String postResult = server.post(baseUrl + "/fileSet", fileSetParams);

    if (postResult.length() > 0) {
      Collection<FileInfo> filesToUpload =
          gson.fromJson(postResult, new TypeToken<Collection<FileInfo>>() {}.getType());
      boolean shouldReset = sameFiles(filesToUpload, fileSet);
      Set<FileInfo> finalFilesToUpload = new LinkedHashSet<FileInfo>();

      if (shouldReset) {
        JsonCommand cmd = new JsonCommand(CommandType.RESET, EMPTY_ARRAYLIST);
        Map<String, String> resetParams = new LinkedHashMap<String, String>();

        resetParams.put("id", params.get("id"));
        resetParams.put("data", gson.toJson(cmd));
        server.post(baseUrl + "/cmd", resetParams);
        server.fetch(baseUrl + "/cmd?id=" + params.get("id"));
        finalFilesToUpload.addAll(filesToUpload);
      } else {
        for (FileInfo file : filesToUpload) {
          finalFilesToUpload.addAll(findDependencies(file));
        }
      }
      List<FileInfo> loadedfiles = fileLoader.loadFiles(finalFilesToUpload, shouldReset);
      Map<String, String> uploadFileParams = new LinkedHashMap<String, String>();

      uploadFileParams.put("id", params.get("id"));
      uploadFileParams.put("data", gson.toJson(loadedfiles));
      server.post(baseUrl + "/fileSet", uploadFileParams);
      List<FileSource> filesSrc = new LinkedList<FileSource>(filterFilesToLoad(finalFilesToUpload));
      int numberOfFilesToLoad = filesSrc.size();

      for (int i = 0; i < numberOfFilesToLoad; i += CHUNK_SIZE) {
        int chunkEndIndex = Math.min(i + CHUNK_SIZE, numberOfFilesToLoad);
        List<String> loadParameters = new LinkedList<String>();
        List<FileSource> filesToLoad = filesSrc.subList(i, chunkEndIndex);

        loadParameters.add(gson.toJson(filesToLoad));
        loadParameters.add("false");
        JsonCommand cmd = new JsonCommand(CommandType.LOADTEST, loadParameters);
        Map<String, String> loadFileParams = new LinkedHashMap<String, String>();

        loadFileParams.put("id", params.get("id"));
        loadFileParams.put("data", gson.toJson(cmd));
        server.post(baseUrl + "/cmd", loadFileParams);
        String jsonResponse = server.fetch(baseUrl + "/cmd?id=" + params.get("id"));
        StreamMessage message = gson.fromJson(jsonResponse, StreamMessage.class);
        Response response = message.getResponse();
        LoadedFiles loadedFiles = gson.fromJson(response.getResponse(), LoadedFiles.class);

        if (loadedFiles.getLoadedFiles().isEmpty()) {
          System.err.println("No files were loaded.");
        } else {
          if (loadedFiles.hasError()) {
            for (FileResult fileResult : loadedFiles.getLoadedFiles()) {
              if (!fileResult.isSuccess()) {
                System.err.println(fileResult.getMessage());
              }
            }
          }
        }
      }
    }
  }

  private Collection<FileInfo> findDependencies(FileInfo file) {
    List<FileInfo> deps = new LinkedList<FileInfo>();

    // TODO(jeremiele): replace filter with a plugin
    for (String fileName : filter.resolveFilesDeps(file.getFileName())) {
      deps.add(new FileInfo(fileName, new File(fileName).lastModified(), false, false, null));
    }
    return deps;
  }

  private List<FileSource> filterFilesToLoad(Collection<FileInfo> fileInfos) {
    List<FileSource> filteredFileSources = new LinkedList<FileSource>();

    for (FileInfo fileInfo : fileInfos) {
      if (!fileInfo.isServeOnly()) {
        filteredFileSources.add(fileInfoToFileSource(fileInfo));
      }
    }
    return filteredFileSources;
  }

  public void run() {
    heartBeatManager.startTimer();
    String browserId = params.get("id");
    String sessionId = null;

    try {
      sessionId = startSession();

      if (!sessionId.equals("")) {
        heartBeatManager.startHeartBeat(baseUrl, browserId, sessionId);
      }
      if (!isBrowserAlive()) {
        return;
      }
      if (upload) {
        uploadFileSet();
      }
      server.post(baseUrl + "/cmd", params);
      StreamMessage streamMessage = null;

      do {
        String response = server.fetch(baseUrl + "/cmd?id=" + browserId);

        streamMessage = gson.fromJson(response, StreamMessage.class);
        stream.stream(streamMessage.getResponse());
      } while (streamMessage != null && !streamMessage.isLast());
    } finally {
      stream.finish();
      heartBeatManager.cancelTimer();
      stopSession(sessionId);
    }
  }

  private boolean sameFiles(Collection<FileInfo> filesToUpload, Collection<FileInfo> fileSet) {
    for (FileInfo info : fileSet) {
      if (!info.isServeOnly() && !filesToUpload.contains(info)) {
        return false;
      }
    }
    return true;
  }
}
