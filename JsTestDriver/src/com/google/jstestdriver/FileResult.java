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
public class FileResult {

  private FileSource file;
  private boolean success;
  private String message;
  private long elapsed = 0;

  public FileResult() {
  }

  public FileResult(FileSource fileSource, boolean success, String message) {
    this.file = fileSource;
    this.success = success;
    this.message = message;
  }

  public FileSource getFileSource() {
    return file;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "FileResult [file=" + file + ", success=" + success + ", message=" + message
        + ", elapsed=" + elapsed + "]";
  }
}
