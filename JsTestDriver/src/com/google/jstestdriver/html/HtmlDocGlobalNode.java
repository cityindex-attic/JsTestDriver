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

import com.google.jstestdriver.html.HtmlDocNodeFactory.CreateNodeStrategy;
import com.google.jstestdriver.token.EscapingWriter;
import com.google.jstestdriver.token.Node;
import com.google.jstestdriver.token.Token;

import java.io.IOException;
import java.io.Writer;
import java.util.List;


/**
 * Represents an globally defined HtmlDoc.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class HtmlDocGlobalNode extends HtmlDocNode {

  private static final char[] JS_END_STATEMENT = "this.document);".toCharArray();
  private static final char[] JS_ARG_SEP = "',".toCharArray();
  private static final char[] JS_ASSIGN_HTML = "jstestdriver.addHtmlDocAssign('".toCharArray();
  private static final char[] JS_APPEND_HTML = "jstestdriver.addHtmlDocAppend('".toCharArray();

  public HtmlDocGlobalNode(Token id, List<Token> html) {
    super(id, html);
  }

  public void write(Writer writer) {
    try {
      EscapingWriter escaping = new EscapingWriter(writer);
      // TODO(corysmith): Uncomment this when the the js supports it.
      /*if (id.contains('+')) {
        writer.write(JS_APPEND_HTML);
      } else {
        writer.write(JS_ASSIGN_HTML);
        id.write(escaping);
        writer.write(JS_ARG_SEP);
        writer.write('\'');
      }
      html.write(escaping);
      writer.write(JS_ARG_SEP);
      writer.write(JS_END_STATEMENT);*/
      writer.write("jstestdriver.console.error('Global HTML Doc not supported.');");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static class GlobalNodeStrategy implements CreateNodeStrategy{
    public Node create(Token id, List<Token> html) {
      return new HtmlDocGlobalNode(id, html);
    }
  }
}
