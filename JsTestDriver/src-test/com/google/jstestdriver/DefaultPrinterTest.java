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

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class DefaultPrinterTest extends TestCase {

  private static final String NEW_LINE = System.getProperty("line.separator");
  private final Gson gson = new Gson();
  private OutputStream buf = new ByteArrayOutputStream();
  private PrintStream out = new PrintStream(buf, true);

  public void testEachPassedTestPrintsDotEorFFailuresOnClose() throws Exception {
    DefaultPrinter printer = new DefaultPrinter(out, 1, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.print(new TestResult(browser, "passed", "", "", "A", "d", 1));
    printer.print(new TestResult(browser, "failed", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "", "B", "e", 2));
    printer.print(new TestResult(browser, "error", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "", "C", "f", 3));
    printer.close();
    assertEquals(".FE" + NEW_LINE +
        "Total 3 tests (Passed: 1; Fails: 1; Errors: 1) (6.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 3 tests (Passed: 1; Fails: 1; Errors 1) (6.00 ms)" + NEW_LINE +
        "    B.e failed (2.00 ms): abc" + NEW_LINE +
        "    C.f error (3.00 ms): abc" + NEW_LINE, buf.toString());
  }

  public void testEachTestPrintsDotAndWrapsLongLine() throws Exception {
    DefaultPrinter printer = new DefaultPrinter(out, 1, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.setLineLength(1);
    TestResult result = new TestResult(browser, "passed", "", "", "", "", 0);

    printer.print(result);
    printer.print(result);
    printer.print(result);
    assertEquals("." + NEW_LINE + "." + NEW_LINE + "." + NEW_LINE, buf.toString());
  }

  public void testNotVerbosePassAndLog() throws Exception {
    DefaultPrinter printer = new DefaultPrinter(out, 1, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.print(new TestResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.close();
    assertEquals("." + NEW_LINE +
        "Total 1 tests (Passed: 1; Fails: 0; Errors: 0) (1.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 1 tests (Passed: 1; Fails: 0; Errors 0) (1.00 ms)" + NEW_LINE +
        "    A.d passed (1.00 ms)" + NEW_LINE +
        "      [LOG] some log" + NEW_LINE, buf.toString());
  }

  public void testVerbosePassAndLog() throws Exception {
    DefaultPrinter printer = new DefaultPrinter(out, 1, true);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.print(new TestResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.close();
    assertEquals("[PASSED] A.d" + NEW_LINE +
        "  [LOG] some log" + NEW_LINE +
        "Total 1 tests (Passed: 1; Fails: 0; Errors: 0) (1.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 1 tests (Passed: 1; Fails: 0; Errors 0) (1.00 ms)" + NEW_LINE, buf.toString());
  }

  public void testNotVerbosePassFailErrorAndLog() throws Exception {
    DefaultPrinter printer = new DefaultPrinter(out, 1, false);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.print(new TestResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.print(new TestResult(browser, "failed", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] failed log", "B", "e", 2));
    printer.print(new TestResult(browser, "error", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] error log", "C", "f", 3));
    printer.close();
    assertEquals(".FE" + NEW_LINE +
        "Total 3 tests (Passed: 1; Fails: 1; Errors: 1) (6.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 3 tests (Passed: 1; Fails: 1; Errors 1) (6.00 ms)" + NEW_LINE +
        "    A.d passed (1.00 ms)" + NEW_LINE +
        "      [LOG] some log" + NEW_LINE +
        "    B.e failed (2.00 ms): abc" + NEW_LINE +
        "      [LOG] failed log" + NEW_LINE +
        "    C.f error (3.00 ms): abc" + NEW_LINE +
        "      [LOG] error log" + NEW_LINE, buf.toString());
  }

  public void testVerbosePassFailErrorAndLog() throws Exception {
    DefaultPrinter printer = new DefaultPrinter(out, 1, true);
    BrowserInfo browser = new BrowserInfo();

    browser.setName("TB");
    browser.setVersion("1");
    browser.setOs("os");
    printer.print(new TestResult(browser, "passed", "", "[LOG] some log", "A", "d", 1));
    printer.print(new TestResult(browser, "failed", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] failed log", "B", "e", 2));
    printer.print(new TestResult(browser, "error", gson.toJson(new JsException("name", "abc",
        "fileName", 1L, "stack")), "[LOG] error log", "C", "f", 3));
    printer.close();
    assertEquals("[PASSED] A.d" + NEW_LINE +
        "  [LOG] some log" + NEW_LINE +
        "[FAILED] B.e" + NEW_LINE +
        "  [LOG] failed log" + NEW_LINE +
        "[ERROR] C.f" + NEW_LINE +
        "  [LOG] error log" + NEW_LINE +
        "Total 3 tests (Passed: 1; Fails: 1; Errors: 1) (6.00 ms)" + NEW_LINE +
        "  TB 1 os: Run 3 tests (Passed: 1; Fails: 1; Errors 1) (6.00 ms)" + NEW_LINE +
        "    B.e failed (2.00 ms): abc" + NEW_LINE +
        "    C.f error (3.00 ms): abc" + NEW_LINE, buf.toString());
  }
}
