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

import java.io.StringWriter;
import java.util.Arrays;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Node;

/**
 * @author corysmith@google.com (Cory Smith)
 * 
 */
public class HtmlDocNestedNodeTest extends TestCase {
  public void testVariableAssignment() throws Exception {
    String id = " foo";
    String html = "<div>\n'foo'</div>";
    Node node =
        new HtmlDocNestedNode(ConcreteToken.from(id), Lists.newArrayList(ConcreteToken.from(html)));

    StringWriter writer = new StringWriter();
    node.write(writer);
    assertEquals(
            String.format("this.%s = jstestdriver.toHtml('%s',window.document);", id,
                html.replace("\n", "\\n").replace("'", "\\'")) + "\n", writer.toString());
  }

  public void testAppend() throws Exception {
    String id = " +";
    String html = "<div>\n</div>";
    Node node =
        new HtmlDocNestedNode(ConcreteToken.from(id), Lists.newArrayList(ConcreteToken.from(html)));

    StringWriter writer = new StringWriter();
    node.write(writer);
    assertEquals(
            String.format("jstestdriver.appendHtml('%s',window.document);",
                html.replace("\n", "\\n").replace("'", "\\'")) + "\n", writer.toString());
  }
  
  public void testAppendMultiline() throws Exception {
    String id = " +";
    char[] breaks = new char[5];
    Arrays.fill(breaks, '\n');
    String html = "<div>" + new String(breaks) + "</div>";

    Node node =
        new HtmlDocNestedNode(ConcreteToken.from(id), Lists.newArrayList(ConcreteToken.from(html)));

    StringWriter writer = new StringWriter();
    node.write(writer);
    assertEquals(
            String.format("jstestdriver.appendHtml('%s',window.document);",
                html.replace("\n", "\\n").replace("'", "\\'")) + new String(breaks),
        writer.toString());
  }
}
