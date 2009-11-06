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
package com.google.jstestdriver.coverage;

import com.google.inject.Inject;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.LoadedFileInfo;
import com.google.jstestdriver.hooks.FileLoadPreProcessor;

import java.util.LinkedList;
import java.util.List;

/**
 * Adds the coverage js at the beginning of the FileInfo list.
 * 
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageJsAdder implements FileLoadPreProcessor {

  public static final String LCOV_JS = "/com/google/jstestdriver/coverage/javascript/LCOV.js";
  private final ClassFileLoader fileLoader;

  @Inject
  public CoverageJsAdder(ClassFileLoader fileLoader) {
    this.fileLoader = fileLoader;
  }

  public List<FileInfo> process(List<FileInfo> files) {
    LinkedList<FileInfo> processed = new LinkedList<FileInfo>();
    processed.add(0, new LoadedFileInfo(LCOV_JS, -1, false, false,
        fileLoader.load(LCOV_JS)));
    processed.addAll(files);
    return processed;
  }
}
