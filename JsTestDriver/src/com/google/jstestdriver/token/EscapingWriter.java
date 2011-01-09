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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

/**
 * Automatically escapes \, ', ", \n, \f, \r when writing to the underlying writer.
 * (Why isn't the Gson Escaper public? Arg!)
 * Not Thread Safe!
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class EscapingWriter extends FilterWriter {

  private static final Map<Character, char[]> REPLACE = ImmutableMap
      .<Character, char[]>builder()
      .put('\\', new char[] {'\\', '\\'})
      .put('\n', new char[] {'\\', 'n'})
      .put('\r', new char[] {'\\', 'r'})
      .put('\f', new char[] {'\\', 'f'})
      .put('\'', new char[] {'\\', '\''})
      .put('"', new char[] {'\\', '"'})
      .build();

  private final char[] escapedBuffer = new char[256];

  private Map<Character, Integer> escapedCounts = Maps.newHashMap();

  public EscapingWriter(Writer out) {
    super(out);
    for (Character key : REPLACE.keySet()) {
      escapedCounts.put(key, 0);
    }
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    int pos = 0;
    for (int i = off; i < len; i++ ) {
      Character character = Character.valueOf(cbuf[i]);
      if (REPLACE.containsKey(character)) {
        pos = writeToBuffer(pos, REPLACE.get(character));
        // TODO(corysmith): find a better method to accomplish this in a performant manner.
        escapedCounts.put(character,  escapedCounts.get(character) + 1);
      } else {
        pos = writeToBuffer(pos, character);
      }
    }
    out.write(escapedBuffer, 0, pos);
  }

  private int writeToBuffer(int pos, char c)  throws IOException {
    if (pos + 1 > escapedBuffer.length) { // flush it
      out.write(escapedBuffer, 0, pos);
      pos = 0;
    }
    escapedBuffer[pos++] = c;
    return pos;
  }

  private int writeToBuffer(int pos, char[] characters) throws IOException {
    if (pos + characters.length > escapedBuffer.length) { // flush it
      out.write(escapedBuffer, 0, pos);
      pos = 0;
    }

    System.arraycopy(characters, 0, escapedBuffer, 0, pos);
    pos += characters.length;
    return pos;
  }

  @Override
  public void write(int c) throws IOException {
    Character chr = Character.valueOf((char)c);
    if (REPLACE.containsKey(chr)) {
      out.write(REPLACE.get(chr));
      // TODO(corysmith): find a better method to accomplish this in a performant manner.
      escapedCounts.put(chr,  escapedCounts.get(chr) + 1);
    } else {
      out.write(c);
    }
  }

  @Override
  public void write(char[] cbuf) throws IOException {
    write(cbuf, 0, cbuf.length);
  }
  
  public int getEscapedCount(char character) {
    return escapedCounts.get(character);
  }

  @Override
  public String toString() {
    return "EscapingWriter [escapedBuffer=" + Arrays.toString(escapedBuffer) + ", escapedCounts="
        + escapedCounts + "]";
  }
}
