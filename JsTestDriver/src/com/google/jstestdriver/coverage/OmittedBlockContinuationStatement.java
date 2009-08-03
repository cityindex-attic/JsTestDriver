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

import java.util.List;

/**
 * A non instrumenting continuation of a block omitted statement.
 * @author corysmith
 *
 */
public class OmittedBlockContinuationStatement implements Statement {
  private final String fileHash;

  private Statement next = new NullStatement();

  protected final int lineNumber;

  protected final String lineSource;

  public OmittedBlockContinuationStatement(int lineNumber, String lineSource, String fileHash) {
    this.lineNumber = lineNumber;
    this.lineSource = lineSource;
    this.fileHash = fileHash;
  }

  public String toSource(int totalLines, int executableLines) {
    String indent = new PrefixBuilder(lineSource, fileHash, lineNumber, totalLines).buildIndent();
    return String.format("\n%s%s%s",
                         indent,
                         lineSource,
                         next.toSource(totalLines, executableLines));
  }

  public String getSourceText() {
    return lineSource;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String toString() {
    return String.format("%s %s: %s\n", getClass().getSimpleName(), lineNumber, lineSource);
  }

  public boolean isExecutable() {
    return false;
  }

  public Statement add(Statement statement, boolean notInOmittedBlock) {
    next = statement;
    return next;
  }

  public void toList(List<Statement> statementList) {
    statementList.add(this);
    next.toList(statementList);
  }
}
