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

import java.io.ByteArrayOutputStream;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class SummaryCoverageWriterTest extends TestCase {
  public void testWriteSummary() throws Exception {
    final String a = "foo.js";
    final String b = "zar.js";
    final FileCoverage[] coverages  = new FileCoverage[] {
        new FileCoverage(a, Lists.newArrayList(new CoveredLine(1, 3),
                                               new CoveredLine(2, 0))),
        new FileCoverage(b, Lists.newArrayList(new CoveredLine(1, 3)))
    };
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    SummaryCoverageWriter writer = new SummaryCoverageWriter(out);
    for (FileCoverage coverage : coverages) {
      coverage.write(writer);
    }
    writer.flush();
    assertEquals(String.format("%s: %s%% covered\n" +
                               "%s: %s%% covered\n",
                               a, "50.0", b, "100.0"), out.toString());
  }
}
