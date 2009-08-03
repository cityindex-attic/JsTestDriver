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

import junit.framework.TestCase;

import org.antlr.runtime.RecognitionException;

public class CodeCoverageDecoratorTest extends TestCase {

  public void testSourceFileLineCoverageDecoration() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',2,2); var a = 1;\n" +
        "LCOV_HASH[2]++; var b = 2;\n",

        "var a = 1;\n" +
        "var b = 2;\n"
    );
  }
  
  public void testSourceFileLineCoverageDecorationOverTenLines() throws Exception {
    int totalLines = 10;
    String line = "a = 1;\n";
    StringBuilder input = new StringBuilder(line);
    StringBuilder expect = new StringBuilder("LCOV_HASH=LCOV.init('FILE',");
    expect.append(totalLines).append(",").append(totalLines).append("); ").append(line);
    for (int i = 2; i <= totalLines; i++) {
      String indent = i < 10 ? " " : "";
      expect.append("LCOV_HASH[").append(i).append(indent).append("]++; ").append(line);
      input.append(line);
    }

    assertCodeCoverageDecorator(
        expect.toString(),
        input.toString()
    );
  }

  public void testDoNotDecorateStatementsAcrossMultipleLines() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',5,3); var a = 0;\n" +
        "LCOV_HASH[2]++; a = true \n" +
        "                  ? 1 \n" +
        "                  : 2;\n" +
        "LCOV_HASH[5]++; a = 3;\n",

        "var a = 0;\n" +
        "a = true \n" +
        "  ? 1 \n" +
        "  : 2;\n" +
        "a = 3;\n"
    );
  }

  public void testDecorateIfStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',7,5); var a = 0;\n" +
        "LCOV_HASH[2]++; if (a) {\n" +
        "LCOV_HASH[3]++;   a = 1;\n" +
        "                } else {\n" +
        "LCOV_HASH[5]++;   a = 2;\n" +
        "                }\n" +
        "LCOV_HASH[7]++; a = 3;\n",
        
        "var a = 0;\n" +
        "if (a) {\n" +
        "  a = 1;\n" +
        "} else {\n" +
        "  a = 2;\n" +
        "}\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateNakedIfWithMultilineStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',5,4); var a = 0;\n" +
        "LCOV_HASH[2]++; if (a)\n" +
        "{LCOV_HASH[3]++;   hello(\n" +
        "                        6);}\n" +
        "LCOV_HASH[5]++; a = 3;\n",

        "var a = 0;\n" +
        "if (a)\n" +
        "  hello(\n" +
        "        6);\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateFunctionStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',5,4); var a = 0;\n" +
        "LCOV_HASH[2]++; function foo (a) {\n" +
        "LCOV_HASH[3]++;   a = 1;\n" +
        "                }\n" +
        "LCOV_HASH[5]++; a = 3;\n",
        
        "var a = 0;\n" +
        "function foo (a) {\n" +
        "  a = 1;\n" +
        "}\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateWhileStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',5,4); var a = 0;\n" +
        "LCOV_HASH[2]++; while (a) {\n" +
        "LCOV_HASH[3]++;   a = 1;\n" +
        "                }\n" +
        "LCOV_HASH[5]++; a = 3;\n",
        
        "var a = 0;\n" +
        "while (a) {\n" +
        "  a = 1;\n" +
        "}\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateForStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',7,5); var a = 0;\n" +
        "LCOV_HASH[2]++; for (\n" +
        "LCOV_HASH[3]++;      var a = 1;\n" +
        "                     a < 20; a++) {\n" +
        "LCOV_HASH[5]++;   a = 1;\n" +
        "                }\n" +
        "LCOV_HASH[7]++; a = 3;\n",
        
        "var a = 0;\n" +
        "for (\n" +
        "     var a = 1;\n" +
        "     a < 20; a++) {\n" +
        "  a = 1;\n" +
        "}\n" +
        "a = 3;\n"
    );
  }

  public void testDecorateNakedIfStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',6,5); var a = 0;\n" +
        "LCOV_HASH[2]++; if (a)\n" +
        "{LCOV_HASH[3]++;   a = 1;}\n" +
        "                else\n" +
        "{LCOV_HASH[5]++;   a = 2;}\n" +
        "LCOV_HASH[6]++; a = 3;\n",

        "var a = 0;\n" +
        "if (a)\n" +
        "  a = 1;\n" +
        "else\n" +
        "  a = 2;\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateNakedNestedIfStatements() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',5,5); var a = 0;\n" +
        "LCOV_HASH[2]++; if (a)\n" +
        "{LCOV_HASH[3]++;   if (a)\n" +
        "{LCOV_HASH[4]++;     a = 1;}}\n" +
        "LCOV_HASH[5]++; a = 3;\n",
   
        "var a = 0;\n" +
        "if (a)\n" +
        "  if (a)\n" +
        "    a = 1;\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateNakedNestedIfElseStatements() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',7,6); var a = 0;\n" +
        "LCOV_HASH[2]++; if (a)\n" +
        "{LCOV_HASH[3]++;   if (a)\n" +
        "{LCOV_HASH[4]++;     a = 1;}\n" +
        "                else\n" +
        "{LCOV_HASH[6]++;   a = 2;}}\n" +
        "LCOV_HASH[7]++; a = 3;\n",
        
        "var a = 0;\n" +
        "if (a)\n" +
        "  if (a)\n" +
        "    a = 1;\n" +
        "else\n" +
        "  a = 2;\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateNakedNestedIfElseStatementsWithMultiline() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.init('FILE',8,6); var a = 0;\n" +
        "LCOV_HASH[2]++; if (a)\n" +
        "{LCOV_HASH[3]++;   if (a)\n" +
        "{LCOV_HASH[4]++;     foo(\n" +
        "                        34);}\n" +
        "                else\n" +
        "{LCOV_HASH[7]++;   a = 2;}}\n" +
        "LCOV_HASH[8]++; a = 3;\n",
        
        "var a = 0;\n" +
        "if (a)\n" +
        "  if (a)\n" +
        "    foo(\n" +
        "        34);\n" +
        "else\n" +
        "  a = 2;\n" +
        "a = 3;\n"
    );
  }
  
  public void testDecorateCommentStartingStatement() throws Exception {
    assertCodeCoverageDecorator(
        "LCOV_HASH=LCOV.initNoop('FILE',6,4); // foo bar\n" +
        "LCOV_HASH[2]++; if (a)\n" +
        "{LCOV_HASH[3]++;   a = 1;}\n" +
        "                else\n" +
        "{LCOV_HASH[5]++;   a = 2;}\n" +
        "LCOV_HASH[6]++; a = 3;\n",
        
        "// foo bar\n" +
        "if (a)\n" +
        "  a = 1;\n" +
        "else\n" +
        "  a = 2;\n" +
        "a = 3;\n"
    );
  }

  private void assertCodeCoverageDecorator(String expect, String sourceCode) throws RecognitionException {
    String filePath = "filename.js";
    String hash = "ABCD";
    CodeCoverageDecorator decorator =
        new CodeCoverageDecorator(new Code(filePath, hash, sourceCode));
    String actual = decorator.decorate();
    expect = expect.replaceAll("LCOV_HASH", "LCOV_" + hash).replaceAll("FILE", filePath);
    assertEquals(expect, actual);
  }
}
