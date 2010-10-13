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

import com.google.gson.Gson;
import com.google.jstestdriver.JsException;
import com.google.jstestdriver.TestResult;
import com.google.jstestdriver.TestResult.Result;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.Writer;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class TestXmlSerializer {


  private final Gson gson = new Gson();

  private final TransformerHandler transformerHandler;

  // TODO(corysmith): remove the work from the constructor.
  public TestXmlSerializer(Writer fileWriter) {
    try {
      transformerHandler =
          ((SAXTransformerFactory) SAXTransformerFactory.newInstance()).newTransformerHandler();
      Transformer transformer = transformerHandler.getTransformer();

      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformerHandler.setResult(new StreamResult(fileWriter));
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    } catch (TransformerFactoryConfigurationError e) {
      throw new RuntimeException(e);
    }
  }

  private void startTestSuite(String name, TestXmlSerializer.SuiteAggregator totals) {
    try {
      transformerHandler.startDocument();
      AttributesImpl atts = new AttributesImpl();

      if (name.trim().length() > 0) {
        atts.addAttribute("", "", "name", "CDATA", name);
        // errors="0" failures="0" hostname="alexeagle.mtv.corp.google.com" name="com.google.jstestdriver.ConfigurationParserTest" tests="9" time="0.232

        atts.addAttribute("", "", "errors", "CDATA", String.valueOf(totals.error));
        atts.addAttribute("", "", "failures", "CDATA", String.valueOf(totals.failed));
        atts.addAttribute("", "", "tests", "CDATA", String.valueOf(totals.tests));
        atts.addAttribute("", "", "time", "CDATA", String.valueOf(totals.elapsedTime));        
      }
      transformerHandler.startElement("", "", "testsuite", atts);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void endTestSuite() {
    try {
      transformerHandler.endElement("", "", "testsuite");
      transformerHandler.endDocument();
//      outputStream.flush();
//      outputStream.close();
    } catch (SAXException e) {
      throw new RuntimeException(e);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
    }
  }

  private void startTestCase(String testCaseName, String testName, float time) {
    AttributesImpl atts = new AttributesImpl();

    atts.addAttribute("", "", "classname", "CDATA", testCaseName);
    atts.addAttribute("", "", "name", "CDATA", testName);
    atts.addAttribute("", "", "time", "CDATA", Float.toString(time / 1000f));
    try {
      transformerHandler.startElement("", "", "testcase", atts);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void endTestCase() {
    try {
      transformerHandler.endElement("", "", "testcase");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void addFailure(String stack, String message) {
    try {
      AttributesImpl atts = new AttributesImpl();

      atts.addAttribute("", "", "type", "CDATA", "failed");
      atts.addAttribute("", "", "message", "CDATA", message);
      transformerHandler.startElement("", "", "failure", atts);
      char[] charMsg = stack.toCharArray();

      transformerHandler.characters(charMsg, 0, charMsg.length);
      transformerHandler.endElement("", "", "failure");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void addError(String inner) {
    try {
      AttributesImpl atts = new AttributesImpl();

      atts.addAttribute("", "", "type", "CDATA", "error");
      transformerHandler.startElement("", "", "error", atts);
      char[] charMsg = inner.toCharArray();

      transformerHandler.characters(charMsg, 0, charMsg.length);
      transformerHandler.endElement("", "", "error");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void addOutput(String output) {
    try {
      AttributesImpl atts = new AttributesImpl();

      transformerHandler.startElement("", "", "system-out", atts);
      char[] charMsg = output.toCharArray();

      transformerHandler.startCDATA();
      transformerHandler.characters(charMsg, 0, charMsg.length);
      transformerHandler.endCDATA();
      transformerHandler.endElement("", "", "system-out");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }    
  }

  public void writeTestCase(String testCaseName, Collection<TestResult> testResults) {
    StringBuilder output = new StringBuilder();
    SuiteAggregator totals = new SuiteAggregator(testResults).aggregate();
    startTestSuite(testCaseName, totals);
    for (TestResult testResult : testResults) {
      startTestCase(testCaseName, testResult.getTestName(), testResult.getTime());
      if (testResult.getResult() != Result.passed) {
        String message;

        try {
          JsException exception = gson.fromJson(testResult.getMessage(), JsException.class);

          message = exception.getMessage();
        } catch (Exception e) {
          message = testResult.getMessage();
        }
        if (testResult.getResult() == TestResult.Result.failed) {
          addFailure(testResult.getStack(), message);
        } else if (testResult.getResult() == TestResult.Result.error) {
          addError(message);
        }
      }
      output.append(testResult.getLog());
      endTestCase();
    }
    if (output.length() > 0) {
        addOutput(output.toString());
      }
    endTestSuite();
  }

  private class SuiteAggregator {
    int tests = 0;
    int failed = 0;
    int error = 0;
    float elapsedTime = 0;
    private final Collection<TestResult> results;
    private boolean topLevel = false;

    public SuiteAggregator(Collection<TestResult> results) {
      this.results = results;
    }

    public SuiteAggregator aggregate() {
      for (TestResult result : results) {
        tests++;
        failed += (result.getResult() == Result.failed ? 1 : 0);
        error += (result.getResult() == Result.error ? 1 : 0);
        elapsedTime += result.getTime() / 1000;
      }
      return this;
    }
  }
}
