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
package com.google.jstestdriver.html;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.jstestdriver.token.BufferedTokenStream;
import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Token;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class HtmlDocLexerTest extends TestCase {
  private Token[] tokens = new Token[]{new ConcreteToken("/*:DOC".toCharArray()),
      new ConcreteToken("*/".toCharArray()),
      new ConcreteToken("{".toCharArray()),
      new ConcreteToken("=".toCharArray()),
      new ConcreteToken("}".toCharArray())};

  public void testCreateTokens() throws Exception {
    List<Token> expected = Lists.newArrayList(tokens[0],
      tokens[1], tokens[2], tokens[3]);
    BufferedInputStream stream =
        new BufferedInputStream(
            new ByteArrayInputStream((tokens[0].toString()
                                      + tokens[1].toString()
                                      + tokens[2].toString()
                                      + tokens[3].toString()).getBytes()));
    BufferedTokenStream lexer = new HtmlDocLexer().createStream(stream);
    for (Token token : expected) {
      assertEquals(token, lexer.read());
    }
    assertFalse(lexer.available());
  }

  public void testCreateTokensWithOtherTokens() throws Exception {
    Token otherOne = new ConcreteToken("<div></div>".toCharArray());
    BufferedInputStream stream =
      new BufferedInputStream(
        new ByteArrayInputStream((tokens[0].toString()
            + otherOne.toString() + tokens[1].toString()).getBytes()));

    BufferedTokenStream lexer = new HtmlDocLexer().createStream(stream);
    List<Token> expected =
        Lists.newArrayList(tokens[0], otherOne, tokens[1]);
    for (Token token : expected) {
      assertEquals(token, lexer.read());
    }
  }
  
  public void testCreateTokensFromRealCode() throws Exception {
    BufferedInputStream stream = new BufferedInputStream(
        new ByteArrayInputStream(("TestCase.prototype.setUp = function(){"
                                  + "  this.foo = 1;"
                                  + "  /*:DOC bar = <div></div>*/"
                                  + "};").getBytes()));
    
    BufferedTokenStream lexer = new HtmlDocLexer().createStream(stream);
    List<Token> expected = Lists.newArrayList(
      ConcreteToken.from("TestCase.prototype.setUp "),
      ConcreteToken.from("="),
      ConcreteToken.from(" function()"),
      ConcreteToken.from("{"),
      ConcreteToken.from("  this.foo "),
      tokens[3],
      ConcreteToken.from(" 1;  "),
      tokens[0],
      ConcreteToken.from(" bar "),
      ConcreteToken.from("="),
      ConcreteToken.from(" <div></div>"),
      tokens[1],
      tokens[4]);
    for (Token token : expected) {
      assertEquals(token, lexer.read());
    }
  }

  public void testCreateTokensWithCommentTokens() throws Exception {
    Token otherOne = new ConcreteToken("/** foo".toCharArray());
    BufferedInputStream stream =
      new BufferedInputStream(
        new ByteArrayInputStream((tokens[2].toString()
            + otherOne.toString() + tokens[1].toString() + tokens[3].toString()).getBytes()));
    
    BufferedTokenStream lexer = new HtmlDocLexer().createStream(stream);
    List<Token> expected =
      Lists.newArrayList(tokens[2], otherOne, tokens[1], tokens[3]);
    for (Token token : expected) {
      assertEquals(token, lexer.read());
    }
  }
  
  
  
}
