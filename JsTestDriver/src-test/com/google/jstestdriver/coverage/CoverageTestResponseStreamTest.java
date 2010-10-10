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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.Response.ResponseType;

/**
 * @author corysmith@google.com (Cory Smith)
 */
public class CoverageTestResponseStreamTest extends TestCase {

  public void testStreamAndFinish() throws Exception {
    CoverageAccumulator accumulator = new CoverageAccumulator();
    String browserId = "firefox";
    CoverageTestResponseStream stream = new CoverageTestResponseStream(
      browserId, accumulator, new TestResultGenerator());

    Response response = new Response();
    response.setType(ResponseType.TEST_RESULT.name());
    Gson gson = new Gson();
    BrowserInfo browser = new BrowserInfo();
    browser.setName(browserId);
    TestResult testResult = new TestResult(browser, "passed", "passed", "log",
        "test.Foo", "Foo", 1f);
    
    List<FileCoverage> expectedFileCoverage =
        Arrays.asList(new FileCoverage(-1, Lists.newArrayList(new CoveredLine(1, 1))));
    CoverageAccumulator expected = new CoverageAccumulator();
    expected.add(browserId, expectedFileCoverage);
    testResult.getData().put(CoverageTestResponseStream.COVERAGE_DATA_KEY,
        "[[-1,[[1,1]]]]");
    response.setResponse(gson.toJson(Arrays.asList(testResult)));

    stream.stream(response);
    assertEquals(expected, accumulator);
    stream.finish();
  }
  
  public void testStreamNoLinesReturned() throws Exception {
    CoverageAccumulator coverageReporter = new CoverageAccumulator();
    String browserId = "firefox";
    CoverageTestResponseStream stream = new CoverageTestResponseStream(
      browserId, null, new TestResultGenerator());
    
    Response response = new Response();
    response.setType(ResponseType.TEST_RESULT.name());
    Gson gson = new Gson();
    BrowserInfo browser = new BrowserInfo();
    browser.setName(browserId);
    TestResult testResult = new TestResult(browser, "passed", "passed", "log",
      "test.Foo", "Foo", 1f);
    
    CoverageAccumulator expected = new CoverageAccumulator();
    response.setResponse(gson.toJson(Arrays.asList(testResult)));
    
    stream.stream(response);
    assertEquals(expected, coverageReporter);
    stream.finish();
  }

  public final static class ResponseStreamStub implements ResponseStream {
    public boolean finished = false;
    public Response response;

    public void finish() {
      finished = true;
    }

    public void stream(Response response) {
      this.response = response;
    }
  }
}
