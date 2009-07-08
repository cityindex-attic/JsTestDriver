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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
      client.runTests(id, new  ResponseStreamFactoryImpl(printer), createTestCaseList(tests),
          captureConsole);
    }
  }

  private Map<String, List<String>> createTestCaseMap(List<String> tests) {
    Map<String, List<String>> testCaseMap = new LinkedHashMap<String, List<String>>();

    for (String test : tests) {
      String[] pair = test.split("\\.");
      List<String> list = testCaseMap.get(pair[0]);

      if (list == null) {
        list = new ArrayList<String>();

        testCaseMap.put(pair[0], list);
      }
      if (pair.length == 2) {
        list.add(pair[1]);
      } else if (pair.length == 3 && pair[1].equals("prototype")) {
        list.add(pair[2]);
      } else if (pair.length > 3) {
        throw new IllegalArgumentException(test + " is not a valid test");
      }
    }
    return testCaseMap;
  }

  private List<TestCase> createTestCaseList(List<String> tests) {
    Map<String, List<String>> testCasesMap = createTestCaseMap(tests);
    List<TestCase> testCases = new ArrayList<TestCase>();

    for (Map.Entry<String, List<String>> entry : testCasesMap.entrySet()) {
      testCases.add(new TestCase(entry.getKey(), entry.getValue()));
    }
    return testCases;
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
