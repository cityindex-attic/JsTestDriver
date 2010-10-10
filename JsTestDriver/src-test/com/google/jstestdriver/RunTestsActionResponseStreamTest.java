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

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.jstestdriver.Response.ResponseType;
import com.google.jstestdriver.output.TestResultListener;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class RunTestsActionResponseStreamTest extends TestCase {

  private final class TestResultListenerStub implements TestResultListener {
    public boolean finished;
    public TestResult testResult;

    public void finish() {
      finished = true;
    }

    public void onTestComplete(TestResult testResult) {
      this.testResult = testResult;
    }

    public void onFileLoad(String browser, FileResult fileResult) {
      
    }
  }

  public void testPrint() throws Exception {
    Gson gson = new Gson();
    TestResultListenerStub printer = new TestResultListenerStub();
    RunTestsActionResponseStream stream = new RunTestsActionResponseStream(
      new TestResultGenerator(), printer, new FailureAccumulator());
    Response response = new Response();
    response.setType(ResponseType.TEST_RESULT.name());
    BrowserInfo browser = new BrowserInfo();
    browser.setId(null);
    TestResult testResult = new TestResult(browser, "passed",
        "passed", "log", "foo", "name", 1.0f);
    response.setResponse(gson.toJson(Collections.singletonList(testResult)));
    stream.stream(response);
    assertEquals(testResult.getResult(), printer.testResult.getResult());
    assertEquals(testResult.getBrowserInfo(), printer.testResult.getBrowserInfo());
    assertFalse(printer.finished);
    stream.finish();
    assertTrue(printer.finished);
  }
}
