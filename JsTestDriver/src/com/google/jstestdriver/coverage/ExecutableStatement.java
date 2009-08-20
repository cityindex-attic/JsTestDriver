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
 * @author corysmith
 * 
 */
public class ExecutableStatement implements Statement {

  protected final String fileHash;
  protected Statement next = new NullStatement();
  protected final int lineNumber;
  protected final String lineSource;

  public ExecutableStatement(int lineNumber, String lineSource, String fileHash) {
    this.lineNumber = lineNumber;
    this.lineSource = lineSource;
    this.fileHash = fileHash;
  }

  public String toSource(int totalLines, int executableLines) {
    PrefixBuilder builder = new PrefixBuilder(lineSource, fileHash, lineNumber, totalLines);
    if (next != null) {
        builder.append(next.toSource(totalLines, executableLines)).build();
    }
    return builder.build();
  }
  
  public boolean isExecutable() {
    return true;
  }

  public Statement add(Statement statement, boolean notInOmittedBlock) {
    next = statement;
    return statement;
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

  public void toList(List<Statement> statementList) {
    statementList.add(this);
    next.toList(statementList);
  }
  
  public void toListOfExecutableLines(List<Integer> numbers) {
    numbers.add(lineNumber);
    next.toListOfExecutableLines(numbers);
  }
}
