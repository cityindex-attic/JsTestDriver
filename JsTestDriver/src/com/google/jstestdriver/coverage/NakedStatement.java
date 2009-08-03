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
public class NakedStatement extends ExecutableStatement{

  private Statement nestedInBlock;
  private Statement nestedOutOfBlock;

  public NakedStatement(int lineNumber, String lineSource, String fileHash) {
    super(lineNumber, lineSource, fileHash);
  }

  @Override
  public String toSource(int totalLines, int executableLines) {
    PrefixBuilder source = new PrefixBuilder(lineSource, fileHash, lineNumber, totalLines).prepend("{");
    if (nestedInBlock != null) {
      source.append(nestedInBlock.toSource(totalLines, executableLines));
    }
    source.append("}");
    if (nestedOutOfBlock != null) {
      source.append(nestedOutOfBlock.toSource(totalLines, executableLines));
    }
    return source.build();
  }

  public boolean nest(Statement statement) {
    if (statement instanceof NakedStatement || statement instanceof NonStatement) {
      nestedInBlock = statement;
      return true;
    }
    if (statement instanceof NakedContinuationStatement) {
      nestedOutOfBlock = statement;
      return true;
    }
    return false;
  }
}
