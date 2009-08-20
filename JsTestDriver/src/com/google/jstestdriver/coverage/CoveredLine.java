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
  private int lineNumber;

  public CoveredLine() {
    executedNumber = 0;
    lineNumber = 0;
  }

  public CoveredLine(int lineNumber, int executedNumber) {
    this.lineNumber = lineNumber;
    this.executedNumber = executedNumber;
  }

  public int getExecutedNumber() {
    return executedNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  /** Aggregates two lines of covered code together. */
  public CoveredLine aggegrate(CoveredLine line) {
    if (lineNumber != line.lineNumber) {
      return null;
    }
    return new CoveredLine(lineNumber,
                           executedNumber + line.executedNumber);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + executedNumber;
    result = prime * result + lineNumber;
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
    return true;
  }

  public int compareTo(CoveredLine o) {
    return lineNumber - o.lineNumber;
  }
  
  @Override
  public String toString() {
    return String.format("%s(%s, %s)",
        getClass().getSimpleName(), lineNumber, executedNumber);
  }

  /**
   * Serializes this line to the coverage writer.
   */
  public void write(CoverageWriter writer) {
    writer.writeCoverage(lineNumber, executedNumber);
  }
}
