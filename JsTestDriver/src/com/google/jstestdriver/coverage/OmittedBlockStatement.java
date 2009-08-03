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
 * Represents a statement to instrumented that should be encapsulated in a block in the source code?
 * @author corysmith@google.com (Cory Smith)
 */
public class OmittedBlockStatement extends ExecutableStatement{

  protected List<Statement> children = new LinkedList<Statement>();

  public OmittedBlockStatement(int lineNumber, String lineSource, String fileHash) {
    super(lineNumber, lineSource, fileHash);
    children.add(new NullStatement());
  }

  @Override
  public String toSource(int totalLines, int executableLines) {
    PrefixBuilder source =
        new PrefixBuilder(lineSource, fileHash, lineNumber, totalLines).prepend("{");
    for (Statement child : children) {
      source.append(child.toSource(totalLines, executableLines));
    }
    source.append("}");
    source.append(next.toSource(totalLines, executableLines));
    return source.build();
  }


  public Statement add(Statement statement, boolean notInOmittedBlock) {
    if (statement instanceof OmittedBlockStatement) {
      children.add(statement);
      return this;
    }
    if (statement instanceof NonStatement) {
      // check if child will take statement
      Statement added = children.get(children.size() - 1).add(statement, false);
      if (added != null) {
        return this;
      }
      children.add(statement);
      return this;
    }
    if (statement instanceof OmittedBlockContinuationStatement) {
      // check if child will take statement
      Statement added = children.get(children.size() - 1).add(statement, false);
      if (added != null) {
        return this;
      }
    }
    next = statement;
    return statement;

  }

  public void toList(List<Statement> statementList) {
    statementList.add(this);
    for (Statement child : children) {
      child.toList(statementList);
    }
    next.toList(statementList);
  }
}
