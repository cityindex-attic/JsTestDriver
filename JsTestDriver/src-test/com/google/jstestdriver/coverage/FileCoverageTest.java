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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class FileCoverageTest extends TestCase {

  public void testAggregate() throws Exception {
    FileCoverage expected = new FileCoverage(1, Lists.newArrayList(new CoveredLine(1, 2)));
    FileCoverage coverOne = new FileCoverage(1, Lists.newArrayList(new CoveredLine(1, 1)));
    FileCoverage coverTwo = new FileCoverage(1, Lists.newArrayList(new CoveredLine(1, 1)));
    FileCoverage actual = coverOne.aggegrate(coverTwo);

    assertEquals(expected, actual);
  }

  public void testAggregateNone() throws Exception {
    FileCoverage coverOne = new FileCoverage(1, Lists.newArrayList(new CoveredLine(1, 1)));
    FileCoverage coverTwo = new FileCoverage(2, Lists.newArrayList(new CoveredLine(1, 1)));
    assertNull(coverOne.aggegrate(coverTwo));
  }
  
  public void testDeserializeCoverages() throws Exception {
    InputStream in =
        new ByteArrayInputStream("[[1,[[0,1],[1, 1], [2,0]]]]".getBytes("UTF-8"));
    List<FileCoverage> coverages =
        new FileCoverageDeserializer().deserializeCoverages(in);
    assertEquals(new FileCoverage(1,
        Lists.newArrayList(new CoveredLine(0, 1),
                           new CoveredLine(1, 1),
                           new CoveredLine(2, 0))), coverages.get(0));
  }
  
  public void testDeserializeMultipleCoverages() throws Exception {
    InputStream in = new ByteArrayInputStream((
        "["+
         "[1,[[0,1],[1,1],[2,0]]],"+
         "[2,[[0,1],[1,1]]],"+
         "[3,[]]"+
        "]").getBytes("UTF-8"));
    List<FileCoverage> coverages =
      new FileCoverageDeserializer().deserializeCoverages(in);
    List<FileCoverage> expected = Lists.newLinkedList();
    expected.add(new FileCoverage(1,
        Lists.newArrayList(new CoveredLine(0, 1),
            new CoveredLine(1, 1),
            new CoveredLine(2, 0))));
    expected.add(new FileCoverage(2,
        Lists.newArrayList(new CoveredLine(0, 1),
            new CoveredLine(1, 1))));
    expected.add(new FileCoverage(3, Lists.<CoveredLine>newArrayList()));
 
    assertEquals(expected, coverages);
  }
}
