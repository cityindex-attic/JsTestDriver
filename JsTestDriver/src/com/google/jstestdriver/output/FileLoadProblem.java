/*
 * Copyright 2010 Google Inc.
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

package com.google.jstestdriver.output;

import com.google.jstestdriver.FileResult;

import java.io.PrintStream;

/**
 * An impl of Problem for issues with FileLoading.
 * 
 * @author corysmith@google.com (Cory Smith)
 */
class FileLoadProblem implements Problem {
  private final FileResult fileResult;

  public FileLoadProblem(FileResult fileResult) {
    this.fileResult = fileResult;
  }

  public void print(PrintStream out, boolean verbose) {
    out.println(String.format("    %s", fileResult.getMessage()));
  }
}
