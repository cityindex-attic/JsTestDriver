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

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class FileCoverageTest extends TestCase {
  public void testAggregate() throws Exception {
    FileCoverage expected = new FileCoverage("foo.js", Lists.newArrayList(new CoveredLine(1, 2)));
    FileCoverage coverOne = new FileCoverage("foo.js", Lists.newArrayList(new CoveredLine(1, 1)));
    FileCoverage coverTwo = new FileCoverage("foo.js", Lists.newArrayList(new CoveredLine(1, 1)));
    FileCoverage actual = coverOne.aggegrate(coverTwo);

    assertEquals(expected, actual);
  }

  public void testAggregateNone() throws Exception {
    FileCoverage coverOne = new FileCoverage("foo.js", Lists.newArrayList(new CoveredLine(1, 1)));
    FileCoverage coverTwo = new FileCoverage("zar.js", Lists.newArrayList(new CoveredLine(1, 1)));
    assertNull(coverOne.aggegrate(coverTwo));
  }
}
