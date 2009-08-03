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
 * Represents the initializing statement of an instrumented code block that is not be covered.
 * @author corysmith
 */
public class InitNonStatement implements Statement {
  private final String fileHash;
  private final String filePath;
  private Statement next = new NullStatement();
  private final String lineSource;

  public InitNonStatement(String lineSource, String fileHash, String filePath) {
    this.lineSource = lineSource;
    this.fileHash = fileHash;
    this.filePath = filePath;
  }

  public String toSource(int totalLines, int executableLines) {
    return String.format("%s_%s=%s.initNoop('%s',%s,%s); %s%s",
        PrefixBuilder.COVERAGE_PREFIX,
        fileHash,
        PrefixBuilder.COVERAGE_PREFIX,
        filePath,
        totalLines,
        executableLines,
        lineSource,
        next.toSource(totalLines, executableLines));
  }

  public Statement add(Statement statement, boolean notInOmittedBlock) {
    next = statement;
    return statement;
  }

  public int getLineNumber() {
    return 1;
  }

  public String getSourceText() {
    return lineSource;
  }

  public boolean isExecutable() {
    return false;
  }

  public void toList(List<Statement> statementList) {
    statementList.add(this);
    next.toList(statementList);
  }
}
