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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents code to be instrumented.
 * @author corysmith
 */
public class Code {

  private final String filePath;

  private final String fileHash;

  private final String sourceCode;

  public Code(String filePath, String fileHash, String sourceCode) {
    this.filePath = filePath;
    this.fileHash = fileHash;
    this.sourceCode = sourceCode;
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public String getFileHash() {
    return fileHash;
  }

  public String getFilePath() {
    return filePath;
  }

  public List<CodeLine> getLines() {
    String[] lines = sourceCode.split("\n");
    List<CodeLine> codeLines = new ArrayList<CodeLine>(lines.length);
    for(int i = 0; i < lines.length; i++) {
      codeLines.add(new CodeLine(i + 1, lines[i]));
    }
    return codeLines;
  }
}
