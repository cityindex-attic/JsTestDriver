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

import com.google.gson.Gson;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class XmlPrinter implements TestResultPrinter {

  private static final String NEW_LINE = System.getProperty("line.separator");

  private final Gson gson = new Gson();
  private final AtomicInteger totalPasses = new AtomicInteger(0);
  private final AtomicInteger totalFails = new AtomicInteger(0);
  private final AtomicInteger totalErrors = new AtomicInteger(0);

  private final PrintStream out;
  private final TestXmlSerializer serializer;
  private final AtomicInteger browsers;
  private final ConcurrentHashMap<String, RunData> browsersRunData;
  private final RunData runData = new RunData();

  public XmlPrinter(PrintStream out, TestXmlSerializer serializer, AtomicInteger browsers,
      ConcurrentHashMap<String, RunData> browsersRunData) {
    this.out = out;
    this.serializer = serializer;
    this.browsers = browsers;
    this.browsersRunData = browsersRunData;
  }

  public void open(String name) {
    serializer.startTestSuite(name);
  }

  // TODO(jeremiele): I know what you think, I think it too...
  private float findMaxTime() {
    float max = 0f;

    for (RunData data : browsersRunData.values()) {
      max = Math.max(data.getTotalTime(), max);
    }
    return max;
  }

  private int getTotalRan() {
    int totalRan = 0;

    for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
      RunData data = entry.getValue();

      totalRan += data.getPassed() + data.getFailed() + data.getErrors();
    }
    return totalRan;
  }

  private int getTotalPassed() {
    int totalPassed = 0;

    for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
      RunData data = entry.getValue();

      totalPassed += data.getPassed();
    }
    return totalPassed;    
  }

  private int getTotalFailed() {
    int totalFailed = 0;

    for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
      RunData data = entry.getValue();

      totalFailed += data.getFailed();
    }
    return totalFailed;    
  }

  private int getTotalErrors() {
    int totalErrors = 0;

    for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
      RunData data = entry.getValue();

      totalErrors += data.getErrors();
    }
    return totalErrors;
  }

  public void close() {
    if (browsers.decrementAndGet() == 0) {
      out.println(String.format("Total %d tests (Passed: %d; Fails: %d; Errors: %d) (%.2f ms)",
          getTotalRan(), getTotalPassed(), getTotalFailed(), getTotalErrors(), findMaxTime()));
      StringBuilder output = new StringBuilder();

      for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
        RunData data = entry.getValue();

        out.println(String.format("  %s: Run %d tests (Passed: %d; Fails: %d; Errors %d) (%.2f ms)",
            entry.getKey(), (data.getPassed() + data.getFailed() + data.getErrors()),
            data.getPassed(), data.getFailed(), data.getErrors(), data.getTotalTime()));
        List<TestResult> problems = data.getProblems();

        for (TestResult testResult : problems) {
          output.append(testResult.getLog() + NEW_LINE);
        }
      }
      if (output.length() > 0) {
        serializer.addOutput(output.toString());
      }
    }
    serializer.endTestSuite();
  }

  // TODO(jeremiele): I know what you think, I think it too...
  private void logData(TestResult testResult) {
    TestResult.Result result = testResult.getResult();
    String browserName = testResult.getBrowserInfo().getName();
    String browserVersion = testResult.getBrowserInfo().getVersion();
    String os = testResult.getBrowserInfo().getOs();

    // There is one thread per browser it should be added the first time
    browsersRunData.putIfAbsent(browserName + " " + browserVersion + " " + os, runData);
    String log = testResult.getLog();

    if (log.length() > 0) {
      runData.addProblem(testResult);
    }
    if (result == TestResult.Result.passed) {
      runData.addPass();
      totalPasses.incrementAndGet();
    } else if (result == TestResult.Result.failed) {
      runData.addFail();
      totalFails.incrementAndGet();
    } else if (result == TestResult.Result.error) {
      runData.addError();
      totalErrors.incrementAndGet();
    }
  }

  public void print(TestResult testResult) {
    logData(testResult);
    BrowserInfo browserInfo = testResult.getBrowserInfo();

    serializer.startTestCase(testResult.getTestCaseName(), testResult.getTestName() + ":" +
        browserInfo.getName() + browserInfo.getVersion(), testResult.getTime() / 1000);
    if (testResult.getResult() != TestResult.Result.passed) {
      String message = "";

      try {
        JsException exception = gson.fromJson(testResult.getMessage(), JsException.class);

        message = exception.getMessage();
      } catch (Exception e) {
        message = testResult.getMessage();
      }
      if (testResult.getResult() == TestResult.Result.failed) {
        serializer.addFailure(testResult.getResult().toString(), message);
      } else if (testResult.getResult() == TestResult.Result.error) {
        serializer.addError(testResult.getResult().toString(), message);
      }
    }
    serializer.endTestCase();
  }
}
