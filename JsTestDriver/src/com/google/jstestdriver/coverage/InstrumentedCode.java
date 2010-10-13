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

import com.google.common.collect.Lists;

import java.util.List;

public class InstrumentedCode {

  private final List<Integer> executableLines;
  private final String instrumentedCode;
  private final String path;
  private final Integer fileId;

  public InstrumentedCode(Integer fileId,
                       String path,
                       List<Integer> executableLines,
                       String instrumentedCode) {
    this.fileId = fileId;
    this.path = path;
    this.executableLines = executableLines;
    this.instrumentedCode = instrumentedCode;
  }

  public String getInstrumentedCode() {
    return instrumentedCode;
  }

  public String getPath() {
    return path;
  }

  public void writeInitialLines(CoverageAccumulator accumulator) {
    List<CoveredLine> initialLines = Lists.newLinkedList();
    for (Integer lineNumber : executableLines) {
      initialLines.add(new CoveredLine(lineNumber, 0));
    }
    accumulator.add("<initial lines>",
        Lists.newArrayList(new FileCoverage(fileId, initialLines)));
  }
}
