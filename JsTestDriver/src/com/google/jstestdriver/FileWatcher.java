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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FileWatcher {

  private static final Logger logger = LoggerFactory.getLogger(FileWatcher.class.getName());
  private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();

  private final Map<String, Long> filesCache = new HashMap<String, Long>();
  private final File basePath;
  private final Set<String> files;

  public FileWatcher(File basePath, Set<String> files) {
    this.basePath = basePath;
    this.files = files;
    initFilesCache();
  }

  private void initFilesCache() {
    for (String file : files) {
      File f = new File(basePath, file);

      filesCache.put(file, f.lastModified());
    }
  }

  public void whitelist(Set<String> files) {
    this.files.addAll(files);
  }

  public List<String> getAllFiles() {
    List<String> modifiedFiles = new ArrayList<String>();

    for (String file : files) {
      File f = new File(basePath, file);
      Long lastModification = filesCache.get(file);

      if (lastModification == null || lastModification.compareTo(f.lastModified()) < 0) {
        logger.info("{} changed, will be updated on the next command.", f.getPath());
        filesCache.put(file, f.lastModified());
        modifiedFiles.add(file);
      }
    }
    if (!modifiedFiles.isEmpty()) {
      return modifiedFiles;
    }
    return EMPTY_STRING_LIST;
  }
}
