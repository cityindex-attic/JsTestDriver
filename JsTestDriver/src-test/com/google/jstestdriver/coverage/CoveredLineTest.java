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

import com.google.gson.Gson;

import junit.framework.TestCase;

/**
 * @author corysmith
 *
 */
public class CoveredLineTest extends TestCase {
  public void testSerializeToJson() throws Exception {
    Gson gson = new Gson();
    final CoveredLine coveredLine = new CoveredLine("foo.js", 1, 1, 1);
    assertEquals(coveredLine, gson.fromJson(gson.toJson(coveredLine), CoveredLine.class));
  }
  
  public void testCompareTo() throws Exception {
    final CoveredLine a = new CoveredLine("foo.js", 1, 1, 2);
    final CoveredLine b = new CoveredLine("zoo.js", 1, 1, 2);
    final CoveredLine c = new CoveredLine("foo.js", 2, 1, 1);
    assertEquals(0, a.compareTo(a));
    assertEquals("foo.js".compareTo("zoo.js"), a.compareTo(b));
    assertEquals("foo.js".compareTo("zoo.js"), a.compareTo(b));
    assertEquals(-b.compareTo(a), a.compareTo(b));
    assertEquals(-1, a.compareTo(c));
    assertEquals(-c.compareTo(a), a.compareTo(c));
  }
  
  public void testAggregate() throws Exception {
    final CoveredLine a = new CoveredLine("foo.js", 1, 1, 1);
    final CoveredLine b = new CoveredLine("foo.js", 1, 2, 1);
    final CoveredLine expected = new CoveredLine("foo.js", 1, 3, 1);
    assertEquals(expected, a.aggegrate(b));
  }
  
  public void testAggregateWrong() throws Exception {
    final CoveredLine a = new CoveredLine("foo.js", 1, 1, 1);
    final CoveredLine b = new CoveredLine("zoo.js", 1, 1, 1);
    assertNull(a.aggegrate(b));
  }
}
