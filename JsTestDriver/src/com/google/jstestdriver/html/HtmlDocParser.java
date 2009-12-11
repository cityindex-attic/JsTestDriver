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

import com.google.jstestdriver.token.BufferedTokenStream;
import com.google.jstestdriver.token.Nodes;
import com.google.jstestdriver.token.Token;


/**
 * Parses htmldoc into a list of nodes.
 * @author corysmith@google.com (Cory Smith)
 */
public class HtmlDocParser {

  public Nodes parse(BufferedTokenStream stream) {
    Nodes nodes = new Nodes();
    
    NodeFactory[] factorys = new NodeFactory[]{
      new HtmlDocNodeFactory(new HtmlDocGlobalNode.GlobalNodeStrategy()),
      new BlockNodeFactory( new NodeFactory[] {
        new HtmlDocNodeFactory(new HtmlDocNestedNode.NestedNodeStrategy()),
        new TextNodeFactory()
      }),
      new TextNodeFactory()
    };
    while(stream.available()) {
      for (NodeFactory factory : factorys) {
        factory.create(stream, nodes);
      }
    }
    return nodes;
  }

  public static class TextNodeFactory implements NodeFactory {
    public void create(BufferedTokenStream stream, Nodes nodes) {
      Token token = stream.read();
      if (token == null) {
        return;
      }
      nodes.add(new TextNode(token));
      stream.mark();
    }
  }

  public interface NodeFactory {
    public abstract void create(BufferedTokenStream stream, Nodes nodes);
  }
}
