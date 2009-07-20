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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.JsonCommand.CommandType;

/**
 * Handles the communication of a command to the JsTestDriverServer from the JsTestDriverClient.
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
  private final FileReader fileReader;
  private final HeartBeatManager heartBeatManager;

  public CommandTask(JsTestDriverFileFilter filter, ResponseStream stream, Set<FileInfo> fileSet,
      String baseUrl, Server server, Map<String, String> params, FileReader fileReader,
      HeartBeatManager heartBeatManager) {
    this.filter = filter;
    this.stream = stream;
    this.fileSet = fileSet;
    this.baseUrl = baseUrl;
    this.server = server;
    this.params = params;
    this.fileReader = fileReader;
    this.heartBeatManager = heartBeatManager;
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
      System.err.println("The browser " + params.get("id") + " is not available anymore, " +
          "you might want to re-capture it");
      return false;
    }
    return true;
  }

  // TODO(corysmith): remove this function once FileInfo is used exclusively. Hate static crap.
  public static FileSource fileInfoToFileSource(FileInfo info) {
    if (info.getFileName().startsWith("http://")) {
      return new FileSource(info.getFileName(), info.getTimestamp());
    }
    return new FileSource("/test/" + info.getFileName(), info.getTimestamp());
  }

  private void uploadFileSet() {
    Map<String, String> fileSetParams = new LinkedHashMap<String, String>();
    Map<String, List<String>> patchMap = new HashMap<String, List<String>>();
    List<FileInfo> patchLessFileSet = createPatchLessFileSet(fileSet, patchMap);

    fileSetParams.put("id", params.get("id"));
    fileSetParams.put("fileSet", gson.toJson(patchLessFileSet));
    String postResult = server.post(baseUrl + "/fileSet", fileSetParams);

    if (postResult.length() > 0) {
      Collection<FileInfo> filesToUpload = gson.fromJson(postResult,
          new TypeToken<Collection<FileInfo>>() {}.getType());
      boolean shouldReset = sameFiles(filesToUpload, patchLessFileSet);
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
      List<FileData> filesData = new LinkedList<FileData>();
      List<FileInfo> filesSrc = new LinkedList<FileInfo>(finalFilesToUpload);

      loadFiles(patchMap, shouldReset, finalFilesToUpload, filesData, filesSrc);
      int size = filesData.size();

      for (int i = 0; i < size; i += CHUNK_SIZE) {
        int chunkEndIndex = Math.min(i + CHUNK_SIZE, size);
        List<FileData> filesDataChunk = filesData.subList(i, chunkEndIndex);
        List<FileInfo> filesSrcChunk = filesSrc.subList(i, chunkEndIndex);
        Map<String, String> loadFileParams = new LinkedHashMap<String, String>();

        loadFileParams.put("id", params.get("id"));
        loadFileParams.put("data", gson.toJson(filesDataChunk));
        server.post(baseUrl + "/fileSet", loadFileParams);
        List<FileSource> filesToLoad = filterFilesToLoad(filesSrcChunk);
        List<String> loadParameters = new LinkedList<String>();

        loadParameters.add(gson.toJson(filesToLoad));
        loadParameters.add("false");
        JsonCommand cmd = new JsonCommand(CommandType.LOADTEST, loadParameters);

        loadFileParams.put("data", gson.toJson(cmd));
        server.post(baseUrl + "/cmd", loadFileParams);
        String jsonResponse = server.fetch(baseUrl + "/cmd?id=" + params.get("id"));
        StreamMessage message = gson.fromJson(jsonResponse, StreamMessage.class);
        Response response = message.getResponse();
        LoadedFiles loadedFiles = gson.fromJson(response.getResponse(), LoadedFiles.class);
        String loadStatus = loadedFiles.getMessage();

        if (loadStatus.length() > 0) {
          System.err.println(loadStatus);
        }
      }
    }
  }

  private Collection<FileInfo> findDependencies(FileInfo file) {
    List<FileInfo> deps = new LinkedList<FileInfo>();
    for (String fileName : filter.resolveFilesDeps(file.getFileName())) {
      // TODO(corysmith): Replace this with a loadFile method.
      deps.add(new FileInfo(fileName, new File(fileName).lastModified(), false, false));
    }
    return deps;
  }

  private List<FileSource> filterFilesToLoad(List<FileInfo> fileInfos) {
    List<FileSource> filteredFileSources = new LinkedList<FileSource>();

    for (FileInfo fileInfo : fileInfos) {
      if (!fileInfo.isServeOnly()) {
        filteredFileSources.add(fileInfoToFileSource(fileInfo));
      }
    }
    return filteredFileSources;
  }

  private List<FileInfo> createPatchLessFileSet(Set<FileInfo> originalFileSet,
      Map<String, List<String>> patchMap) {
    List<FileInfo> patchLessFileSet = new LinkedList<FileInfo>();

    for (FileInfo i : originalFileSet) {
      if (i.isPatch()) {
        int size = patchLessFileSet.size();

        if (size > 0) {
          FileInfo prev = patchLessFileSet.get(size - 1);
          List<String> patches = patchMap.get(prev.getFileName());

          if (patches == null) {
            patches = new LinkedList<String>();
            patchMap.put(prev.getFileName(), patches);
          }
          patches.add(i.getFileName());
        } else {
          patchLessFileSet.add(i);
        }
      } else {
        patchLessFileSet.add(i);
      }
    }
    return patchLessFileSet;
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
      uploadFileSet();
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

  private boolean sameFiles(Collection<FileInfo> filesToUpload, List<FileInfo> fileSet) {
    if (filesToUpload.size() != fileSet.size()) {
      return false;
    }
    for (FileInfo info : fileSet) {
      if (!filesToUpload.contains(info)) {
        return false;
      }
    }
    return true;
  }

  private String readFile(String file) {
    return fileReader.readFile(file);
  }
}
