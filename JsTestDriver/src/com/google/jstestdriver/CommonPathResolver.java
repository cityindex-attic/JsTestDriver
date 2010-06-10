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

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CommonPathResolver {

  private static final char PATH_SEPARATOR = File.separatorChar;

  private final List<FileInfo> files;

  public CommonPathResolver(List<FileInfo> files) {
    this.files = files;
  }

  public String resolve() {
    if (files.isEmpty()) {
      return "";
    }
    if (files.size() == 1) {
      return new File(files.get(0).getFilePath()).getParent() + PATH_SEPARATOR;
    }
    Iterator<FileInfo> iterator = files.iterator();
    String longestPath = iterator.next().getFilePath();

    do {
      longestPath = getLongestCommonPath(longestPath, iterator.next().getFilePath());
    } while (iterator.hasNext());
    return longestPath;
  }

  private String getLongestCommonPath(String longestPath, String currentPath) {
    char[] longestPathChar = longestPath.toCharArray();
    char[] currentPathChar = currentPath.toCharArray();
    int lastPathSeparatorIndex = 0;

    for (int i = 0; i < longestPathChar.length && i < currentPathChar.length
        && longestPathChar[i] == currentPathChar[i]; i++) {
      if (longestPathChar[i] == PATH_SEPARATOR && currentPathChar[i] == PATH_SEPARATOR) {
        lastPathSeparatorIndex = i;
      }
    }
    return longestPath.substring(0, lastPathSeparatorIndex + 1);
  }
}
