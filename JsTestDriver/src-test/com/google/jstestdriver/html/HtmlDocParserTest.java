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

import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.jstestdriver.token.BufferedTokenStream;
import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Nodes;
import com.google.jstestdriver.token.Token;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class HtmlDocParserTest extends TestCase {

  public void testParseHtmlDoc() throws Exception {
    Token id = ConcreteToken.from("foo ");
    Token html = ConcreteToken.from(" <div></div>");
    List<Token> tokens = Lists.<Token>newArrayList(
                       ConcreteToken.from("/*:DOC"),
                       id,
                       ConcreteToken.from("="),
                       html,
                       ConcreteToken.from("*/"));
    
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());
    
    assertEquals(new Nodes().add(new HtmlDocGlobalNode(id, Lists.newArrayList(html))), 
                 new HtmlDocParser().parse(stream));
  }

  public void testParseHtmlDocInBlock() throws Exception {
    Token id = ConcreteToken.from("foo ");
    Token html = ConcreteToken.from(" <div></div>");
    Token functionToken = ConcreteToken.from("function foo()");
    Token blockStartToken = ConcreteToken.from("{");
    Token blockEndToken = ConcreteToken.from("}");
    List<Token> tokens = Lists.<Token>newArrayList(
                       functionToken,
                       blockStartToken,
                       ConcreteToken.from("/*:DOC"),
                       id,
                       ConcreteToken.from("="),
                       html,
                       ConcreteToken.from("*/"),
                       blockEndToken);
    
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());
    
    assertEquals(new Nodes().add(new TextNode(functionToken))
                            .add(new TextNode(blockStartToken))
                            .add(new HtmlDocNestedNode(id, Lists.newArrayList(html)))
                            .add(new TextNode(blockEndToken)), 
                 new HtmlDocParser().parse(stream));
  }

  public void testOtherAndBlock() throws Exception {
    Token setUpToken = ConcreteToken.from("TestCase.prototype.setUp ");
    Token equalsToken = ConcreteToken.from("=");
    Token functionToken = ConcreteToken.from(" function()");
    Token blockStartToken = ConcreteToken.from("{");
    Token fooToken = ConcreteToken.from("  this.foo ");
    Token oneToken = ConcreteToken.from(" 1;  ");
    Token docStartToken = ConcreteToken.from("/*:DOC");
    Token barToken = ConcreteToken.from(" bar ");
    Token htmlToken = ConcreteToken.from(" <div></div>");
    Token endDocToken = ConcreteToken.from("*/");
    Token endBlockToken = ConcreteToken.from("}");
    List<Token> tokens = Lists.<Token>newArrayList(
        setUpToken,
        equalsToken,
        functionToken,
        blockStartToken,
        fooToken,
        equalsToken,
        oneToken,
        docStartToken,
        barToken,
        equalsToken,
        htmlToken,
        endDocToken,
        endBlockToken);
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());

    assertEquals(new Nodes().add(new TextNode(setUpToken))
                            .add(new TextNode(equalsToken))
                            .add(new TextNode(functionToken))
                            .add(new TextNode(blockStartToken))
                            .add(new TextNode(fooToken))
                            .add(new TextNode(equalsToken))
                            .add(new TextNode(oneToken))
                            .add(new HtmlDocNestedNode(barToken, Lists.newArrayList(htmlToken)))
                            .add(new TextNode(endBlockToken)), 
                 new HtmlDocParser().parse(stream));
  }
}
