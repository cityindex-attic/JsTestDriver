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
import com.google.jstestdriver.token.Token;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class BufferedTokenStreamTest extends TestCase {

  List<Token> tokens;
  
  @Override
  protected void setUp() throws Exception {
    tokens = Lists.<Token>newArrayList(new ConcreteToken("1".toCharArray()),
                                       new ConcreteToken("2".toCharArray()),
                                       new ConcreteToken("3".toCharArray()),
                                       new ConcreteToken("4".toCharArray()));
  }
  
  public void testRead() throws Exception {
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());
    assertEquals(tokens.get(0), stream.read());
  }

  public void testReadReset() throws Exception {
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());
    assertEquals(tokens.get(0), stream.read());
    stream.reset();
    assertEquals(tokens.get(0), stream.read());
  }
  
  public void testReadMarkReset() throws Exception {
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());
    assertEquals(tokens.get(0), stream.read());
    assertEquals(tokens.get(1), stream.read());
    stream.mark();
    assertEquals(tokens.get(2), stream.read());
    assertEquals(tokens.get(3), stream.read());
    stream.reset();
    assertEquals(tokens.get(2), stream.read());
  }
  
  public void testReadEndOfStream() throws Exception {
    BufferedTokenStream stream = new BufferedTokenStream(Lists.<Token>newArrayList().iterator());
    assertEquals(null, stream.read());
  }
  
  public void testReadAvailable() throws Exception {
    BufferedTokenStream stream = new BufferedTokenStream(tokens.iterator());
    assertEquals(tokens.get(0), stream.read());
    assertEquals(tokens.get(1), stream.read());
    assertTrue(stream.available());
    stream.mark();
    assertTrue(stream.available());
    assertEquals(tokens.get(2), stream.read());
    assertEquals(tokens.get(3), stream.read());
    assertFalse(stream.available());
    assertNull(stream.read());
    stream.reset();
    assertTrue(stream.available());
  }
}
