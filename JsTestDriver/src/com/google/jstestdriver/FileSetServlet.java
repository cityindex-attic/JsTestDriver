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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileSetServlet extends HttpServlet implements Observer {

  private final Gson gson = new Gson();
  private final CapturedBrowsers capturedBrowsers;
  private final Map<String, String> files;
  private final Map<String, Lock> locks = new ConcurrentHashMap<String, Lock>();

  public FileSetServlet(CapturedBrowsers capturedBrowsers, Map<String, String> files) {
    this.capturedBrowsers = capturedBrowsers;
    this.files = files;
    this.capturedBrowsers.addObserver(this);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String id = req.getParameter("id");
    String session = req.getParameter("session");

    if (session.equals("start")) {
      startSession(id, resp.getWriter());
    } else if (session.equals("stop")) {
      stopSession(id, resp.getWriter());
    }
  }

  public void stopSession(String id, PrintWriter writer) {
    Lock lock = locks.get(id);

    lock.unlock();
    writer.flush();
  }

  public void startSession(String id, PrintWriter writer) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Lock lock = locks.get(id);

    if (lock.tryLock()) {
      writer.write("OK");
    } else {
      // session is probably staled
      if (!browser.isCommandRunning() && browser.peekCommand() == null) {
        lock.unlock();
        writer.write(lock.tryLock() ? "OK" : "FAILED");
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
      writer.write(gson.toJson(filesToRequest));
    }
    writer.flush();
  }

  public void update(Observable o, Object arg) {
    SlaveBrowser browser = (SlaveBrowser) arg;

    locks.put(browser.getId(), new Lock());
  }

  public void uploadFiles(String id, String data) {
    SlaveBrowser browser = capturedBrowsers.getBrowser(id);
    Collection<FileData> filesData = gson.fromJson(data, new TypeToken<Collection<FileData>>()
        {}.getType());
    LinkedHashSet<FileInfo> filesUploaded = new LinkedHashSet<FileInfo>();

    for (FileData f : filesData) {
      String path = f.getFile();
      String fileData = f.getData();

      files.put(path, fileData);
      files.put(resolvePath(path), fileData);
      filesUploaded.add(new FileInfo(f.getFile(), f.getTimestamp(), false));
    }
    browser.addFiles(filesUploaded);
  }

  private String resolvePath(String path) {
    Stack<String> resolvedPath = new Stack<String>();
    String[] tokenizedPath = path.split("/");

    for (String token : tokenizedPath) {
      if (token.equals("..")) {
        if (!resolvedPath.isEmpty()) {
          resolvedPath.pop();
          continue;
        }
      }
      resolvedPath.push(token);
    }
    return join(resolvedPath);
  }

  private String join(Collection<String> collection) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> iterator = collection.iterator();

    if (iterator.hasNext()) {
      sb.append(iterator.next());

      while (iterator.hasNext()) {
        sb.append("/");
        sb.append(iterator.next());
      }
    }
    return sb.toString();
  }
}
