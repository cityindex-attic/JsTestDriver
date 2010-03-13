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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes a summary (executed lines/executable lines) * 100 of the coverage to the
 * provided PrintStream.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class SummaryCoverageWriter implements CoverageWriter {

  private String qualifiedFile;
  private float totalLines = 0f;
  private float executed = 0f;
  private final OutputStream out;
  private final CoverageNameMapper mapper;

  public SummaryCoverageWriter(OutputStream out, CoverageNameMapper mapper) {
    this.out = out;
    this.mapper = mapper;
  }


  public void flush() {
  }

  public void writeCoverage(int lineNumber, int executedNumber) {
    totalLines++;
    if (executedNumber > 0) {
      executed++;
    }
  }

  public void writeRecordEnd() {
    float percent = (executed/totalLines) * 100;
    try {
      out.write(String.format("%s: %s%% covered\n", qualifiedFile, percent).getBytes());
      totalLines = 0f;
      executed = 0f;
      qualifiedFile = "";
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeRecordStart(Integer fileId) {
    this.qualifiedFile = mapper.unmap(fileId);
  }
}
