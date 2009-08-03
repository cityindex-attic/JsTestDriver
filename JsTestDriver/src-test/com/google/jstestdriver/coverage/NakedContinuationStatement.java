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
 * A continuation of a block ommitted statement.
 * @author corysmith
 *
 */
public class NakedContinuationStatement extends Statement {
  private final String fileHash;

  private Statement nested = null;

  public NakedContinuationStatement(int lineNumber, String lineSource, String fileHash) {
    super(lineNumber, lineSource);
    this.fileHash = fileHash;
  }

  @Override
  public String toSource(int totalLines, int executableLines) {
    String indent = new PrefixBuilder(lineSource, fileHash, lineNumber, totalLines).buildIndent();
    if (nested != null) {
      return String.format("\n%s%s%s",
                           indent,
                           lineSource,
                           nested.toSource(totalLines, executableLines));
    }
    return String.format("\n%s%s", indent, lineSource);
  }

  public boolean nest(Statement statement) {
    if (statement instanceof NakedStatement || statement instanceof NonStatement) {
      nested = statement;
      return true;
    }
    return false;
  }
}
