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

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * Acts as a buffer for an Iterator<Token>.
 * @author corysmith@google.com (Cory Smith)
 */
public class BufferedTokenStream {
  private final Iterator<Token> iterator;
  private int streamPos = 0;
  private int bufferIndex = 0;
  private final int readAhead = 2;
  private List<Token> buffer = Lists.newArrayList();

  public BufferedTokenStream(Iterator<Token> iterator) {
    this.iterator = iterator;
  }

  public Token read() {
    while(iterator.hasNext() && (buffer.size() - (bufferIndex + 1)) < readAhead) {
      streamPos++;
      buffer.add(iterator.next());
    }
    if (isBufferEmpty()) {
      return null;
    }
    Token token = buffer.get(bufferIndex++);
    return token;
  }

  private boolean isBufferEmpty() {
    return buffer.isEmpty() || bufferIndex + 1 > buffer.size();
  }

  public boolean available() {
    return iterator.hasNext() || !isBufferEmpty();
  }

  public void mark() {
    List<Token> oldBuffer = buffer;
    buffer = Lists.newArrayList();
    buffer.addAll(oldBuffer.subList(bufferIndex, oldBuffer.size()));
    bufferIndex = 0;
  }

  public void reset() {
    bufferIndex = 0;
  }
}
