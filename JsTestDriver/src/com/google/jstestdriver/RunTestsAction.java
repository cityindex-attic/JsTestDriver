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

import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class RunTestsAction extends ThreadedAction {

  private final ResponsePrinterFactory responsePrinterFactory;
  private final List<String> tests;
  private final boolean captureConsole;

  public RunTestsAction(ResponseStreamFactory responseStreamFactory,
      ResponsePrinterFactory responsePrinterFactory, List<String> tests, boolean captureConsole) {
    super(responseStreamFactory);
    this.responsePrinterFactory = responsePrinterFactory;
    this.tests = tests;
    this.captureConsole = captureConsole;
  }

  private TestResultPrinter createPrinter(String id) {
    String testSuiteName = String.format("com.google.jstestdriver.%s", id);
    TestResultPrinter printer =
        responsePrinterFactory.getResponsePrinter(String.format("TEST-%s.xml", testSuiteName));

    printer.open(testSuiteName);
    return printer;
  }

  @Override
  public void run(String id, JsTestDriverClient client) {
    ResponseStream runTestsActionResponseStream =
        responseStreamFactory.getRunTestsActionResponseStream();

    // TODO(jeremiele): super ugly, need to find a better way
    if (runTestsActionResponseStream.getClass()
        .isAssignableFrom(RunTestsActionResponseStream.class)) {
      RunTestsActionResponseStream stream =
          (RunTestsActionResponseStream) runTestsActionResponseStream;

      stream.setResponsePrinter(createPrinter(id));
    }
    if (tests.size() == 1 && tests.get(0).equals("all")) {
      client.runAllTests(id, runTestsActionResponseStream, captureConsole);
    } else if (tests.size() > 0) {
      client.runTests(id, runTestsActionResponseStream, tests, captureConsole);
    }
  }

  public List<String> getTests() {
    return tests;
  }

  public boolean isCaptureConsole() {
    return captureConsole;
  }
}
