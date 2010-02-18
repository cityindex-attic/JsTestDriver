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
package com.google.eclipse.javascript.jstestdriver.ui.launch;

import java.io.ByteArrayInputStream;
import java.util.List;

import com.google.eclipse.javascript.jstestdriver.ui.launch.TestCaseNameFinder;

import junit.framework.TestCase;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class TestCaseNameFinderTest extends TestCase {
  private TestCaseNameFinder finder;
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    finder = new TestCaseNameFinder();
  }

  public void testGetTestCasesFindsSingleTestCase() throws Exception {
    String input = "//Some Stupid comment\n"
        + "var assertsTest = jstestdriver.testCaseManager.TestCase('assertsTest');\n"
        + "\n" + "assertsTest.prototype.testA = function() {};";
    List<String> testCases = finder.getTestCases(new ByteArrayInputStream(input
        .getBytes()));
    assertEquals(1, testCases.size());
    assertEquals("assertsTest.prototype.testA", testCases.get(0));
  }

  public void testGetTestCasesFindsMultipleTestCases() throws Exception {
    String input = "//Some Stupid comment\n"
        + "var assertsTest = jstestdriver.testCaseManager.TestCase('assertsTest');\n"
        + "\n"
        + "assertsTest.prototype.testA = function() {};\n\n"
        + "var libLoaderTest = jstestdriver.testCaseManager.TestCase('libLoaderTest');\n"
        + "\n" + "libLoaderTest.prototype.testAB = function() {};\n\n";
    List<String> testCases = finder.getTestCases(new ByteArrayInputStream(input
        .getBytes()));
    assertEquals(2, testCases.size());
    assertEquals("assertsTest.prototype.testA", testCases.get(0));
    assertEquals("libLoaderTest.prototype.testAB", testCases.get(1));
  }

  public void testGetTestCasesNoTestsReturnsEmptyList() throws Exception {
    assertEquals(0, finder.getTestCases(
        new ByteArrayInputStream("No Tests here".getBytes())).size());
  }

  public void testGetTestCasesFromSource() throws Exception {
    String input = "//Some Stupid comment\n"
        + "var assertsTest = jstestdriver.testCaseManager.TestCase('assertsTest');\n"
        + "\n"
        + "assertsTest.prototype.testA = function() {};\n\n"
        + "var libLoaderTest = jstestdriver.testCaseManager.TestCase('libLoaderTest');\n"
        + "\n" + "libLoaderTest.prototype.testAB = function() {};\n\n";
    List<String> testCases = finder.getTestCases(input);
    assertEquals(2, testCases.size());
    assertEquals("assertsTest.prototype.testA", testCases.get(0));
    assertEquals("libLoaderTest.prototype.testAB", testCases.get(1));
  }
}
