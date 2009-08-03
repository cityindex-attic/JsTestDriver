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

import java.util.Iterator;
import java.util.LinkedList;

/**
 * A collection of Statements for lines of Js source code.
 * @author corysmith
 *
 */
public class Statements implements Iterable<Statement>{

  private Statement last = null;
  private int totalStatements = 0;
  private int executableStatements = 0;
  private Statement first;

  public void add(Statement statement) {
    if (first == null) {
      first = statement;
      last = statement;
    } else {
      last = last.add(statement, true);
    }
    totalStatements++;
    executableStatements += statement.isExecutable() ? 1 : 0;
  }

  public Iterator<Statement> iterator() {
    LinkedList<Statement> statements = new LinkedList<Statement>();
    first.toList(statements);
    return statements.iterator();
  }

  @Override
  public String toString() {
    LinkedList<Statement> statements = new LinkedList<Statement>();
    first.toList(statements);
    return String.format("%s{ %s }", getClass().getSimpleName(), statements);
  }

  public int getTotalSourceLines() {
    return totalStatements;
  }

  public int getExecutableLines() {
    return executableStatements;
  }

  public String toSource(Code code) {
    StringBuilder source = new StringBuilder();

    source.append(first.toSource(getTotalSourceLines(), getExecutableLines()));
    source.append("\n");
    return source.toString();
  }
}
