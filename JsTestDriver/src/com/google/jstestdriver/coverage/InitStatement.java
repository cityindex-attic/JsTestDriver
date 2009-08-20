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

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the initializing statement of an instrumented code block.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class InitStatement implements Statement{

  private final String filePath;
  protected final String fileHash;
  protected Statement next = new NullStatement();
  protected final int lineNumber;
  protected final String lineSource;
  

  public InitStatement(int lineNumber, String lineSource, String fileHash, String filePath) {
    this.lineNumber = lineNumber;
    this.lineSource = lineSource;
    this.fileHash = fileHash;
    this.filePath = filePath;
  }

  public String toSource(int totalLines, int executableLines) {
    LinkedList<Integer> executableLineNumbers = new LinkedList<Integer>();
    toListOfExecutableLines(executableLineNumbers);
    return String.format("%s_%s=%s.init('%s',%s,%s); %s%s",
        PrefixBuilder.COVERAGE_PREFIX,
        fileHash,
        PrefixBuilder.COVERAGE_PREFIX,
        filePath,
        totalLines,
        toJsArray(executableLineNumbers),
        lineSource,
        next.toSource(totalLines, executableLines));
  }

  //TODO(corysmith): Fix duplication.
  private String toJsArray(LinkedList<Integer> executableLineNumbers) {
    StringBuilder js = new StringBuilder("[");
    String sep = "";
    for (Integer number : executableLineNumbers) {
      js.append(sep).append(number);
      sep = ",";
    }
    return js.append("]").toString();
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
