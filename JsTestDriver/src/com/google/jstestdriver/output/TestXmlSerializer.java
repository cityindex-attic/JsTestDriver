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

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.io.OutputStream;

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

  private final TransformerHandler transformerHandler;
  private final OutputStream outputStream;

  public TestXmlSerializer(OutputStream outputStream) {
    this.outputStream = outputStream;
    try {
      transformerHandler =
          ((SAXTransformerFactory) SAXTransformerFactory.newInstance()).newTransformerHandler();
      Transformer transformer = transformerHandler.getTransformer();

      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformerHandler.setResult(new StreamResult(outputStream));
    } catch (TransformerConfigurationException e) {
      throw new RuntimeException(e);
    } catch (TransformerFactoryConfigurationError e) {
      throw new RuntimeException(e);
    }
  }

  public void startTestSuite(String name) {
    try {
      transformerHandler.startDocument();
      AttributesImpl atts = new AttributesImpl();

      if (name.trim().length() > 0) {
        atts.addAttribute("", "", "name", "CDATA", name);
      }
      transformerHandler.startElement("", "", "testsuite", atts);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  public void startTestSuite() {
    startTestSuite("");
  }

  public void endTestSuite() {
    try {
      transformerHandler.endElement("", "", "testsuite");
      transformerHandler.endDocument();
      outputStream.flush();
      outputStream.close();
    } catch (SAXException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void startTestCase(String testCaseName, String testName, float time) {
    AttributesImpl atts = new AttributesImpl();

    atts.addAttribute("", "", "classname", "CDATA", testCaseName);
    atts.addAttribute("", "", "name", "CDATA", testName);
    atts.addAttribute("", "", "time", "CDATA", Float.toString(time));
    try {
      transformerHandler.startElement("", "", "testcase", atts);
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  public void endTestCase() {
    try {
      transformerHandler.endElement("", "", "testcase");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  public void addFailure(String type, String msg) {
    try {
      AttributesImpl atts = new AttributesImpl();

      atts.addAttribute("", "", "type", "CDATA", type);
      transformerHandler.startElement("", "", "failure", atts);
      char[] charMsg = msg.toCharArray();

      transformerHandler.characters(charMsg, 0, charMsg.length);
      transformerHandler.endElement("", "", "failure");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  public void addError(String msg, String inner) {
    try {
      AttributesImpl atts = new AttributesImpl();

      atts.addAttribute("", "", "message", "CDATA", msg);
      transformerHandler.startElement("", "", "error", atts);
      char[] charMsg = inner.toCharArray();

      transformerHandler.characters(charMsg, 0, charMsg.length);
      transformerHandler.endElement("", "", "error");
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  public void addOutput(String output) {
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
}
