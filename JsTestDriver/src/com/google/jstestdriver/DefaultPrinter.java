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

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class DefaultPrinter implements TestResultPrinter {

  private static final String NEW_LINE = System.getProperty("line.separator");

  private final PrintStream out;
  private final AtomicInteger browsers;
  private final boolean verbose;
  private final Map<String, RunData> browsersRunData = new ConcurrentHashMap<String, RunData>();
  private final AtomicInteger lineColumn = new AtomicInteger(0);
  private final AtomicInteger totalPasses = new AtomicInteger(0);
  private final AtomicInteger totalFails = new AtomicInteger(0);
  private final AtomicInteger totalErrors = new AtomicInteger(0);

  private int lineLength = 70;

  public DefaultPrinter(PrintStream out, int browsers, boolean verbose) {
    this.out = out;
    this.browsers = new AtomicInteger(browsers);
    this.verbose = verbose;
  }

  public void setLineLength(int lineLength) {
    this.lineLength = lineLength;
  }

  public void open() {
  }

  public void open(String name) {
  }

  public void close() {
    if (browsers.decrementAndGet() == 0) {
      if (!verbose) {
        out.println();
      }
      out.println(String.format("Total %d tests (Passed: %d; Fails: %d; Errors: %d) (%.2f ms)",
          (totalPasses.get() + totalFails.get() + totalErrors.get()), totalPasses.get(),
          totalFails.get(), totalErrors.get(), findMaxTime()));
      for (Map.Entry<String, RunData> entry : browsersRunData.entrySet()) {
        RunData data = entry.getValue();

        out.println(String.format("  %s: Run %d tests (Passed: %d; Fails: %d; Errors %d) (%.2f ms)",
            entry.getKey(), (data.getPassed() + data.getFailed() + data.getErrors()),
            data.getPassed(), data.getFailed(), data.getErrors(), data.getTotalTime()));
        List<TestResult> problems = data.getProblems();

        for (TestResult testResult : problems) {
          if (!testResult.getResult().equals("passed")) {
            FailureParser parser = new FailureParser();

            parser.parse(testResult.getMessage());
            String message = parser.getMessage();
            List<String> stack = parser.getStack();
            String formattedStack = "";

            if (stack.size() > 0) {
              StringBuilder sb = new StringBuilder();

              for (String line : stack) {
                sb.append(NEW_LINE + "      " + line);
              }
              formattedStack = sb.toString();
            }
            out.println(String.format("    %s.%s %s (%.2f ms): %s%s", testResult.getTestCaseName(),
                testResult.getTestName(), testResult.getResult(), testResult.getTime(), message,
                formattedStack));
          } else {
            out.println(String.format("    %s.%s %s (%.2f ms)", testResult.getTestCaseName(),
                testResult.getTestName(), testResult.getResult(), testResult.getTime()));
          }
          if (!verbose && testResult.getLog().length() > 0) {
            String[] logLines = testResult.getLog().split("\n");

            for (String line : logLines) {
              out.println("      " + line);
            }
          }
        }
      }
    }
  }

  private float findMaxTime() {
    float max = 0f;

    for (RunData data : browsersRunData.values()) {
      max = Math.max(data.getTotalTime(), max);
    }
    return max;
  }

  public void print(TestResult testResult) {
    String browser = testResult.getBrowserInfo().toString();
    RunData runData = browsersRunData.get(browser);

    if (runData == null) {
      runData = new RunData();
      browsersRunData.put(browser, runData);
    }
    String result = testResult.getResult();
    String log = testResult.getLog();

    runData.addTime(testResult.getTime());
    if (result.equals("passed")) {
      if (!verbose) {
        out.print('.');
        if (log.length() > 0) {
          runData.addProblem(testResult);
        }
      } else {
        out.println("[PASSED] " + testResult.getTestCaseName() + "." + testResult.getTestName());
        if (log.length() > 0) {
          String[] logLines = log.split("\n");

          for (String line : logLines) {
            out.println("  " + line);
          }
        }
      }
      runData.addPass();
      totalPasses.incrementAndGet();
    } else if (result.startsWith("failed")) {
      if (!verbose) {
        out.print('F');
      } else {
        out.println("[FAILED] " + testResult.getTestCaseName() + "." + testResult.getTestName());
        if (log.length() > 0) {
          String[] logLines = log.split("\n");

          for (String line : logLines) {
            out.println("  " + line);
          }
        }
      }
      runData.addFail();
      runData.addProblem(testResult);
      totalFails.incrementAndGet();
    } else if (result.startsWith("error")) {
      if (!verbose) {
        out.print('E');
      } else {
        out.println("[ERROR] " + testResult.getTestCaseName() + "." + testResult.getTestName());
        if (log.length() > 0) {
          String[] logLines = log.split("\n");

          for (String line : logLines) {
            out.println("  " + line);
          }
        }
      }
      runData.addError();
      runData.addProblem(testResult);
      totalErrors.incrementAndGet();
    } else {
      out.print("<" + result + ">");
      if (verbose) {
        out.println(" " + testResult.getTestCaseName() + "." + testResult.getTestName());
        if (log.length() > 0) {
          String[] logLines = log.split("\n");

          for (String line : logLines) {
            out.println("  " + line);
          }
        }
      }
      runData.addProblem(testResult);
    }
    if (lineColumn.incrementAndGet() == lineLength) {
      out.println();
      lineColumn.set(0);
    }
  }
}
