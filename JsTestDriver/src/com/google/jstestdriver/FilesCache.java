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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FilesCache {

  private final Map<String, FileInfo> files;

  public FilesCache(Map<String, FileInfo> files) {
    this.files = files;
  }

  public String getFileContent(String fileName) {
    FileInfo FileInfo = files.get(fileName);

    return FileInfo != null ? FileInfo.getData() : "";
  }

  public FileInfo getFileInfo(String fileName) {
    return files.get(fileName);
  }

  public void clear() {
    files.clear();
  }

  public void addFile(FileInfo fileInfo) {
    files.put(fileInfo.getFilePath(), fileInfo);
  }

  public int getFilesNumber() {
    return files.size();
  }

  public Set<String> getAllFileNames() {
    return files.keySet();
  }
  
  public Collection<FileInfo> getAllFileInfos() {
    return files.values();
  }
}
