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
package com.google.jstestdriver.output;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import com.google.gson.Gson;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.JsException;
import com.google.jstestdriver.TestResult;

public class DefaultListenerTest extends TestCase {

  private static final String NEW_LINE = System.getProperty("line.separator");
  private final Gson gson = new Gson();
  private OutputStream buf = new ByteArrayOutputStream();
  private PrintStream out = new PrintStream(buf, true);

  public void testEachPassedTestPrintsDotEorFFailuresOnClose() throws Exception {
    DefaultListener printer = new DefaultListener(out, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.onTestComplete(testResult(browser, "passed", "", "", "A", "d", 1));
    printer.onTestComplete(testResult(browser, "failed", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "", "B", "e", 2));
    printer.onTestComplete(testResult(browser, "error", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "", "C", "f", 3));
    printer.finish();
    assertEquals(".FE" + NEW_LINE +
        "Total 3 tests (Passed: 1; Fails: 1; Errors: 1) (6.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 3 tests (Passed: 1; Fails: 1; Errors 1) (6.00 ms)" + NEW_LINE +
        "    B.e failed (2.00 ms): abc" + NEW_LINE + "      stack" + NEW_LINE + NEW_LINE +
        "    C.f error (3.00 ms): abc" + NEW_LINE + "      stack" + NEW_LINE + NEW_LINE,
        buf.toString());
  }

  public void testEachTestPrintsDotAndWrapsLongLine() throws Exception {
    DefaultListener printer = new DefaultListener(out, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.setLineLength(1);
    TestResult result = new TestResult(browser, "passed", "", "", "", "", 0);

    printer.onTestComplete(result);
    printer.onTestComplete(result);
    printer.onTestComplete(result);
    assertEquals("." + NEW_LINE + "." + NEW_LINE + "." + NEW_LINE, buf.toString());
  }

  public void testNotVerbosePassAndLog() throws Exception {
    DefaultListener printer = new DefaultListener(out, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.onTestComplete(testResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.finish();
    assertEquals("." + NEW_LINE +
        "Total 1 tests (Passed: 1; Fails: 0; Errors: 0) (1.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 1 tests (Passed: 1; Fails: 0; Errors 0) (1.00 ms)" + NEW_LINE +
        "    A.d passed (1.00 ms)" + NEW_LINE +
        "      [LOG] some log" + NEW_LINE, buf.toString());
  }

  public void testVerbosePassAndLog() throws Exception {
    DefaultListener printer = new DefaultListener(out, true);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.onTestComplete(testResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.finish();
    assertEquals("TB 1 os [PASSED] A.d" + NEW_LINE +
        "  [LOG] some log" + NEW_LINE +
        "Total 1 tests (Passed: 1; Fails: 0; Errors: 0) (1.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 1 tests (Passed: 1; Fails: 0; Errors 0) (1.00 ms)" + NEW_LINE,
        buf.toString());
  }

  public void testNotVerbosePassFailErrorAndLog() throws Exception {

    DefaultListener printer = new DefaultListener(out, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.onTestComplete(testResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.onTestComplete(testResult(browser, "failed", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] failed log", "B", "e", 2));
    printer.onTestComplete(testResult(browser, "error", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] error log", "C", "f", 3));
    printer.finish();
    assertEquals(".FE" + NEW_LINE +
        "Total 3 tests (Passed: 1; Fails: 1; Errors: 1) (6.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 3 tests (Passed: 1; Fails: 1; Errors 1) (6.00 ms)" + NEW_LINE +
        "    A.d passed (1.00 ms)" + NEW_LINE +
        "      [LOG] some log" + NEW_LINE +
        "    B.e failed (2.00 ms): abc" + NEW_LINE +
        "      stack" + NEW_LINE + NEW_LINE +
        "      [LOG] failed log" + NEW_LINE +
        "    C.f error (3.00 ms): abc" + NEW_LINE +
        "      stack" + NEW_LINE + NEW_LINE +
        "      [LOG] error log" + NEW_LINE, buf.toString());
  }
  
  private TestResult testResult(BrowserInfo browser, String result, String message, String log,
      String testCaseName, String testName, float time) {
    final TestResult testResult =
      new TestResult(browser, result, message, log, testCaseName, testName, time);
    if (!"passed".equals(result)) {
      final JsException exception = gson.fromJson(message, JsException.class);
      
      testResult.setParsedMessage(exception.getMessage());
      testResult.setStack(exception.getStack());
    }
    return testResult;
  }

  public void testVerbosePassFailErrorAndLog() throws Exception {
    DefaultListener printer = new DefaultListener(out, true);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.onTestComplete(testResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.onTestComplete(testResult(browser, "failed", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] failed log", "B", "e", 2));
    printer.onTestComplete(testResult(browser, "error", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] error log", "C", "f", 3));
    printer.finish();
    assertEquals("TB 1 os [PASSED] A.d" + NEW_LINE +
        "  [LOG] some log" + NEW_LINE +
        "TB 1 os [FAILED] B.e" + NEW_LINE +
        "  [LOG] failed log" + NEW_LINE +
        "TB 1 os [ERROR] C.f" + NEW_LINE +
        "  [LOG] error log" + NEW_LINE +
        "Total 3 tests (Passed: 1; Fails: 1; Errors: 1) (6.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 3 tests (Passed: 1; Fails: 1; Errors 1) (6.00 ms)" + NEW_LINE +
        "    B.e failed (2.00 ms): abc" + NEW_LINE +
        "      stack" + NEW_LINE + NEW_LINE +
        "    C.f error (3.00 ms): abc" + NEW_LINE +
        "      stack" + NEW_LINE + NEW_LINE , buf.toString());
  }
}
