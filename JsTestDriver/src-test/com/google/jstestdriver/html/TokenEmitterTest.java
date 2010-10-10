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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Token;
import com.google.jstestdriver.token.TokenEmitter;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class TokenEmitterTest extends TestCase {
  public void testEmitTokens() throws Exception {
    ByteArrayInputStream stream = new ByteArrayInputStream("{}".getBytes());
    
    TokenEmitter emitter = new TokenEmitter(new BufferedInputStream(stream), new Token[]{
      ConcreteToken.from("{"),
      ConcreteToken.from("}"),
    });
    assertTrue(emitter.hasNext());
    assertEquals(ConcreteToken.from("{"), emitter.next());
    assertTrue(emitter.hasNext());
    assertEquals(ConcreteToken.from("}"), emitter.next());
    assertFalse(emitter.hasNext());
  }

  public void testEmitFallThroughTokens() throws Exception {
    ByteArrayInputStream stream = new ByteArrayInputStream("foo,bar,foo}".getBytes());
    
    TokenEmitter emitter = new TokenEmitter(new BufferedInputStream(stream), new Token[]{
      ConcreteToken.from("}"),
    });
    assertTrue(emitter.hasNext());
    assertEquals(ConcreteToken.from("foo,bar,foo"), emitter.next());
    assertTrue(emitter.hasNext());
    assertEquals(ConcreteToken.from("}"), emitter.next());
    assertFalse(emitter.hasNext());
  }
}
