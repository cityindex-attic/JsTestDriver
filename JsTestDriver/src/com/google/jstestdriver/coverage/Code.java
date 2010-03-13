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
 * Represents code to be instrumented.
 * @author corysmith@google.com (Cory Smith)
 */
public class Code {

  private final String filePath;

  private final String sourceCode;

  public Code(String filePath,String sourceCode) {
    this.filePath = filePath;
    this.sourceCode = sourceCode;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public String getFilePath() {
    return filePath;
  }

  @Override
  public String toString() {
    return String.format("%s(%s, %s)",
        getClass().getSimpleName(), filePath, sourceCode);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
    result = prime * result
      + ((sourceCode == null) ? 0 : sourceCode.hashCode());
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
    Code other = (Code) obj;
    if (filePath == null) {
      if (other.filePath != null)
        return false;
    } else if (!filePath.equals(other.filePath))
      return false;
    if (sourceCode == null) {
      if (other.sourceCode != null)
        return false;
    } else if (!sourceCode.equals(other.sourceCode))
      return false;
    return true;
  }
}
