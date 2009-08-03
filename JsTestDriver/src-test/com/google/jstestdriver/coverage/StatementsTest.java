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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class StatementsTest extends TestCase {

  public void testAdd() {
    Statements statements = new Statements();
    ExecutableStatement eStatement = new ExecutableStatement(1, "if(true)", "hash");
    OmittedBlockStatement oStatement = new OmittedBlockStatement(2, "  foo(", "hash");
    NonStatement nStatement = new NonStatement(3, "  1);", "hash");
    OmittedBlockContinuationStatement cStatement =
        new OmittedBlockContinuationStatement(4, "else", "hash");
    OmittedBlockStatement oStatement2 = new OmittedBlockStatement(2, "  bar();", "hash");
    statements.add(eStatement);
    statements.add(oStatement);
    statements.add(nStatement);
    statements.add(cStatement);
    statements.add(oStatement2);
    
    assertEquals(5, statements.getTotalSourceLines());
    assertEquals(3, statements.getExecutableLines());
  }

  public void testIterator() {
    Statements statements = new Statements();
    List<Statement> expected = Arrays.asList(
        new ExecutableStatement(1, "if(true)", "hash"),
        new OmittedBlockStatement(2, "  foo(", "hash"),
        new NonStatement(3, "  1);", "hash"),
        new OmittedBlockContinuationStatement(4, "else", "hash"),
        new OmittedBlockStatement(2, "  bar();", "hash")
    );

    for (Statement statement : expected) {
      statements.add(statement);
    }
    Iterator<Statement> actualIterator = statements.iterator();
    Iterator<Statement> expectedIterator = expected.iterator();
    while (actualIterator.hasNext() && expectedIterator.hasNext()) {
      assertEquals(expectedIterator.next(), actualIterator.next());
    }
    assertFalse(actualIterator.hasNext());
    assertFalse(expectedIterator.hasNext());
  }
}
