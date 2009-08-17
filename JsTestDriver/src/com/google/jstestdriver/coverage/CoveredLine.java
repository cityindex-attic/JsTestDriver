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

/**
 * Represent a line of code after the instrumented code has been executed.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoveredLine implements Comparable<CoveredLine>{

  private int executedNumber;
  private String qualifiedFile;
  private int lineNumber;
  private int totalExecutableLines;
  
  public CoveredLine() {
    executedNumber = 0;
    qualifiedFile = "";
    lineNumber = 0;
    totalExecutableLines = 0;
  }
  
  public String getQualifiedFile() {
    return qualifiedFile;
  }

  public CoveredLine(String qualifiedFile, int lineNumber, int executedNumber, int totalLines) {
    this.qualifiedFile = qualifiedFile;
    this.lineNumber = lineNumber;
    this.executedNumber = executedNumber;
    this.totalExecutableLines = totalLines;
  }

  public int getExecutedNumber() {
    return executedNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  /** Aggregates two lines of covered code together. */
  public CoveredLine aggegrate(CoveredLine line) {
    if (!line.qualifiedFile.equals(qualifiedFile) || lineNumber != line.lineNumber) {
      return null;
    }
    return new CoveredLine(qualifiedFile,
                           lineNumber,
                           executedNumber + line.executedNumber,
                           totalExecutableLines);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + executedNumber;
    result = prime * result + lineNumber;
    result = prime * result
      + ((qualifiedFile == null) ? 0 : qualifiedFile.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CoveredLine other = (CoveredLine) obj;
    if (executedNumber != other.executedNumber)
      return false;
    if (lineNumber != other.lineNumber)
      return false;
    if (qualifiedFile == null) {
      if (other.qualifiedFile != null)
        return false;
    } else if (!qualifiedFile.equals(other.qualifiedFile))
      return false;
    // don't care about total lines.
    return true;
  }

  public int compareTo(CoveredLine o) {
    final int fileCompare = qualifiedFile.compareTo(o.qualifiedFile);
    if (fileCompare != 0) {
      return fileCompare;
    }
    return lineNumber - o.lineNumber;
  }
  
  @Override
  public String toString() {
    return String.format("%s(%s, %s, %s, %s)", getClass().getSimpleName(),
      qualifiedFile, lineNumber, executedNumber, totalExecutableLines);
  }

  /**
   * Serializes this line to the coverage writer.
   */
  public void write(CoverageWriter writer) {
    // conditionals are here because Gson won't recognize a specific type of file for creation.
    // Should investigate a more robust method of deserialize.
    if (lineNumber == 1) {
      writer.writeRecordStart(qualifiedFile);
    }
    writer.writeCoverage(lineNumber, executedNumber);
    if (lineNumber == totalExecutableLines) {
      writer.writeRecordEnd();
    }
  }
}
