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

import java.util.LinkedList;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileInfo {

  @Override
  public String toString() {
    return "FileInfo [file=" + file + ", isPatch=" + isPatch + ", serveOnly=" + serveOnly
        + ", timestamp=" + timestamp + "]";
  }

  private String file;
  private Long timestamp;
  private transient boolean isPatch;
  private boolean serveOnly;
  private List<FileInfo> patches;
  private String data;

  public String getData() {
    return data == null ? "" : data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public FileInfo() {
  }

  public FileInfo(String fileName, long timestamp, boolean isPatch,
      boolean serveOnly, String data) {
    this.file = fileName;
    this.timestamp = timestamp;
    this.isPatch = isPatch;
    this.serveOnly = serveOnly;
    this.data = data;
  }

  public List<FileInfo> getPatches() {
    if (patches != null) {
      return new LinkedList<FileInfo>(patches);
    }
    return new LinkedList<FileInfo>();
  }

  public void addPatch(FileInfo patch) {
    if (patches == null) {
      patches = new LinkedList<FileInfo>();
    }
    this.patches.add(patch);
  }

  public boolean isServeOnly() {
    return serveOnly;
  }

  public String getFileName() {
    return file;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public boolean isPatch() {
    return isPatch;
  }
  
  public boolean canLoad() {
    return !(file.startsWith("http://") || file.startsWith("https://"));
  }

  @Override
  public boolean equals(Object obj) {
    return hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return file.hashCode();
  }
}
