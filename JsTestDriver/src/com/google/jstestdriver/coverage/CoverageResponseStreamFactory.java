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

import com.google.inject.Inject;
import com.google.jstestdriver.Response;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.TestResultGenerator;

/**
 * Wraps the DefaultResponseStreamFactory so that coverage results can be retrieved from every test.
 * @author corysmith
 *
 */
public class CoverageResponseStreamFactory implements ResponseStreamFactory {
  static final ResponseStream NULL_RESPONSE_STREAM = new NullResponseStream();
  private final CoverageAccumulator coverageAccumulator;
  private final TestResultGenerator testResultGenerator;

  @Inject
  public CoverageResponseStreamFactory(CoverageAccumulator coverageAccumulator,
                                       TestResultGenerator testResultGenerator) {
    this.coverageAccumulator = coverageAccumulator;
    this.testResultGenerator = testResultGenerator;
  }

  public ResponseStream getDryRunActionResponseStream() {
    return NULL_RESPONSE_STREAM;
  }

  public ResponseStream getEvalActionResponseStream() {
    return NULL_RESPONSE_STREAM;
  }

  public ResponseStream getResetActionResponseStream() {
    return NULL_RESPONSE_STREAM;
  }

  public ResponseStream getRunTestsActionResponseStream(String browserId) {
    return new CoverageTestResponseStream(browserId, coverageAccumulator,
        testResultGenerator);
  }

  /**
   * A response stream that does nothing.
   * @author corysmith@google.com (Cory Smith)
   */
  public static class NullResponseStream implements ResponseStream {
    public void finish() {
    }

    public void stream(Response response) {
    }
  }
}
