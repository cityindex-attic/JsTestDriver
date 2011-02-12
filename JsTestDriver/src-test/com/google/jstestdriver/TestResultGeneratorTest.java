/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.jstestdriver.Response.ResponseType;
import com.google.jstestdriver.model.NullPathPrefix;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class TestResultGeneratorTest extends TestCase {

  public void testGetTestResults() throws Exception {
    TestResult expected =
        new TestResult(new BrowserInfo(), "passed", "Message", "log", "testCase", "TestName", 0f);
    Gson gson = new Gson();
    Collection<TestResult> results = new ArrayList<TestResult>();
    results.add(expected);
    String gsonString = gson.toJson(results);
    TestResultGenerator generator =
        new TestResultGenerator(new FailureParser(new NullPathPrefix()));
    Response response = new Response();
    response.setType(ResponseType.TEST_RESULT.name());
    response.setResponse(gsonString);
    response.setBrowser(new BrowserInfo());
    Collection<TestResult> testResults = generator.getTestResults(response);
    assertEquals(1, testResults.size());
    TestResult actual = testResults.iterator().next();

    assertEquals(expected.getResult(), actual.getResult());
    assertEquals(expected.getMessage(), actual.getMessage());
    assertEquals(expected.getLog(), actual.getLog());
    assertEquals(expected.getTestName(), actual.getTestName());
    assertEquals(expected.getTestCaseName(), actual.getTestCaseName());
  }
}
