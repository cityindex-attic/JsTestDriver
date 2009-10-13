// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.jstestdriver.hooks.TestsPreProcessor;

import junit.framework.TestCase;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class RunTestsActionTest extends TestCase {

  public void testRunTests() throws Exception {
    final ResponseStreamStub stream = new ResponseStreamStub();
    final List<String> tests = Lists.<String> newArrayList("TestCase.testNomable");
    final boolean captureConsole = false;
    final String browserId = "webNom";
    final MockJsTestDriverClient.TestRun expected =
        new MockJsTestDriverClient.TestRun(browserId, stream, tests, captureConsole);

    final RunTestsAction action =
        new RunTestsAction(new FakeResponseStreamFactory(stream), tests, captureConsole,
            Collections.<TestsPreProcessor> emptySet());
    final MockJsTestDriverClient client = new MockJsTestDriverClient();
    action.run(browserId, client);

    client.assertTestRun(expected);
  }

  public void testRunTestsPreProcessed() throws Exception {
    final ResponseStreamStub stream = new ResponseStreamStub();
    final List<String> tests = Lists.<String> newArrayList("TestCase.testNotNomable");
    final List<String> expectedTests = Lists.<String> newArrayList("TestCase.testNomable");
    final boolean captureConsole = false;
    final String browserId = "webNom";
    final MockJsTestDriverClient.TestRun expected =
        new MockJsTestDriverClient.TestRun(browserId, stream, expectedTests, captureConsole);
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
    final MockJsTestDriverClient client = new MockJsTestDriverClient();
    action.run(browserId, client);

    client.assertTestRun(expected);
  }

  private static final class MockJsTestDriverClient implements JsTestDriverClient {

    public TestRun testRun = null;

    public void runTests(String id, ResponseStream responseStream, List<String> tests,
        boolean captureConsole) {
      testRun = new TestRun(id, responseStream, tests, captureConsole);
    }

    public void runAllTests(String id, ResponseStream responseStream, boolean captureConsole) {
    }

    public void reset(String id, ResponseStream responseStream) {
    }

    public Collection<BrowserInfo> listBrowsers() {
      return null;
    }

    public void eval(String id, ResponseStream responseStream, String cmd) {
    }

    public void dryRunFor(String id, ResponseStream responseStream, List<String> expressions) {
    }

    public void dryRun(String id, ResponseStream responseStream) {
    }

    public void assertTestRun(TestRun expected) {
      assertEquals(expected, testRun);
    }

    public static class TestRun {

      private final String id;
      private final ResponseStream responseStream;
      private final List<String> tests;
      private final boolean captureConsole;

      public TestRun(String id, ResponseStream responseStream, List<String> tests,
          boolean captureConsole) {
        this.id = id;
        this.responseStream = responseStream;
        this.tests = tests;
        this.captureConsole = captureConsole;
      }

      @Override
      public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (captureConsole ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((responseStream == null) ? 0 : responseStream.hashCode());
        result = prime * result + ((tests == null) ? 0 : tests.hashCode());
        return result;
      }

      @Override
      public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TestRun other = (TestRun) obj;
        if (captureConsole != other.captureConsole) return false;
        if (id == null) {
          if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (responseStream == null) {
          if (other.responseStream != null) return false;
        } else if (!responseStream.equals(other.responseStream)) return false;
        if (tests == null) {
          if (other.tests != null) return false;
        } else if (!tests.equals(other.tests)) return false;
        return true;
      }
    }
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
