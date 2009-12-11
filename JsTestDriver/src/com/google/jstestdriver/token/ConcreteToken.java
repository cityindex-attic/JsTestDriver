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
import java.io.Writer;
import java.util.Arrays;

public class ConcreteToken implements Token {

  /** Convenience method for creating a token from a string.*/
  public static Token from(String token) {
    return new ConcreteToken(token.toCharArray());
  }

  private final char[] token;

  public ConcreteToken(char[] token) {
    this.token = token;
  }

  public Token create(BufferedInputStream stream) {
    try {
      for (int i = 0; i < token.length; i++) {
        char read = (char) stream.read();
        if (token[i] != read) {
          stream.reset();
          return null;
        }
      }
      stream.mark(Integer.MAX_VALUE);
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConcreteToken other = (ConcreteToken) obj;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!Arrays.equals(token, other.token))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return new String(token);
  }

  /** Writes the token to the writer. */
  public void write(Writer out) {
    for (char character : token) {
      try {
        out.append(character);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public boolean contains(char chr) {
    for (char character : token) {
      if (character == chr) {
        return true;
      }
    }
    return false;
  }
}