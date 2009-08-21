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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import com.google.jstestdriver.coverage.es3.ES3Lexer;
import com.google.jstestdriver.coverage.es3.ES3Parser;

/**
 * Builder for the statement lists. Parses the source code to produce a series
 * of statements that represents instrumented code.
 * 
 * @author corysmith@google.com (Cory Smith)
 * 
 */
public class StatementsBuilder {

  private static final Pattern NEWLINE_REGEX = Pattern.compile("(?m)($)");

  private static final Set<Integer> STATEMENT_TYPES = new HashSet<Integer>();
  static {
    STATEMENT_TYPES.add(ES3Lexer.ASSIGN);
    STATEMENT_TYPES.add(ES3Lexer.IF);
    STATEMENT_TYPES.add(ES3Lexer.VAR);
    STATEMENT_TYPES.add(ES3Lexer.FUNCTION);
    STATEMENT_TYPES.add(ES3Lexer.WHILE);
    STATEMENT_TYPES.add(ES3Lexer.FOR);
    STATEMENT_TYPES.add(ES3Lexer.WITH);
    STATEMENT_TYPES.add(ES3Lexer.CALL);
    STATEMENT_TYPES.add(ES3Lexer.RETURN);
    STATEMENT_TYPES.add(ES3Lexer.TRY);
    STATEMENT_TYPES.add(ES3Lexer.CATCH);
    STATEMENT_TYPES.add(ES3Lexer.THROW);
    STATEMENT_TYPES.add(ES3Lexer.PINC);
    STATEMENT_TYPES.add(ES3Lexer.PDEC);
    STATEMENT_TYPES.add(ES3Lexer.ARRAY);
  }

  private static final Set<Integer> HAS_ARGS_TYPES = new HashSet<Integer>();
  static {
    HAS_ARGS_TYPES.add(ES3Lexer.IF);
    HAS_ARGS_TYPES.add(ES3Lexer.WHILE);
    HAS_ARGS_TYPES.add(ES3Lexer.FOR);
    HAS_ARGS_TYPES.add(ES3Lexer.WITH);
  }

  private static final Set<Integer> EXPRESSION_TYPES = new HashSet<Integer>();
  static {
    EXPRESSION_TYPES.add(ES3Lexer.EXPR);
    EXPRESSION_TYPES.add(ES3Lexer.ARGS);
    EXPRESSION_TYPES.add(ES3Lexer.PAREXPR);
  }

  private static final Set<Integer> NAKED_TYPES = new HashSet<Integer>();
  static {
    NAKED_TYPES.add(ES3Lexer.FOR);
    NAKED_TYPES.add(ES3Lexer.IF);
    NAKED_TYPES.add(ES3Lexer.WHILE);
    NAKED_TYPES.add(ES3Lexer.WITH);
  }

  private final Code code;

  private CommonTokenStream tokenStream;

  public StatementsBuilder(Code code) {
    this.code = code;
  }

  /**
   * Correlates the AST of the code with the sourcelines to build a list of
   * Statements to represent instrumentable lines of code.
   */
  @SuppressWarnings("unchecked")
  public Statements build() {
    List<CodeLine> sourceLines = code.getLines();
    CommonTree root = parse();

    NodeQueue nodes = new NodeQueue();
    flattenTree(nodes, root.getChildren());
    Statements statements = new Statements();

    for (CodeLine line : sourceLines) {
      statements.add(nodes.firstNodeAt(line).createStatementFor(line, code));
    }
    return statements;
  }

  @SuppressWarnings("unchecked")
  private void flattenTree(NodeQueue nodes, List<CommonTree> trees) {
    if (trees == null) {
      return;
    }
    for (CommonTree cTree : trees) {
      if (isFirstInLine(cTree)) {
        if (isStatement(cTree)) {
          if (isNaked(cTree)) {
            nodes.add(new NakedNode(getLine(cTree)));
          } else {
            nodes.add(new StatementNode(getLine(cTree)));
          }
        } else if (isNakedContinuation(cTree)) {
          nodes.add(new ContinuationNode(getLine(cTree)));
        }
      }
      flattenTree(nodes, cTree.getChildren());
    }
  }

  private int getLine(CommonTree tree) {
    if (tree.getTokenStartIndex() >= 0) {
      return tokenStream.get(tree.getTokenStartIndex()).getLine();
    }
    return tree.getLine();
  }

  private boolean isNakedContinuation(CommonTree tree) {
    if (tree.getParent().getType() == ES3Lexer.IF && tree.getChildIndex() == 2) {
      return true;
    }
    return false;
  }

  private boolean isNaked(CommonTree tree) {
    if (tree.getType() == ES3Lexer.BLOCK) {
      return tokenStream.get(tree.getTokenStopIndex()).getLine() == getLine(tree);
    }
    return NAKED_TYPES.contains(tree.getParent().getType());
  }

  private boolean isFirstInLine(CommonTree tree) {
    for (int index = tree.getTokenStartIndex() - 1; index > 0; index--) {
      Token token = tokenStream.get(index);
      if (token.getChannel() == ES3Lexer.HIDDEN) {
        continue;
      }
      if (token.getType() == ES3Lexer.MultiLineComment) {
        int lineBreaks = -1;
        Matcher matcher = NEWLINE_REGEX.matcher(token.getText());
        while (matcher.find()) {
          lineBreaks++;
        }
        return token.getLine() + lineBreaks < getLine(tree);
      }
      return token.getLine() < getLine(tree);
    }
    return true;
  }

