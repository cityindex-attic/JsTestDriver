// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.jstestdriver.model.RunData;

import java.util.Collection;
import java.util.List;

final public class FakeJsTestDriverClient implements JsTestDriverClient {

  public FakeJsTestDriverClient.TestRun testRun = null;
  private final List<BrowserInfo> browsers;

  public FakeJsTestDriverClient(List<BrowserInfo> browsers) {
    this.browsers = browsers;
  }

  public void runTests(String id, ResponseStream responseStream, List<String> tests,
      boolean captureConsole, RunData runData) {
    testRun = new TestRun(id, responseStream, tests, captureConsole);
  }

  public void runAllTests(String id, ResponseStream responseStream, boolean captureConsole, RunData runData) {
  }

  public void reset(String id, ResponseStream responseStream, RunData runData) {
  }

  public Collection<BrowserInfo> listBrowsers() {
    return browsers;
  }

  public void eval(String id, ResponseStream responseStream, String cmd, RunData runData) {
  }

  public void dryRunFor(String id, ResponseStream responseStream, List<String> expressions, RunData runData) {
  }

  public void dryRun(String id, ResponseStream responseStream, RunData runData) {
  }

  public void assertTestRun(FakeJsTestDriverClient.TestRun expected) {
    RunTestsActionTest.assertEquals(expected, testRun);
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
      FakeJsTestDriverClient.TestRun other = (FakeJsTestDriverClient.TestRun) obj;
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

  public String getNextBrowserId() {
    return null;
  }
  public void uploadFiles(String browserId, RunData runData) {
    
  }
}