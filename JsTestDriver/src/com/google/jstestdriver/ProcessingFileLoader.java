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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;
import com.google.jstestdriver.util.StopWatch;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A simple loader for files.
 * @author corysmith@google.com (Cory Smith)
 */
public class ProcessingFileLoader implements FileLoader {
  private final FileReader reader;
  private final Set<FileLoadPostProcessor> postprocessors;
  private final File basePath;
  private final StopWatch stopWatch;

  @Inject
  public ProcessingFileLoader(FileReader reader,
                              Set<FileLoadPostProcessor> postprocessors,
                              @Named("basePath") File basePath,
                              StopWatch stopWatch) {
    this.reader = reader;
    this.postprocessors = postprocessors;
    this.basePath = basePath;
    this.stopWatch = stopWatch;
  }

  // TODO(corysmith): Remove shouldReset.
  public List<FileInfo> loadFiles(
      Collection<FileInfo> filesToLoad, boolean shouldReset) {
    List<FileInfo> processed = new LinkedList<FileInfo>();
    List<FileInfo> loaded = new LinkedList<FileInfo>();
    try {
      stopWatch.start("preProcessFiles");
      final List<FileInfo> preProcessedFiles = new LinkedList<FileInfo>(filesToLoad);
      stopWatch.stop("preProcessFiles");

      stopWatch.start("loadFile");
      for (FileInfo file : preProcessedFiles) {
        loaded.add(file.loadFile(reader, basePath));
      }
      stopWatch.stop("loadFile");

      stopWatch.start("postProcessFile");
      for (FileInfo file : loaded) {
        processed.add(postProcessFile(file));
      }
      stopWatch.stop("postProcessFile");
    } catch (RuntimeException e) {
      throw e;
    }
    return processed;
  }

  private FileInfo postProcessFile(FileInfo processed) {
    for (FileLoadPostProcessor hook : postprocessors) {
      processed = hook.process(processed);
    }
    return processed;
  }
}
