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

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Node;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class HtmlDocGlobalNodeTest extends TestCase {

  public void testVariableAssignment() throws Exception {
    String id = " foo";
    String html = "<div>\n'foo'</div>";
    Node node = new HtmlDocGlobalNode(ConcreteToken.from(id), Lists
      .newArrayList(ConcreteToken.from(html)));

    StringWriter writer = new StringWriter();
    node.write(writer);
    assertEquals(
      "jstestdriver.console.error('Global HTML Doc not supported.');", writer
        .toString());
    /*
     * assertEquals(String.format("jstestdriver.addHtmlDocAssign('%s','%s',this);"
     * , id, html.replace("\n", "\\n").replace("'", "\\'")), writer.toString());
     */
  }

  public void testGlobalAppend() throws Exception {
    String id = " +";
    String html = "<div>\n</div>";
    Node node = new HtmlDocGlobalNode(ConcreteToken.from(id),
      Lists.newArrayList(ConcreteToken.from(html)));

    StringWriter writer = new StringWriter();
    node.write(writer);
    assertEquals(
      "jstestdriver.console.error('Global HTML Doc not supported.');", writer
        .toString());
    /*
     * assertEquals(String.format("jstestdriver.addHtmlDocAppend('%s',this);",
     * html.replace("\n", "\\n").replace("'", "\\'")), writer.toString());
     */
  }
}
