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
import java.io.Writer;

/**
 * Writes the code coverage in the LCOV format.
 * @author corysmith@google.com (Cory Smith)
 */
public class LcovWriter implements CoverageWriter {
  private final Writer out;
  private CoveredLine last;

  public LcovWriter(Writer out) {
    this.out = out;
  }

  public CoverageWriter addLine(CoveredLine line) {
    try {
      if (last == null) {
        writeRecordStart(line.getQualifiedFile());
      } else if (line.getQualifiedFile() != last.getQualifiedFile()) {
        writeEndOfRecord();
        writeRecordStart(line.getQualifiedFile());
      }
      writeCoverageLine(line.getLineNumber(), line.getExecutedNumber());
      last = line;
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeRecordStart(String qualifiedFile) throws IOException {
    out.append("SF:").append(qualifiedFile).append("\n");
  }

  private Writer writeEndOfRecord() throws IOException {
    return out.append("end_of_record\n");
  }

  private void writeCoverageLine(int lineNumber, int executedNumber) throws IOException {
    out.append("DA:")
       .append(String.valueOf(lineNumber))
       .append(",")
       .append(String.valueOf(executedNumber))
       .append("\n");
  }

  public void flush() {
    try {
      writeEndOfRecord();
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
