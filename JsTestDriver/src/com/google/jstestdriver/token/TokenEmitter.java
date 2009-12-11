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
package com.google.jstestdriver.token;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;

public class TokenEmitter implements Iterator<Token>{
  private final BufferedInputStream stream;
  private final Token[] tokens;
  private Token currentToken = null;
  private TokenEmitter.TokenBuilder tokenBuilder = new TokenBuilder();

  public TokenEmitter(BufferedInputStream stream, Token[] tokens) {
    this.stream = stream;
    this.tokens = tokens;
  }
  
  public Token next() {
    stream.mark(Integer.MAX_VALUE);
    if (currentToken != null) {
      Token nextToken = currentToken;
      currentToken = null;
      return nextToken;
    }
    try {
      while(stream.available() > 0) {
        for (Token token : tokens) {
          Token newToken = token.create(stream);
          if (newToken != null) {
            if (tokenBuilder.hasToken()) {
              currentToken = newToken;
              newToken = tokenBuilder.toToken();
              tokenBuilder = new TokenBuilder();
              return newToken;
            }
            return newToken;
          }
        }
        tokenBuilder.append(stream.read());
        stream.mark(Integer.MAX_VALUE);
      }
      if (currentToken != null) {
        Token nextToken = currentToken;
        currentToken = null;
        return nextToken;
      }
      if (tokenBuilder.hasToken()) {
        Token nextToken = tokenBuilder.toToken();
        tokenBuilder = new TokenBuilder();
        return nextToken;
      }
      throw new IndexOutOfBoundsException();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean hasNext() {
    try {
      return stream.available() > 0 || currentToken != null || tokenBuilder.hasToken();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  /** A optimized buffer for managing an array of characters, and creating tokens from them. */
  public static class TokenBuilder {
    private char[] buffer = new char[256];
    private int marker = 0;

    public void append(int read) {
      if (marker >= buffer.length) {
        char[] oldBuffer = buffer;
        buffer = new char[oldBuffer.length * 2];
        System.arraycopy(oldBuffer, 0, buffer, 0, marker);
      }
      buffer[marker++] = (char)read;
    }

    public boolean hasToken() {
      return marker > 0;
    }

    public Token toToken() {
      char out[] = new char[marker];
      System.arraycopy(buffer, 0, out, 0, marker);
      return new ConcreteToken(out);
    }
  }
}