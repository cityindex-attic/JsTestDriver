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

import com.google.jstestdriver.Response;
import com.google.jstestdriver.Response.ResponseType;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResultGenerator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author corysmith
 *
 */
public class CoverageTestResponseStream implements ResponseStream {

  public static final String COVERAGE_DATA_KEY = "linesCovered";
  private final String browserId;
  private final CoverageAccumulator accumulator;
  private final TestResultGenerator generator;
  private final FileCoverageDeserializer deserializer = new FileCoverageDeserializer();

  public CoverageTestResponseStream(String browserId, CoverageAccumulator coverageReporter,
      TestResultGenerator generator) {
    this.browserId = browserId;
    this.accumulator = coverageReporter;
    this.generator = generator;
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
        final String coveredLines =
            testResult.getData().get(COVERAGE_DATA_KEY);
        if (coveredLines != null) {
          InputStream inputStream = new ByteArrayInputStream(coveredLines.getBytes("UTF-8"));
          Collection<FileCoverage> lines = deserializer.deserializeCoverages(inputStream);
          accumulator.add(browserId, lines);
        }
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
