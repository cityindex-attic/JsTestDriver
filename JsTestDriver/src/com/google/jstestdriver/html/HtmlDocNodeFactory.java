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

import com.google.common.collect.Lists;
import com.google.jstestdriver.html.HtmlDocParser.NodeFactory;
import com.google.jstestdriver.token.BufferedTokenStream;
import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Node;
import com.google.jstestdriver.token.Nodes;
import com.google.jstestdriver.token.Token;

import java.util.List;

public class HtmlDocNodeFactory implements NodeFactory {
  private static final Token DOC_START = ConcreteToken.from("/*:DOC");
  private static final Token EQUALS = ConcreteToken.from("=");
  private static final Token END_COMMENT= ConcreteToken.from("*/");

  private final CreateNodeStrategy createStrategy;

  public HtmlDocNodeFactory(CreateNodeStrategy createStrategy) {
    this.createStrategy = createStrategy;
  }

  public void create(BufferedTokenStream stream, Nodes nodes) {
    Token id = null;
    List<Token> html = Lists.newLinkedList();

    if (!DOC_START.equals(stream.read())) {
      stream.reset();
      return;
    }

    id = stream.read();

    if (!EQUALS.equals(stream.read())) {
      stream.reset();
      return;
    }

    while(stream.available()) {
      Token token = stream.read();
      if (END_COMMENT.equals(token)) {
        break;
      }
      html.add(token);
    }

    stream.mark();
    nodes.add(createStrategy.create(id, html));
  }
  
  public interface CreateNodeStrategy {
    public Node create(Token id, List<Token> html);
  }
}
