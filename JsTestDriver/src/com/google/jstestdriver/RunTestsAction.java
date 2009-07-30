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
public class RunTestsAction implements ThreadedAction {

  private final List<String> tests;
  private final ResponsePrinterFactory factory;
  private final boolean captureConsole;

  private boolean failures = false;

  public RunTestsAction(List<String> tests, ResponsePrinterFactory factory,
      boolean captureConsole) {
    this.tests = tests;
    this.factory = factory;
    this.captureConsole = captureConsole;
  }

  private TestResultPrinter createPrinter(String id, ResponsePrinterFactory factory) {
    String testSuiteName = String.format("com.google.jstestdriver.%s", id);
    TestResultPrinter printer = factory.getResponsePrinter(String.format("TEST-%s.xml",
        testSuiteName));

    printer.open(testSuiteName);
    return printer;
  }

  public void run(String id, JsTestDriverClient client) {
    TestResultPrinter printer = createPrinter(id, factory);

    if (tests.size() == 1 && tests.get(0).equals("all")) {
      client.runAllTests(id, new ResponseStreamFactoryImpl(printer), captureConsole);
    } else if (tests.size() > 0) {
      client.runTests(id, new  ResponseStreamFactoryImpl(printer), tests, captureConsole);
    }
  }

  public List<String> getTests() {
    return tests;
  }

  public boolean isCaptureConsole() {
    return captureConsole;
  }

  public boolean failures() {
    return failures;
  }
}
