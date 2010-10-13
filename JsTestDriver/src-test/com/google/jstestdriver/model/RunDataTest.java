/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.hooks.JstdTestCaseProcessor;

import junit.framework.TestCase;

import java.util.Collections;
import java.util.List;

public class RunDataTest extends TestCase {

  private final class NoopStream implements ResponseStream {
    public void stream(Response response) {

    }

    public void finish() {

    }
  }

  public void testAggregateresponses() throws Exception {
    FileInfo one = new FileInfo("one", -1, false, false, null);
    ResponseStream streamOne = new NoopStream();
    ResponseStream streamTwo = new NoopStream();
    final RunData runDataOne = new RunData(Lists
        .newArrayList(streamOne), Collections.<JstdTestCase> emptyList(), null);
    final RunData runDataTwo = new RunData(Lists
        .newArrayList(streamTwo), Collections.<JstdTestCase> emptyList(), null);

    assertEquals(new RunData(Lists.newArrayList(
        streamOne, streamTwo), Collections.<JstdTestCase> emptyList(), null),
        runDataOne.aggregateResponses(runDataTwo));
  }

  public void testRecordResponse() throws Exception {
    FileInfo one = new FileInfo("one", -1, false, false, null);
    ResponseStream streamOne = new NoopStream();
    ResponseStream streamTwo = new NoopStream();
    final RunData runDataOne = new RunData(Lists
        .newArrayList(streamOne), Collections.<JstdTestCase> emptyList(), null);

    assertEquals(new RunData(Lists.newArrayList(
        streamOne, streamTwo), Collections.<JstdTestCase> emptyList(), null),
        runDataOne.recordResponse(streamTwo));
  }

  public void testUpdateFileSet() throws Exception {
    ResponseStream streamOne = new NoopStream();
    FileInfo one = new FileInfo("one", -1, false, false, null);
    FileInfo two = new FileInfo("two", -1, false, false, null);
    
    final JstdTestCaseFactory testCaseFactory =
        new JstdTestCaseFactory(Sets.<JstdTestCaseProcessor>newHashSet());
    final List<JstdTestCase> testCases = testCaseFactory.createCases(
        Collections.<FileInfo>emptyList(),
        Lists.newArrayList(one),
        Lists.newArrayList(two));
    final RunData runData = new RunData(Lists.newArrayList(streamOne), testCases, testCaseFactory);
    assertEquals(new RunData(Lists
        .newArrayList(streamOne), testCases, testCaseFactory),
        runData.updateFileSet(Sets.newHashSet(one, two)));
  }
}
