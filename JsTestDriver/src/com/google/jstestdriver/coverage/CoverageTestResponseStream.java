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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;
import com.google.jstestdriver.Response.ResponseType;

import java.util.Collection;

/**
 * @author corysmith
 *
 */
public class CoverageTestResponseStream implements ResponseStream {

  public static final String COVERAGE_DATA_KEY = "linesCovered";
  private final String browserId;
  private final CoverageAccumulator accumulator;
  private final TestResultGenerator generator = new TestResultGenerator();
  private final Gson gson = new Gson();
  
  public CoverageTestResponseStream(String browserId, CoverageAccumulator coverageReporter) {
    this.browserId = browserId;
    this.accumulator = coverageReporter;
  }

  public void finish() {
  }

  public void stream(Response response) {
    if (response.getResponseType() != ResponseType.TEST_RESULT) {
      return;
    }
    try {
      Collection<TestResult> testResults = generator.getTestResults(response);
      for (TestResult testResult : testResults) {
        final String coveredLines = testResult.getData().get(COVERAGE_DATA_KEY);
        if (coveredLines != null) {
          Collection<FileCoverage> lines = gson.fromJson(coveredLines,
              new TypeToken<Collection<FileCoverage>>(){}.getType());
          accumulator.add(browserId, lines);
        }
      }
    }catch (RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
