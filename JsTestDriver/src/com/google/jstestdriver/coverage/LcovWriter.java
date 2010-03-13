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

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.io.Writer;

/**
 * Writes the code coverage in the LCOV format.
 * @author corysmith@google.com (Cory Smith)
 */
public class LcovWriter implements CoverageWriter {
  private final Writer out;
  private final CoverageNameMapper mapper;

  @Inject
  public LcovWriter(@Named("coverageFileWriter") Writer out,
                    CoverageNameMapper mapper) {
    this.out = out;
    this.mapper = mapper;
  }

  public void writeRecordStart(Integer fileId){
    try {
      out.append("SF:").append(mapper.unmap(fileId)).append("\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeRecordEnd(){
    try {
      out.append("end_of_record\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeCoverage(int lineNumber, int executedNumber){
    try {
      out.append("DA:")
         .append(String.valueOf(lineNumber))
         .append(",")
         .append(String.valueOf(executedNumber))
         .append("\n");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void flush() {
    try {
      out.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
