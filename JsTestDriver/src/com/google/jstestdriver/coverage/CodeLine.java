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
 * @author corysmith
 *
 */
public class CodeLine {

  private final String source;
  private final int lineNumber;

  /**
   * @param i
   * @param string
   */
  public CodeLine(int lineNumber, String source) {
    this.lineNumber = lineNumber;
    this.source = source;
  }

  public String getSource() {
    return source;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + lineNumber;
    result = prime * result + ((source == null) ? 0 : source.hashCode());
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
    CodeLine other = (CodeLine) obj;
    if (lineNumber != other.lineNumber)
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return String.format("%s {%s, %s}", getClass().getSimpleName(), lineNumber, source);
  }
}

