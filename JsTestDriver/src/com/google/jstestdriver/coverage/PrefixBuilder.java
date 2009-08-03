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

import java.util.LinkedList;
import java.util.List;

class PrefixBuilder {
  private static final String PADDING = "                                                         ";
  public static String COVERAGE_PREFIX = "LCOV";
  private final String lineSource;
  private final String fileHash;
  private final int lineNumber;
  private List<String> prepends = new LinkedList<String>();
  private List<String> appends = new LinkedList<String>();
  private final int totalLines;

  public PrefixBuilder(String lineSource, String fileHash, int lineNumber, int totalLines) {
    this.lineNumber = lineNumber;
    this.fileHash = fileHash;
    this.lineSource = lineSource;
    this.totalLines = totalLines;
  }

  public PrefixBuilder prepend(String prepend) {
    this.prepends.add(prepend);
    return this;
  }

  public PrefixBuilder append(String append) {
    this.appends.add(append);
    return this;
  }

  public String build() {
    StringBuilder source = new StringBuilder("\n");
    for (String prepend : prepends) {
      source.append(prepend);
    }
    source.append(COVERAGE_PREFIX).append("_").append(fileHash).append("[").append(
        lineNumber).append(padding()).append("]++; ").append(lineSource);
    for (String append : appends) {
      source.append(append);
    }
    return source.toString();
  }

  private String padding() {
    int length = new Double(Math.log10(totalLines)).intValue()
        - new Double(Math.log10(lineNumber)).intValue();
    return PADDING.substring(0, length);
  }

  private int prefixLength() {
    return new Double(Math.log10(totalLines)).intValue() + "_[]++;  ".length() + fileHash.length()
        + COVERAGE_PREFIX.length();
  }

  public String buildIndent() {
    return PADDING.substring(0, prefixLength());
  }
}
