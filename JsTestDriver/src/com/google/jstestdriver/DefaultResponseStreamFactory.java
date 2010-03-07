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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.jstestdriver.DryRunAction.DryRunActionResponseStream;
import com.google.jstestdriver.EvalAction.EvalActionResponseStream;
import com.google.jstestdriver.ResetAction.ResetActionResponseStream;
import com.google.jstestdriver.output.TestResultListener;

import java.io.PrintStream;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 * 
 */
public class DefaultResponseStreamFactory implements ResponseStreamFactory {

  private final Provider<TestResultListener> responsePrinterFactory;
  private final String configFileName;
  private final FailureAccumulator accumulator;
  private final PrintStream out;

  @Inject
  public DefaultResponseStreamFactory(Provider<TestResultListener> responsePrinterFactory,
                                      @Named("config") String configFileName,
                                      FailureAccumulator accumulator,
                                      @Named("outputStream") PrintStream out) {
    this.responsePrinterFactory = responsePrinterFactory;
    this.configFileName = configFileName;
    this.accumulator = accumulator;
    this.out = out;
  }

  public ResponseStream getRunTestsActionResponseStream(String browserId) {
    String testSuiteName =
        String.format("com.google.jstestdriver.%s", browserId);
    TestResultListener listener = responsePrinterFactory.get();

    RunTestsActionResponseStream responseStream = new RunTestsActionResponseStream(
        new TestResultGenerator(), listener, accumulator);

    return responseStream;
  }

  public ResponseStream getDryRunActionResponseStream() {
    return new DryRunActionResponseStream(out);
  }

  public ResponseStream getEvalActionResponseStream() {
    return new EvalActionResponseStream(out);
  }

  public ResponseStream getResetActionResponseStream() {
    return new ResetActionResponseStream(out);
  }
}
