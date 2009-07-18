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

import java.util.Map;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FilesCache {

  private final Map<String, FileData> files;

  public FilesCache(Map<String, FileData> files) {
    this.files = files;
  }

  public String getFileContent(String fileName) {
    FileData fileData = files.get(fileName);

    return fileData != null ? fileData.getData() : "";
  }

  public FileData getFileData(String fileName) {
    return files.get(fileName);
  }

  public void clear() {
    files.clear();
  }

  public void addFile(String path, FileData fileData) {
    files.put(path, fileData);
  }

  public int getFilesNumber() {
    return files.size();
  }

  public Set<String> getAllFileNames() {
    return files.keySet();
  }
}
