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

import static com.google.jstestdriver.TestResult.Result.error;
import static com.google.jstestdriver.TestResult.Result.failed;
import static com.google.jstestdriver.TestResult.Result.passed;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.TestResult;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestXmlSerializerTest extends TestCase {

  StringWriter writer = new StringWriter();
  TestXmlSerializer serializer = new TestXmlSerializer(writer);
  BrowserInfo firefox = makeBrowser("Firefox", "Linux", "2.5", 1);
  String stdout = "Some standard out\n logging";
  TestResult firefoxPassed1 =
      new TestResult(firefox, passed.toString(), "great", stdout, "testCase1", "test1", 1.0f);
  TestResult firefoxPassed2 =
      new TestResult(firefox, passed.toString(), "pretty good", "',.p", "testCase1", "test2", 2.0f);
  TestResult firefoxFailed1 =
      new TestResult(firefox, failed.toString(), "oh noes", "aoeu", "testCase1", "test3", 3.0f);
  String message = "crash, burn\nDied horribly";
  TestResult firefoxError1 =
      new TestResult(firefox, error.toString(), message, ";qjk", "testCase1", "test4", 4.0f);
  XPath xPath = XPathFactory.newInstance().newXPath();

  public void testTestSuiteIsRootNode() throws Exception {
    serializer.writeTestCase("myTestCase", asList(firefoxPassed1));
    Document doc = parse();
    int testSuiteCount = queryInt(doc, "count(//testsuite)");
    assertEquals(1, testSuiteCount);
  }

  // example: <testsuite errors="0" failures="0" hostname="alexeagle.mtv.corp.google.com" name="com.google.jstestdriver.ConfigurationParserTest" tests="9" time="0.232" timestamp="2009-10-29T21:21:16">
  public void testTestSuiteHasAttributesFilledIn() throws Exception {
    firefoxFailed1.setStack("a stack trace");
    serializer.writeTestCase("myTestCase",
        asList(firefoxPassed1, firefoxError1, firefoxFailed1, firefoxPassed2));
    Document doc = parse();
    String name = queryString(doc, "/testsuite/@name");
    int errorCount = queryInt(doc, "/testsuite/@errors");
    int failureCount = queryInt(doc, "/testsuite/@failures");
    int testCount = queryInt(doc, "/testsuite/@tests");
    float time = queryFloat(doc, "/testsuite/@time");
    assertEquals("myTestCase", name);
    assertEquals(1, errorCount);
    assertEquals(1, failureCount);
    assertEquals(4, testCount);
    assertEquals((1 + 2 + 3 + 4) / 1000f, time, 0.0001);
  }

  // example: <testcase classname="com.google.jstestdriver.ConfigurationParserTest" name="testParseConfigFileAndHaveListOfFiles" time="0.12" />
  public void testTestCaseHasAttributesFilledIn() throws Exception {
    serializer.writeTestCase("myTestCase", asList(firefoxPassed1));
    Document doc = parse();
    String name = queryString(doc, "/testsuite/testcase/@name");
    String classname = queryString(doc, "/testsuite/testcase/@classname");
    float time = queryFloat(doc, "/testsuite/testcase/@time");
    assertEquals("test1", name);
    assertEquals("myTestCase", classname);
    assertEquals(1 / 1000f, time, 0.0001);
  }
  // example: <error type="java.lang.NullPointerException">java.lang.NullPointerException
  //        at com.google.jstestdriver.output.PrintXmlTestResultsAction.run(PrintXmlTestResultsAction.java:18)
  //        at com.google.jstestdriver.ActionRunner.runActions(ActionRunner.java:45)
  //        at com.google.jstestdriver.IDEPluginActionBuilderTest.testExample(IDEPluginActionBuilderTest.java:75)
  //</error>
  public void testTestErrorAppearsAsChildOfTestCase() throws Exception {
    serializer.writeTestCase("myTestCase", asList(firefoxError1));
    Document doc = parse();
    int errorTagCount = queryInt(doc, "count(/testsuite/testcase/error)");
    assertEquals(1, errorTagCount);
    String type = queryString(doc, "/testsuite/testcase/error/@type");
    String body = queryString(doc, "/testsuite/testcase/error");
    assertEquals("error", type);
    assertEquals(message, body);
  }

  // example: <failure message="suck" type="junit.framework.AssertionFailedError">junit.framework.AssertionFailedError: suck
  //      at com.google.jstestdriver.ActionRunnerTest.testCreateCommandQueueFromFlags(ActionRunnerTest.java:42)
  //</failure>
  public void testTestFailureAppearsAsChildOfTestCase() throws Exception {
    firefoxFailed1.setStack("a\nstack\ntrace");
    serializer.writeTestCase("myTestCase", asList(firefoxFailed1));
    Document doc = parse();
    int errorTagCount = queryInt(doc, "count(/testsuite/testcase/failure)");
    assertEquals(1, errorTagCount);
    String type = queryString(doc, "/testsuite/testcase/failure/@type");
    String message = queryString(doc, "/testsuite/testcase/failure/@message");
    String body = queryString(doc, "/testsuite/testcase/failure");
    assertEquals("failed", type);
    assertEquals("oh noes", message);
    assertEquals("a\nstack\ntrace", body);
  }

  // example: <system-out><![CDATA[EMMA: collecting runtime coverage data ...
  //]]></system-out>
  public void testStandardOutputAppears() throws Exception {
    serializer.writeTestCase("myTestCase", asList(firefoxPassed1));
    Document doc = parse();
    int stdoutTagCount = queryInt(doc, "count(/testsuite/system-out)");
    assertEquals(1, stdoutTagCount);
    String body = queryString(doc, "/testsuite/system-out");
    assertEquals(stdout, body);
  }

  // example: <system-err><![CDATA[]]></system-err>
  public void testStandardErrorAppears() throws Exception {
    serializer.writeTestCase("myTestCase", asList(firefoxPassed1));
    Document doc = parse();
    int stderrTagCount = queryInt(doc, "count(/testsuite/system-err)");
    assertEquals(0, stderrTagCount);
  }

  private BrowserInfo makeBrowser(String name, String os, String version, long id) {
    BrowserInfo info = new BrowserInfo();
    info.setName(name);
    info.setOs(os);
    info.setVersion(version);
    info.setId(id);
    return info;
  }

  private String queryString(Document doc, String expression) throws XPathExpressionException {
    return (String) xPath.compile(expression).evaluate(doc, XPathConstants.STRING);
  }

  private float queryFloat(Document doc, String expression) throws XPathExpressionException {
    return ((Double) xPath.compile(expression).evaluate(doc, XPathConstants.NUMBER)).floatValue();
  }

  private int queryInt(Document doc, String expression) throws XPathExpressionException {
    return ((Double) xPath.compile(expression).evaluate(doc, XPathConstants.NUMBER)).intValue();
  }

  private Document parse() throws ParserConfigurationException, IOException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(false);
    DocumentBuilder parser = factory.newDocumentBuilder();
    try {
      return parser.parse(new InputSource(new StringReader(writer.getBuffer().toString())));
    } catch (SAXException e) {
      fail("Couldn't parse " + writer.getBuffer().toString() + ": " + e.getMessage());
      return null;
    }
  }
}
