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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;


public class CodeTest extends TestCase {
  public void testGetLines() throws Exception {
    List<CodeLine> expected = Arrays.asList(
        new CodeLine(1, "foo();"),
        new CodeLine(2, "a=1;"),
        new CodeLine(3, "bar();")
    );
    StringBuilder sourceCode = new StringBuilder();
    for (CodeLine codeLine : expected) {
      sourceCode.append(codeLine.getSource()).append("\n");
    }
    Code code = new Code("foo.js", sourceCode.toString());
    Iterator<CodeLine> expIterator = expected.iterator();
    Iterator<CodeLine> actIterator = code.getLines().iterator();
    while(expIterator.hasNext() && actIterator.hasNext()) {
      assertEquals(expIterator.next(), actIterator.next());
    }
    assertFalse(expIterator.hasNext());
    assertFalse(actIterator.hasNext());
  }
}
