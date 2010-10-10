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

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class RelativePathConverter {

  private final String baseDir;
  private final List<FileInfo> files;

  public RelativePathConverter(String baseDir, List<FileInfo> files) {
    this.baseDir = baseDir;
    this.files = files;
  }

  public List<FileInfo> convert() {
    List<FileInfo> convertedPaths = Lists.newLinkedList();

    for (FileInfo f : files) {
      convertedPaths.add(new FileInfo(f.getFilePath().replace(baseDir, ""), f.getTimestamp(),
          f.isPatch(), f.isServeOnly(), f.getData()));
    }
    return convertedPaths;
  }
}
