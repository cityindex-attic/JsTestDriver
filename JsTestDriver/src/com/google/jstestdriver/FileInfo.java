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

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileInfo {

  private String fileName;
  private Long timestamp;
  private boolean isPatch;
  private boolean serveOnly;

  public FileInfo() {
  }

  public FileInfo(String fileName, long timestamp, boolean isPatch, boolean serveOnly) {
    this.fileName = fileName;
    this.timestamp = timestamp;
    this.isPatch = isPatch;
    this.serveOnly = serveOnly;
  }

  public String getFileName() {
    return fileName;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public boolean isPatch() {
    return isPatch;
  }

  public boolean isServeOnly() {
    return serveOnly;
  }

  @Override
  public boolean equals(Object obj) {
    return hashCode() == obj.hashCode();
  }

  @Override
  public int hashCode() {
    return fileName.hashCode();
  }
  
  public boolean isRemote() {
    return fileName.startsWith("http://") || fileName.startsWith("https://");
  }
}
