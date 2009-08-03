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

/**
 * @author corysmith
 *
 */
public class Statement {
  protected static String COVERAGE_PREFIX = "LCOV";

  protected final String lineSource;
  protected final int lineNumber;

  public Statement(int lineNumber, String lineSource) {
    this.lineNumber = lineNumber;
    this.lineSource = lineSource;
  }

  public String getSourceText() {
    return lineSource;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public String toString() {
    return String.format("%s %s: %s\n", getClass().getSimpleName(), lineNumber, lineSource);
  }

  public String toSource(int totalLines, int executableLines) {
    return null;
  }

  public boolean isExecutable() {
    return false;
  }

  /**
   * @param statement
   * @return
   */
  public boolean nest(Statement statement) {
    return false;
  }
}

