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
 * Represents a statement that does nothing.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class NullStatement implements Statement {

  public Statement add(Statement statement, boolean notInOmittedBlock) {
    return null;
  }

  public int getLineNumber() {
    return 0;
  }

  public String getSourceText() {
    return null;
  }

  public boolean isExecutable() {
    return false;
  }

  public String toSource(int totalLines, int executableLines) {
    return "";
  }

  public void toList(List<Statement> statementList) {
  }

  public void toListOfExecutableLines(List<Integer> numbers) {
  }
}