  private boolean isStatement(CommonTree tree) {
    if (tree.getType() == ES3Lexer.BLOCK) {
      boolean isSingleline = tokenStream.get(tree.getTokenStopIndex()).getLine() == getLine(tree);
      if (!isSingleline) {
        CommonTree child = (CommonTree) tree.getChild(0);
        if (isStatement(child) && getLine(child) == getLine(tree)) {
          System.err.printf("Warning: multiline block on line %s in %s has a statement"
              + " that will not be instrumented.\n", getLine(tree), code.getFilePath());
        }
      }
      return isSingleline;
    }
    if (!STATEMENT_TYPES.contains(tree.getType())) {
      return false;
    }
    // if it's in an expression, we don't want it.
    if (EXPRESSION_TYPES.contains(tree.getParent().getType())) {
      System.err.printf("Warning: multiline expression on line %s in %s has a statement"
          + " that will not be instrumented.\n", getLine(tree), code.getFilePath());
      return false;
    }
    if (HAS_ARGS_TYPES.contains(tree.getParent().getType()) && tree.getParent().getChild(0) == tree) {
      System.err.printf("Warning: multiline expression in '%s' on line %s in %s has a statement"
          + " that will not be instrumented.\n", tree.getParent().getText(), getLine(tree), code
          .getFilePath());
      return false;
    }
    return true;
  }

  private CommonTree parse() {
    tokenStream = new CommonTokenStream(new ES3Lexer(new ANTLRStringStream(code.getSourceCode())));
    ES3Parser parser = new ES3Parser(tokenStream);
    try {
      CommonTree tree = (CommonTree) parser.program().getTree();
      // it's a generic holder
      if (tree.isNil()) {
        return tree;
      }
      // have to create a generic holder -- the program consists of one
      // statement.
      CommonTree root = new CommonTree();
      root.addChild(tree);
      return root;
    } catch (RecognitionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Represents a list of Nodes that can be correlated against the lines of
   * source code.
   * 
   * @author corysmith
   * @author misko
   */
  private static class NodeQueue {
    private List<Node> nodes = new LinkedList<Node>();
    public static final Node NULL_NODE = new Node();

    public void add(StatementNode node) {
      nodes.add(node);
    }

    public Node firstNodeAt(CodeLine line) {
      while (!nodes.isEmpty()) {
        Node node = nodes.get(0);
        if (node.isBefore(line)) {
          nodes.remove(0);
        } else if (node.isAfter(line)) {
          return NodeQueue.NULL_NODE;
        } else {
          return nodes.remove(0);
        }
      }
      return NodeQueue.NULL_NODE;
    }

    @Override
    public String toString() {
      return nodes.toString();
    }
  }

  /**
   * Represents a CommonTree that is a statement in the AST.
   * 
   * @author corysmith
   * @author misko
   */
  private static class StatementNode extends Node {

    private final int lineNumber;

    public StatementNode(int lineNumber) {
      this.lineNumber = lineNumber;
    }

    public Statement createStatementFor(CodeLine line, Code code) {
      String fileHash = code.getFileHash();
      if (line.getLineNumber() == 1) {
        return new InitStatement(line.getLineNumber(), line.getSource(), fileHash, code
            .getFilePath());
      }
      return new ExecutableStatement(line.getLineNumber(), line.getSource(), fileHash);
    }

    @Override
    public boolean isBefore(CodeLine line) {
      return lineNumber < line.getLineNumber();
    }

    @Override
    public boolean isAfter(CodeLine line) {
      return lineNumber > line.getLineNumber();
    }
  }

  /**
   * Represents a statement node that is an omitted block statement.
   * 
   * @author corysmith
   */
  private class NakedNode extends StatementNode {
    public NakedNode(int lineNumber) {
      super(lineNumber);
    }

    public Statement createStatementFor(CodeLine line, Code code) {
      return new OmittedBlockStatement(line.getLineNumber(), line.getSource(), code.getFileHash());
    }
  }

  /**
   * Represents a statement node that is continuation of an omitted block
   * statement.
   * 
   * @author corysmith
   */
  private class ContinuationNode extends StatementNode {
    public ContinuationNode(int lineNumber) {
      super(lineNumber);
    }

    public Statement createStatementFor(CodeLine line, Code code) {
      return new OmittedBlockContinuationStatement(line.getLineNumber(), line.getSource(), code
          .getFileHash());
    }
  }

  /**
   * Represents a simple non-statement node of js code.
   * 
   * @author corysmith
   */
  private static class Node {

    public boolean isBefore(CodeLine line) {
      return false;
    }

    public boolean isAfter(CodeLine line) {
      return false;
    }

    public String toString() {
      return "<EOF>";
    }

    public Statement createStatementFor(CodeLine line, Code code) {
      if (line.getLineNumber() == 1) {
        return new InitNonStatement(line.getSource(), code.getFileHash(), code.getFilePath());
      }
      return new NonStatement(line.getLineNumber(), line.getSource(), code.getFileHash());
    }
  }
}
