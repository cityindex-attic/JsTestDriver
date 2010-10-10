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

import java.io.StringWriter;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class LcovWriterTest extends TestCase {
  public void testWriteCoverage() throws Exception {
    final CoverageNameMapper nameMapper = new CoverageNameMapper();
    final String aName = "foo.js";
    final String bName = "zar.js";
    final int a = nameMapper.map(aName);
    final int b = nameMapper.map(bName);
    final FileCoverage[] coverages  = new FileCoverage[] {
        new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 3),
                                               new CoveredLine(2, 0))),
        new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 3)))
    };
    final StringWriter out = new StringWriter();
    
    CoverageWriter writer = new LcovWriter(out, nameMapper);
    for (FileCoverage coverage : coverages) {
      coverage.write(writer);
    }
    writer.flush();
    assertEquals("SF:" + aName + "\n"
               + "DA:" + 1 + "," + 3 + "\n"
               + "DA:" + 2 + "," + 0 + "\n"
               + "end_of_record\n"
               + "SF:" + bName + "\n" 
               + "DA:" + 1 + "," + 3 + "\n"
               + "end_of_record\n", out.toString());
  }
}
