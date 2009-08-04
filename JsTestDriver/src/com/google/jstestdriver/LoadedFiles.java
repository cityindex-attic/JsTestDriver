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
import java.util.LinkedHashSet;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class LoadedFiles {

  private Collection<FileResult> loadedFiles;

  public LoadedFiles() {
    loadedFiles = new LinkedHashSet<FileResult>();
  }

  public LoadedFiles(Collection<FileResult> loadedFiles) {
    this.loadedFiles = loadedFiles;
  }

  public Collection<FileResult> getLoadedFiles() {
    return loadedFiles;
  }

  public boolean hasError() {
    for (FileResult fileResult : loadedFiles) {
      if (!fileResult.isSuccess()) {
        return true;
      }
    }
    return false;
  }
}
