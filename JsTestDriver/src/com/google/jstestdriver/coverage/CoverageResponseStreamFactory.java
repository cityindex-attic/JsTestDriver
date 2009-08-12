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
import com.google.jstestdriver.DefaultResponseStreamFactory;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.ResponseStreamFactory;

/**
 * Wraps the DefaultResponseStreamFactory so that coverage results can be retrieved from every test.
 * @author corysmith
 *
 */
public class CoverageResponseStreamFactory implements ResponseStreamFactory {

  private final DefaultResponseStreamFactory defaultResponseStreamFactory;
  private final CoverageAccumulator coverageaccumulator;

  @Inject
  public CoverageResponseStreamFactory(
      DefaultResponseStreamFactory defaultResponseStreamFactory,
      CoverageAccumulator coverageReporter) {
        this.defaultResponseStreamFactory = defaultResponseStreamFactory;
        this.coverageaccumulator = coverageReporter;
  }

  public ResponseStream getDryRunActionResponseStream() {
    return defaultResponseStreamFactory.getDryRunActionResponseStream();
  }

  public ResponseStream getEvalActionResponseStream() {
    return defaultResponseStreamFactory.getEvalActionResponseStream();
  }

  public ResponseStream getResetActionResponseStream() {
    return defaultResponseStreamFactory.getResetActionResponseStream();
  }

  public ResponseStream getRunTestsActionResponseStream(String browserId) {
    return new CoverageTestResponseStream(
        browserId,
        coverageaccumulator,
        defaultResponseStreamFactory.getRunTestsActionResponseStream(browserId));
  }
}
