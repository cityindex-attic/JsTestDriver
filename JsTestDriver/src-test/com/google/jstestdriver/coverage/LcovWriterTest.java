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

import com.google.common.collect.Lists;

import junit.framework.TestCase;

import java.io.StringWriter;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class LcovWriterTest extends TestCase {
  public void testWriteCoverage() throws Exception {
    final String a = "foo.js";
    final String b = "zar.js";
    final FileCoverage[] coverages  = new FileCoverage[] {
        new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 3),
                                               new CoveredLine(2, 0))),
        new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 3)))
    };
    final StringWriter out = new StringWriter();
    
    CoverageWriter writer = new LcovWriter(out);
    for (FileCoverage coverage : coverages) {
      coverage.write(writer);
    }
    writer.flush();
    assertEquals("SF:" + a + "\n"
               + "DA:" + 1 + "," + 3 + "\n"
               + "DA:" + 2 + "," + 0 + "\n"
               + "end_of_record\n"
               + "SF:" + b + "\n" 
               + "DA:" + 1 + "," + 3 + "\n"
               + "end_of_record\n", out.toString());
  }
}
