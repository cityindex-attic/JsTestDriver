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

package com.google.jstestdriver;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.hooks.TestsPreProcessor;
import com.google.jstestdriver.model.JstdTestCase;
import com.google.jstestdriver.model.RunData;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class RunTestsActionTest extends TestCase {

  public void testRunTests() throws Exception {
    final ResponseStreamStub stream = new ResponseStreamStub();
    final List<String> tests = Lists.<String> newArrayList("TestCase.testNomable");
    final boolean captureConsole = false;
    final String browserId = "webNom";
    final FakeJsTestDriverClient.TestRun expected =
        new FakeJsTestDriverClient.TestRun(browserId, stream, tests, captureConsole);

    final RunTestsAction action =
        new RunTestsAction(new FakeResponseStreamFactory(stream), tests, captureConsole,
            Collections.<TestsPreProcessor> emptySet());
    final FakeJsTestDriverClient client = new FakeJsTestDriverClient(Collections.<BrowserInfo>emptyList());
    action.run(browserId, client, new RunData(
        Collections.<ResponseStream>emptyList(),
        Collections.<JstdTestCase>emptyList(),
        null), null);

    client.assertTestRun(expected);
  }

  public void testRunTestsPreProcessed() throws Exception {
    final ResponseStreamStub stream = new ResponseStreamStub();
    final List<String> tests = Lists.<String> newArrayList("TestCase.testNotNomable");
    final List<String> expectedTests = Lists.<String> newArrayList("TestCase.testNomable");
    final boolean captureConsole = false;
    final String browserId = "webNom";
    final FakeJsTestDriverClient.TestRun expected =
        new FakeJsTestDriverClient.TestRun(browserId, stream, expectedTests, captureConsole);
    final Set<TestsPreProcessor> preProcessors =
        Sets.<TestsPreProcessor> newHashSet(new TestsPreProcessor() {
          public List<String> process(String actualBrowserId, Iterator<String> actualTests) {
            assertEquals(tests, Lists.newArrayList(tests));
            assertEquals(browserId, actualBrowserId);
            return expectedTests;
          }
        });

    final RunTestsAction action =
        new RunTestsAction(new FakeResponseStreamFactory(stream), tests, captureConsole,
            preProcessors);
    final FakeJsTestDriverClient client = new FakeJsTestDriverClient(Collections.<BrowserInfo>emptyList());
    action.run(browserId, client, new RunData(Collections.<ResponseStream>emptyList(), Collections.<JstdTestCase>emptyList(), null), null);

    client.assertTestRun(expected);
  }

  private static final class FakeResponseStreamFactory implements ResponseStreamFactory {

    private final ResponseStreamStub stream;

    public FakeResponseStreamFactory(ResponseStreamStub stream) {
      this.stream = stream;
    }

    public ResponseStream getRunTestsActionResponseStream(String browserId) {
      return stream;
    }

    public ResponseStream getResetActionResponseStream() {
      return null;
    }

    public ResponseStream getEvalActionResponseStream() {
      return null;
    }

    public ResponseStream getDryRunActionResponseStream() {
      return null;
    }
  }

  private static final class ResponseStreamStub implements ResponseStream {
    public void stream(Response response) {
    }

    public void finish() {
    }
  }
}
