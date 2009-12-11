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
import com.google.jstestdriver.token.ConcreteToken;
import com.google.jstestdriver.token.Token;
import com.google.jstestdriver.token.TokenEmitter;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Acts as a factory for configuring a TokenEmitter and wrapping it in a 
 * BufferedTokenStream from an InputStream.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class HtmlDocLexer{
  private static final Token[] TOKENS = new Token[]{ConcreteToken.from("/*:DOC"),
                                                    ConcreteToken.from("*/"),
                                                    ConcreteToken.from("{"),
                                                    ConcreteToken.from("="),
                                                    ConcreteToken.from("}")};
  
  public BufferedTokenStream createStream(InputStream in) {
    return new BufferedTokenStream(new TokenEmitter(new BufferedInputStream(in), TOKENS));
  }
}
