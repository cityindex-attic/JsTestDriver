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

import junit.framework.TestCase;

/**
 * @author corysmith
 *
 */
public class StatementBuilderTest extends TestCase {

  public void testFlattenSimple() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var i=0;", InitStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }

  public void testArrayLiteral() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("[1,2,4]", InitStatement.class)
    );
  }
  
  public void testTopLevelIncAndDec() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("coverage[", InitStatement.class)
      .expect("         foo]++;", NonStatement.class)
      .expect("coverage[foo]--;", ExecutableStatement.class)
    );
  }
  
  public void testNestedLevelIncAndDec() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("coverage[foo]++;", InitStatement.class)
      .expect("coverage[foo]--;", ExecutableStatement.class)
    );
  }
  
  public void testFlattenWithNestedComment() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var i=0;", InitStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
      .expect("// doo be doo be doooooo.", NonStatement.class)
      .expect("goodbye(i);", ExecutableStatement.class)
    );
  }
  
  public void testFlattenWithMultilineComment() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var i=0;", InitStatement.class)
      .expect("hello(", ExecutableStatement.class)
      .expect(");", NonStatement.class)
      .expect("/* foooooo", NonStatement.class)
      .expect(" */goodbye(i);", NonStatement.class)
    );
  }
  
  public void testFlattenWithClassDefine() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("/**", InitNonStatement.class)
      .expect(" * Fourscore and  seven foos ago...", NonStatement.class)
      .expect(" */", NonStatement.class)
      .expect("function Foo(){};", ExecutableStatement.class)
      .expect("/**", NonStatement.class)
      .expect(" * Wakka Wakka!", NonStatement.class)
      .expect(" */", NonStatement.class)
      .expect("Foo.prototype.bar = function() {;", ExecutableStatement.class)
      .expect(" // doo be doo be doooooo.", NonStatement.class)
      .expect(" return goodbye(i);", ExecutableStatement.class)
      .expect("};", NonStatement.class)
    );
  }

  public void testFlattenWithNonStatement() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var i=0;", InitStatement.class)
      .expect("hello(0,", ExecutableStatement.class)
      .expect("      i);", NonStatement.class)
      .expect("i=1;", ExecutableStatement.class)
    );
  }

  public void testFlattenWithNakedFor() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(var i=0; i<0; i++)", InitStatement.class)
      .expect("  hello(", OmittedBlockStatement.class)
      .expect("        i)", NonStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }
  
  public void testFlattenWithNakedMultilineFor() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(var i=0;", InitStatement.class)
      .expect("        i<0;", NonStatement.class)
      .expect("        i++)", NonStatement.class)
      .expect("  hello(i)", OmittedBlockStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }

  public void testFlattenWithNakedNestedFor() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(var i=0; i<0; i++)", InitStatement.class)
      .expect("  for(var i=0; i<0; i++)", OmittedBlockStatement.class)
      .expect("    hello(i);", OmittedBlockStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }
  
  public void testFlattenWithIfElse() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("if(i<0) {", InitStatement.class)
      .expect("  hello(", ExecutableStatement.class)
      .expect("        i);", NonStatement.class)
      .expect("} else {", NonStatement.class)
      .expect("  goodbye(i);", ExecutableStatement.class)
      .expect("}", NonStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }

  public void testFlattenWithNakedIf() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("if(i<0)", InitStatement.class)
      .expect("  hello(", OmittedBlockStatement.class)
      .expect("        i)", NonStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }
  
  public void testFlattenWithNakedIfElse() throws Exception {
    assertStatements(new StatementExpectations()
    .expect("if(i<0)", InitStatement.class)
    .expect("  hello(", OmittedBlockStatement.class)
    .expect("        i)", NonStatement.class)
    .expect("else", OmittedBlockContinuationStatement.class)
    .expect("  hello(0);", OmittedBlockStatement.class)
    );
  }

  public void testFlattenWithNakedWhile() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("while(i<0)", InitStatement.class)
      .expect("  hello(", OmittedBlockStatement.class)
      .expect("        i)", NonStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }

  public void testFlattenWithNakedWith() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("with(i)", InitStatement.class)
      .expect("  hello(", OmittedBlockStatement.class)
      .expect("        i)", NonStatement.class)
      .expect("hello(0);", ExecutableStatement.class)
    );
  }

  public void testFlattenWithBlock() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(var i=0; i<0; i++)", InitStatement.class)
      .expect("  {hello(i);}", OmittedBlockStatement.class)
      .expect("hello(other);", ExecutableStatement.class)
    );
  }

  public void testFlattenWithMultilineBlock() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(var i=0; i<0; i++)", InitStatement.class)
      .expect("{", NonStatement.class)
      .expect("  hello(i);", ExecutableStatement.class)
      .expect("}", NonStatement.class)
      .expect("hello(other);", ExecutableStatement.class)
    );
  }

  // TODO(corysmith): Fix this issue.
  public void testFlattenWithMultilineBlockStatementOnFirstLine() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(var i=0; i<0; i++)", InitStatement.class)
      .expect("{hello(i);", NonStatement.class)
      .expect("}", NonStatement.class)
      .expect("hello(other);", ExecutableStatement.class)
    );
  }

  public void testFlattenWithMultilineArguments() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("hello(0", InitStatement.class)
      .expect(");hello(0", NonStatement.class)
      .expect(");", NonStatement.class)
    );
  }

  public void testFlattenWithObjectLiteral() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var a = {", InitStatement.class)
      .expect("  b : 1,", NonStatement.class)
      .expect("  c : function(are) {", NonStatement.class)
      .expect("    if (are < 1) {", ExecutableStatement.class)
      .expect("      return are + 2;", ExecutableStatement.class)
      .expect("    }", NonStatement.class)
      .expect("    return 0;", ExecutableStatement.class)
      .expect("  }", NonStatement.class)
      .expect("}", NonStatement.class)
    );
  }

  public void testFlattenWithFunctionExpressionCall() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("(function(window) {", InitStatement.class)
      .expect("  var a = 4;", ExecutableStatement.class)
      .expect("  window.reload(a);", ExecutableStatement.class)
      .expect("})();", NonStatement.class)
    );
  }

  public void testFlattenWithNew() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var a = 1;", InitStatement.class)
      .expect("new Bar();", ExecutableStatement.class)
    );
  }

  public void testFlattenWithThrow() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("var a = 1;", InitStatement.class)
      .expect("throw new Error();", ExecutableStatement.class)
    );
  }
  
  public void testFlattenStartingWithComment() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("// this is a comment", InitNonStatement.class)
      .expect("var a = 1;", ExecutableStatement.class)
      .expect("throw new Error();", ExecutableStatement.class)
    );
  }

  public void testFlattenWithTryCatch() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("try {", InitStatement.class)
      .expect("  throw new Error();", ExecutableStatement.class)
      .expect("} catch(e) {", NonStatement.class)
      .expect("  throw e;", ExecutableStatement.class)
      .expect("}", NonStatement.class)
    );
  }

  public void testFlattenNewInArgs() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("new TestCaseManager(", InitStatement.class)
      .expect("  new TestRunner());", NonStatement.class)
    );
  }
  
  public void testFlattenAssignmentInWhile() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("while(", InitStatement.class)
      .expect("  a = 32) {", NonStatement.class)
      .expect("  new TestRunner();", ExecutableStatement.class)
      .expect("}", NonStatement.class)
    );
  }
  
  public void testFlattenAssignmentInIf() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("if(", InitStatement.class)
      .expect("  a = 32) {", NonStatement.class)
      .expect("  new TestRunner();", ExecutableStatement.class)
      .expect("}", NonStatement.class)
    );
  }
  
  public void testFlattenAssignmentInWithAndParens() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("with(", InitStatement.class)
      .expect("  (a = foo())) {", NonStatement.class)
      .expect("  new TestRunner();", ExecutableStatement.class)
      .expect("}", NonStatement.class)
    );
  }
  
  public void testFlattenAssignmentInIfWithParens() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("if(", InitStatement.class)
      .expect("   (", NonStatement.class)
      .expect("   b + (", NonStatement.class)
      .expect("     a = 32))) {", NonStatement.class)
      .expect("  new TestRunner();", ExecutableStatement.class)
      .expect("}", NonStatement.class)
    );
  }
  
  public void testFlattenAssignmentInFor() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("for(", InitStatement.class)
      .expect("  a = 32;", NonStatement.class)
      .expect("  a = b();", NonStatement.class)
      .expect("  b = new zed()) {", NonStatement.class)
      .expect("  new TestRunner();", ExecutableStatement.class)
      .expect("}", NonStatement.class)
    );
  }
  
  public void testFlattenFullTree() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("jstd.initializeTestCaseManager = function() {", InitStatement.class)
      .expect("  jstd.testCaseManager = new jstd.TestCaseManager(", ExecutableStatement.class)
      .expect("    new jstd.TestRunner(function() {", NonStatement.class)
      .expect("      jstd.jQuery('body').children().remove();", ExecutableStatement.class)
      .expect("      jstd.jQuery(document).unbind();", ExecutableStatement.class)
      .expect("      jstd.jQuery(document).die();", ExecutableStatement.class)
      .expect("    }, Date)", NonStatement.class)
      .expect("  );", NonStatement.class)
      .expect("};", NonStatement.class)
    );
  }
  
  public void testFlattenTreeWithPrototype() throws Exception {
    assertStatements(new StatementExpectations()
      .expect("jstestdriver.TestCaseManager.prototype.TestCase = function(testCaseName, proto) {",
          InitStatement.class)
      .expect("  var testCaseClass = function() {};", ExecutableStatement.class)
      .expect("", NonStatement.class)
      .expect("  if (proto) {", ExecutableStatement.class)
      .expect("    testCaseClass.prototype = proto;", ExecutableStatement.class)
      .expect("  }", NonStatement.class)
      .expect("  if (typeof testCaseClass.prototype.setUp == 'undefined') {",
          ExecutableStatement.class)
      .expect("    testCaseClass.prototype.setUp = function() {};", ExecutableStatement.class)
      .expect("  }", NonStatement.class)
      .expect("  if (typeof testCaseClass.prototype.tearDown == 'undefined') {",
          ExecutableStatement.class)
      .expect("    testCaseClass.prototype.tearDown = function() {};", ExecutableStatement.class)
      .expect("  }", NonStatement.class)
      .expect("  this.testCases_[testCaseName] = testCaseClass;", ExecutableStatement.class)
      .expect("  return testCaseClass;", ExecutableStatement.class)
      .expect("};", NonStatement.class)
    );
    
  }


  private void assertStatements(StatementExpectations expectations) throws Exception {
    Statements actualStatements = new StatementsBuilder(new Code("foo.js", "HASH",
      expectations.source())).build();
    expectations.verify(actualStatements);
  }

  public static class StatementExpectations {
    public List<StatementExpectation> expectations = new LinkedList<StatementExpectation>();
    private int lineNumber = 0;

    public StatementExpectations expect(String line, Class<?> expectedType) {
      lineNumber++;
      expectations.add(new StatementExpectation(lineNumber , line, expectedType));
      return this;
    }

    public String source() {
      StringBuilder source = new StringBuilder();
      for (StatementExpectation expectation : expectations) {
        source.append(expectation.line).append("\n");
      }
      return source.toString();
    }

    public void verify(Statements actualStatements) {
      assertEquals(expectations.size(), actualStatements.getTotalSourceLines());
      Iterator<StatementExpectation> expected = expectations.iterator();
      Iterator<Statement> actual = actualStatements.iterator();
      
      int executableLines = 0;
      while(expected.hasNext() && actual.hasNext()) {
        Statement nextStatment = actual.next();
        executableLines  += nextStatment.isExecutable() ? 1 : 0;
        expected.next().assertStatement(nextStatment);
      }
      assertFalse(String.format("Expected %s was %s", expectations, actualStatements),
          expected.hasNext());
      assertFalse(String.format("Expected %s was %s", expectations, actualStatements),
          actual.hasNext());
      assertEquals(executableLines, actualStatements.getExecutableLines());
    }
  }

  public static class StatementExpectation {
    private final int lineNumber;
    private final String line;
    private final Class<?> expectedType;

    public StatementExpectation(int lineNumber, String line, Class<?> expectedType) {
      this.lineNumber = lineNumber;
      this.line = line;
      this.expectedType = expectedType;
    }

    public void assertStatement(Statement actual) {
      assertEquals(String.format("expected: \n%s actual: \n%s", this, actual),
          lineNumber, actual.getLineNumber());
      assertEquals(line, actual.getSourceText());
      assertEquals(actual.toString(), expectedType, actual.getClass());
    }

    @Override
    public String toString() {
      return String.format("Expect %s %s: %s\n", expectedType.getSimpleName(), lineNumber, line);
    }
  }
}
