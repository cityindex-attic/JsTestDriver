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
import java.util.List;

/**
 * A collection of Statements for lines of Js source code.
 * @author corysmith
 *
 */
public class Statements implements Iterable<Statement>{

  private List<Statement> statements = new LinkedList<Statement>();

  public void add(Statement statement) {
    statements.add(statement);
  }

  public Iterator<Statement> iterator() {
    return statements.iterator();
  }

  @Override
  public String toString() {
    return String.format("%s{ %s }", getClass().getSimpleName(), statements);
  }

  public int getTotalSourceLines() {
    return statements.size();
  }

  public int getExecutableLines() {
    int executableLines = 0;
    for (Statement statement : statements) {
      executableLines += statement.isExecutable() ? 1 : 0;
    }
    return executableLines;
  }

  public String toSource(Code code) {
    StringBuilder source = new StringBuilder();

    for (Statement statement : nestStatements()) {
      source.append(statement.toSource(getTotalSourceLines(), getExecutableLines()));
    }
    source.append("\n");
    return source.toString();
  }

  private List<Statement> nestStatements() {
    List<Statement> nestedStatements = new LinkedList<Statement>();
    Statement parent = statements.get(0);
    nestedStatements.add(parent);
    for (Statement statement : statements.subList(1, statements.size())) {
      if (!parent.nest(statement)) {
        nestedStatements.add(statement);
      }
      parent = statement;
    }
    return nestedStatements;
  }
}

