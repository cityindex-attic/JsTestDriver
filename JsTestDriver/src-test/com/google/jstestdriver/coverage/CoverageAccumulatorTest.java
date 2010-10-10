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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

/**
 * @author corysmith@google.com (Cory Smith)
 * 
 */
public class CoverageAccumulatorTest extends TestCase {

  public void testWrite() throws Exception {
    final Integer a = 1;
    final Integer b = 2;

    final List<FileCoverage> expected = Arrays.asList(
        new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 2),
                                               new CoveredLine(2, 2),
                                               new CoveredLine(3, 3),
                                               new CoveredLine(4, 0))),
        new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 3))));

    final CoverageAccumulator accumulator = new CoverageAccumulator();
    final CoverageWriterFake coverageWriter = new CoverageWriterFake();

    accumulator.add("ff", Arrays.asList(
      new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 1))),
      new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 1),
                                             new CoveredLine(2, 1),
                                             new CoveredLine(3, 1),
                                             new CoveredLine(4, 0))))
    );

    accumulator.add("op", Arrays.asList(
      new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 1))),
      new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 0),
                                             new CoveredLine(2, 0),
                                             new CoveredLine(3, 1),
                                             new CoveredLine(4, 0)))
    ));

    accumulator.add("ie", Arrays.asList(
      new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 1),
                                             new CoveredLine(2, 1),
                                             new CoveredLine(3, 1),
                                             new CoveredLine(4, 0))),
      new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 1)))
    ));

    accumulator.write(coverageWriter);

    coverageWriter.assertLines(expected);
  }

  private final class CoverageWriterFake implements CoverageWriter {
    List<CoveredLine> lines = new LinkedList<CoveredLine>();
    List<FileCoverage> coveredLines = new ArrayList<FileCoverage>();
    

    public void assertLines(List<FileCoverage> expected) {
      assertEquals(expected, coveredLines);
    }

    public void flush() {
    }

    public void writeCoverage(int lineNumber, int executedNumber) {
      lines.add(new CoveredLine(lineNumber, executedNumber));
    }

    public void writeRecordEnd() {
    }

    public void writeRecordStart(Integer fileId) {
      lines = new LinkedList<CoveredLine>();
      this.coveredLines.add(new FileCoverage(fileId, lines));
    }
  }
}
