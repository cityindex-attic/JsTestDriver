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

import com.google.jstestdriver.token.EscapingWriter;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class EscapingWriterTest extends TestCase {
  public void testEscapeQuotes() throws Exception {
    StringWriter writer = new StringWriter();
    EscapingWriter escaper = new EscapingWriter(writer);
    for (char chr : "'\"".toCharArray()) {
      escaper.write(chr);
    }
    assertEquals("\\'\\\"", writer.toString());
    assertEquals(1, escaper.getEscapedCount('"'));
  }

  public void testEscapeSlashes() throws Exception {
    StringWriter writer = new StringWriter();
    EscapingWriter escaper = new EscapingWriter(writer);
    escaper.write('\\');
    assertEquals("\\\\", writer.toString());
    assertEquals(1, escaper.getEscapedCount('\\'));
  }
  
  public void testEscapeLineBreak() throws Exception {
    StringWriter writer = new StringWriter();
    EscapingWriter escaper = new EscapingWriter(writer);
    escaper.write('\n');
    assertEquals("\\n", writer.toString());
    assertEquals(1, escaper.getEscapedCount('\n'));
    
  }
}
