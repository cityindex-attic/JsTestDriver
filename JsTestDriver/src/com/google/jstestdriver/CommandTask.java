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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommandTask {

  private static final List<String> EMPTY_ARRAYLIST = new ArrayList<String>();
  private static final long WAIT_INTERVAL = 500L;
  private static final int CHUNK_SIZE = 50;

  private final Gson gson = new Gson();

  private final JsTestDriverFileFilter filter;
  private final ResponseStream stream;
  private final Set<FileInfo> fileSet;
  private final String baseUrl;
  private final Server server;
  private final Map<String, String> params;

  public CommandTask(JsTestDriverFileFilter filter, ResponseStream stream, Set<FileInfo> fileSet,
      String baseUrl, Server server, Map<String, String> params) {
    this.filter = filter;
    this.stream = stream;
    this.fileSet = fileSet;
    this.baseUrl = baseUrl;
    this.server = server;
    this.params = params;
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

  private void uploadFileSet() {
    Map<String, String> fileSetParams = new LinkedHashMap<String, String>();
    Map<String, List<String>> patchMap = new HashMap<String, List<String>>();
    List<FileInfo> patchLessFileSet = createPatchLessFileSet(fileSet, patchMap);

    fileSetParams.put("id", params.get("id"));
    fileSetParams.put("fileSet", gson.toJson(patchLessFileSet));
    String postResult = server.post(baseUrl + "/fileSet", fileSetParams);

    if (postResult.length() > 0) {
      Collection<String> filesToUpload = gson.fromJson(postResult,
          new TypeToken<Collection<String>>() {}.getType());
      boolean shouldReset = sameFiles(filesToUpload, patchLessFileSet);
      Set<String> finalFilesToUpload = new LinkedHashSet<String>();

      if (shouldReset) {
        JsonCommand cmd = new JsonCommand(CommandType.RESET, EMPTY_ARRAYLIST);
        Map<String, String> resetParams = new LinkedHashMap<String, String>();

        resetParams.put("id", params.get("id"));
        resetParams.put("data", gson.toJson(cmd));
        server.post(baseUrl + "/cmd", resetParams);
        server.fetch(baseUrl + "/cmd?id=" + params.get("id"));
        finalFilesToUpload.addAll(filesToUpload);
      } else {
        for (String file : filesToUpload) {
          finalFilesToUpload.addAll(filter.resolveFilesDeps(file));
        }
      }
      List<FileData> filesData = new LinkedList<FileData>();
      List<FileSource> filesSrc = new LinkedList<FileSource>();

      for (String file : finalFilesToUpload) {
        StringBuilder fileContent = new StringBuilder();
        long timestamp = -1;

        if (file.startsWith("http://") || file.startsWith("https://")) {
          filesSrc.add(new FileSource(file, -1));
          fileContent.append("none");
        } else {
          timestamp = getTimestamp(file);
          filesSrc.add(new FileSource("/test/" + file, timestamp));
          fileContent.append(filter.filterFile(readFile(file), !shouldReset));
          List<String> patches = patchMap.get(file);

          if (patches != null) {
            for (String patch : patches) {
              fileContent.append(readFile(patch));
            }
          }
        }
        filesData.add(new FileData(file, fileContent.toString(), timestamp));
      }
      int size = filesData.size();

      for (int i = 0; i < size; i += CHUNK_SIZE) {
        int chunkEndIndex = Math.min(i + CHUNK_SIZE, size);
        List<FileData> filesDataChunk = filesData.subList(i, chunkEndIndex);
        List<FileSource> filesSrcChunk = filesSrc.subList(i, chunkEndIndex);
        Map<String, String> loadFileParams = new LinkedHashMap<String, String>();

        loadFileParams.put("id", params.get("id"));
        loadFileParams.put("data", gson.toJson(filesDataChunk));
        server.post(baseUrl + "/fileSet", loadFileParams);
        List<String> loadParameters = new LinkedList<String>();

        loadParameters.add(gson.toJson(filesSrcChunk));
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
    Timer timer = new Timer(true);
    String browserId = params.get("id");
    String sessionId = null;

    try {
      sessionId = startSession();

      if (!sessionId.equals("")) {
        timer.schedule(new HeartBeat(
            (baseUrl + "/fileSet?id=" + browserId + "&sessionId=" + sessionId)), 0, 500);
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
      timer.cancel();
      stopSession(sessionId);
    }
  }

  private static class HeartBeat extends TimerTask {

    private final String url;

    public HeartBeat(String url) {
      this.url = url;
    }

    private String toString(InputStream inputStream) throws IOException {
      StringBuilder sb = new StringBuilder();
      int ch;

      while ((ch = inputStream.read()) != -1) {
        sb.append((char) ch);
      }
      inputStream.close();
      return sb.toString();
    }

    @Override
    public void run() {
      HttpURLConnection connection = null;

      try {
        connection = (HttpURLConnection) new URL(url).openConnection();

        connection.connect();
        toString(connection.getInputStream());
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }    
  }

  private long getTimestamp(String file) {
    for (FileInfo info : fileSet) {
      if (info.getFileName().equals(file)) {
        return info.getTimestamp();
      }
    }
    return 0;
  }

  private boolean sameFiles(Collection<String> filesToUpload, List<FileInfo> fileSet) {
    if (filesToUpload.size() != fileSet.size()) {
      return false;
    }
    for (FileInfo info : fileSet) {
      if (!filesToUpload.contains(info.getFileName())) {
        return false;
      }
    }
    return true;
  }

  private String readFile(String file) {
    BufferedInputStream bis = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(file));
      StringBuilder sb = new StringBuilder();

      for (int c = bis.read(); c != -1; c = bis.read()) {
        sb.append((char) c);
      }
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException("Impossible to read file: " + file, e);
    } finally {
      if (bis != null) {
        try {
          bis.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }
}
