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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class FileSetCacheStrategy {
  private static final Logger logger =
      LoggerFactory.getLogger(FileSetCacheStrategy.class);

  /** Creates a fileSet from out of date and absent files. */
  public Set<FileInfo> createExpiredFileSet(Collection<FileInfo> newFileSet,
                                            Set<FileInfo> currentFileSet) {
    Set<FileInfo> expiredFileSet = new LinkedHashSet<FileInfo>();

    if (currentFileSet.isEmpty()) {
      for (FileInfo info : newFileSet) {
        expiredFileSet.add(info);
      }
    } else {
      Set<FileInfo> diff = new LinkedHashSet<FileInfo>(newFileSet);
  
      diff.removeAll(currentFileSet);
      for (FileInfo info : diff) {
        expiredFileSet.add(info);
      }
      for (FileInfo browserFileInfo : currentFileSet) {
        for (FileInfo clientFileInfo : newFileSet) {
          if (clientFileInfo.equals(browserFileInfo)){
            if (clientFileInfo.getTimestamp() != browserFileInfo.getTimestamp() ||
                clientFileInfo.getLength() != browserFileInfo.getLength()) {
              expiredFileSet.add(clientFileInfo);
            } else {
              logger.debug("files equal {}, {} no update", clientFileInfo, browserFileInfo);
            }
            break;
          }
        }
      }
    }
    return expiredFileSet;
  }
}