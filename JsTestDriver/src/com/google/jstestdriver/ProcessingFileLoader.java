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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;

/**
 * A simple loader for files.
 * @author corysmith@google.com (Cory Smith)
 */
public class ProcessingFileLoader implements FileLoader {
  private final JsTestDriverFileFilter filter;
  private final FileReader reader;
  private final Collection<FileLoadPostProcessor> postprocessors;

  @Inject
  public ProcessingFileLoader(JsTestDriverFileFilter filter,
                              FileReader reader,
                              Collection<FileLoadPostProcessor> postprocessors) {
    this.filter = filter;
    this.reader = reader;
    this.postprocessors = postprocessors;
  }

  public List<FileInfo> loadFiles(Set<FileInfo> filesToLoad, boolean shouldReset) {
    List<FileInfo> loadedFiles = new LinkedList<FileInfo>();
    for (FileInfo file : filesToLoad) {
      StringBuilder fileContent = new StringBuilder();
      long timestamp = -1;
      if (!file.isRemote()) {
        timestamp = file.getTimestamp();
        fileContent.append(filter.filterFile(reader.readFile(file.getFileName()), !shouldReset));
        List<FileInfo> patches = file.getPatches();

        if (patches != null) {
          for (FileInfo patch : patches) {
            fileContent.append(reader.readFile(patch.getFileName()));
          }
        }
      }
      FileInfo processed = new FileInfo(file.getFileName(), timestamp, false, file
          .isServeOnly(), fileContent.toString());
      for (FileLoadPostProcessor hook : postprocessors) {
        processed = hook.process(processed);
      }
      loadedFiles.add(processed);
    }
    return loadedFiles;
  }
}
