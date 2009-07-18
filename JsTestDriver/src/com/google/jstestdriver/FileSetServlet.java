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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetServlet extends HttpServlet implements Observer {

  private static final int HEARTBEAT_TIMEOUT = 2000;

  private final Gson gson = new Gson();
  private final CapturedBrowsers capturedBrowsers;
  private final Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();

  // Shared with the TestResourceServlet
  private final FilesCache filesCache;

  public FileSetServlet(CapturedBrowsers capturedBrowsers, FilesCache filesCache) {
    this.capturedBrowsers = capturedBrowsers;
    this.filesCache = filesCache;
    this.capturedBrowsers.addObserver(this);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    String session = req.getParameter("session");
    String sessionId = req.getParameter("sessionId");

    if (session == null && sessionId != null) {
      sessionHeartBeat(id, sessionId);
    } else { 
      if (session.equals("start")) {
        startSession(id, resp.getWriter());
      } else if (session.equals("stop")) {
        stopSession(id, sessionId, resp.getWriter());
      }
    }
  }

  private void sessionHeartBeat(String id, String sessionId) {
    Lock lock = locks.get(id);

    if (lock.getSessionId().equals(sessionId)) {
      lock.setLastHeartBeat(new Date().getTime());
    } else {
      // who are you??
    }
  }

  public void stopSession(String id, String sessionId, PrintWriter writer) {
    Lock lock = locks.get(id);

    lock.unlock(sessionId);
    writer.flush();
  }

  public void startSession(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Lock lock = locks.get(id);
    String sessionId = UUID.randomUUID().toString();

    if (lock.tryLock(sessionId)) {
      writer.write(sessionId);
    } else {
      // session is probably staled
      if ((!browser.isCommandRunning() && browser.peekCommand() == null) ||
          ((new Date().getTime() - lock.getLastHeartBeat()) > HEARTBEAT_TIMEOUT)) {
        lock.forceUnlock();
        SlaveBrowser slaveBrowser = capturedBrowsers.getBrowser(id);

        slaveBrowser.clearCommandRunning();
        slaveBrowser.clearResponseQueue();
        filesCache.clear();
        writer.write(lock.tryLock(sessionId) ? sessionId : "FAILED");
      } else {
        writer.write("FAILED");
      }
    }
    writer.flush();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String data = req.getParameter("data");
    String id = req.getParameter("id");

    if (data != null) {
      uploadFiles(id, data);
    } else {
      checkFileSet(req.getParameter("fileSet"), id, resp.getWriter());
    }
  }

  public void checkFileSet(String fileSet, String browserId, PrintWriter writer) {
    Collection<FileInfo> clientFileSet =
      gson.fromJson(fileSet, new TypeToken<Collection<FileInfo>>() {}.getType());
    SlaveBrowser browser = capturedBrowsers.getBrowser(browserId);
    Set<FileInfo> browserFileSet = browser.getFileSet();
    Set<String> filesToRequest = new LinkedHashSet<String>();

    if (browserFileSet.isEmpty() || !clientFileSet.containsAll(browserFileSet)) {
      for (FileInfo info : clientFileSet) {
        filesToRequest.add(info.getFileName());
      }
    } else {
      Set<FileInfo> diff = new LinkedHashSet<FileInfo>(clientFileSet);

      diff.removeAll(browserFileSet);
      for (FileInfo info : diff) {
        filesToRequest.add(info.getFileName());
      }
      for (FileInfo browserFileInfo : browserFileSet) {
        for (FileInfo clientFileInfo : clientFileSet) {
          if (clientFileInfo.equals(browserFileInfo) &&
              clientFileInfo.getTimestamp() > browserFileInfo.getTimestamp()) {
            filesToRequest.add(clientFileInfo.getFileName());
            break;
          }
        }
      }
    }
    if (!filesToRequest.isEmpty()) {
      if (browser.getBrowserInfo().getName().contains("Safari")) {
        filesToRequest.clear();
        for (FileInfo info : clientFileSet) {
          filesToRequest.add(info.getFileName());
        }
      }
      // TODO(jeremiele): remove when Cory's FileInfo refactoring is in
      Set<String> filteredFilesToRequest = filterServeOnlyFiles(clientFileSet, filesToRequest);
      writer.write(gson.toJson(filteredFilesToRequest));
    }
    writer.flush();
  }

  // TODO(jeremiele): remove when Cory's FileInfo refactoring is in
  private Set<String> filterServeOnlyFiles(Collection<FileInfo> clientFileSet,
      Set<String> filesToRequest) {
    Set<String> filteredFilesToRequest = new LinkedHashSet<String>();
    Set<String> cachedFiles = filesCache.getAllFileNames();

    for (FileInfo fileInfo : clientFileSet) {
      String fileName = fileInfo.getFileName();

      if (filesToRequest.contains(fileName)) {
        if (!fileInfo.isServeOnly() || !cachedFiles.contains(fileName) ||
            filesCache.getFileData(fileName).getTimestamp() < fileInfo.getTimestamp()) {
          filteredFilesToRequest.add(fileName);
        }
      }
    }
    return filteredFilesToRequest;
  }

  public void update(Observable o, Object arg) {
    SlaveBrowser browser = (SlaveBrowser) arg;

    locks.put(browser.getId(), new Lock());
  }

  public void uploadFiles(String id, String data) {
    Collection<FileData> filesData = gson.fromJson(data, new TypeToken<Collection<FileData>>()
        {}.getType());

    for (FileData f : filesData) {
      String path = f.getFile();
//      String fileData = f.getData();

      filesCache.addFile(path, f);
    }
  }
}
