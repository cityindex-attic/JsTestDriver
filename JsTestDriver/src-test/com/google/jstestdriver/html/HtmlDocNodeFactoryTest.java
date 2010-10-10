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
 *
 */
public class HtmlDocNodeFactoryTest extends TestCase {
  public void testMultipleHtmlNodes() throws Exception {
    Token docStartToken = ConcreteToken.from("/*:DOC");
    Token barToken = ConcreteToken.from(" bar ");
    Token equalsToken = ConcreteToken.from("=");
    Token htmlTokenOne = ConcreteToken.from(" <div");
    Token htmlTokenTwo = ConcreteToken.from("'foo'></div>");
    Token endDocToken = ConcreteToken.from("*/");
    List<Token> tokens = Lists.<Token>newArrayList(
        docStartToken,
        barToken,
        equalsToken,
        htmlTokenOne,
        equalsToken,
        htmlTokenTwo,
        endDocToken);
    Nodes nodes = new Nodes();
    new HtmlDocNodeFactory(
        new HtmlDocNestedNode.NestedNodeStrategy()).create(
            new BufferedTokenStream(tokens.iterator()), nodes);
    
    assertEquals(new Nodes().add(new HtmlDocNestedNode(barToken,
      Lists.newArrayList(htmlTokenOne, equalsToken, htmlTokenTwo))),
        nodes);
  }
}
