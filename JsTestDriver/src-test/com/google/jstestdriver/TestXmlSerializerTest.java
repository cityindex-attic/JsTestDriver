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

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class TestXmlSerializerTest extends TestCase {

  public void testAddTestCaseToXMLFile() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    TestXmlSerializer serializer = new TestXmlSerializer(outputStream);

    serializer.startTestSuite("testSuiteName");
    serializer.startTestCase("testCaseName", "testName", 42L);
    serializer.endTestCase();
    serializer.endTestSuite();

    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(inputStream);

    doc.getDocumentElement().normalize();
    NodeList testSuiteNodeList = doc.getElementsByTagName("testsuite");

    assertEquals(1, testSuiteNodeList.getLength());
    Node testSuite = testSuiteNodeList.item(0);

    assertEquals("testSuiteName", testSuite.getAttributes().getNamedItem("name").getNodeValue());
    NodeList nodeList = doc.getElementsByTagName("testcase");

    assertEquals(1, nodeList.getLength());
    Node node = nodeList.item(0);

    assertEquals("testCaseName", node.getAttributes().getNamedItem("classname").getNodeValue());
    assertEquals("testName", node.getAttributes().getNamedItem("name").getNodeValue());
    assertEquals("42.0", node.getAttributes().getNamedItem("time").getNodeValue());
  }

  public void testTestFailure() throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    TestXmlSerializer serializer = new TestXmlSerializer(outputStream);

    serializer.startTestSuite();
    serializer.startTestCase("testCaseName", "testName", 42L);
    serializer.addFailure("type", "error");
    serializer.endTestCase();
    serializer.endTestSuite();

    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(inputStream);

    doc.getDocumentElement().normalize();
    NodeList nodeList = doc.getElementsByTagName("failure");

    assertEquals(1, nodeList.getLength());
    Node node = nodeList.item(0);

    assertEquals("type", node.getAttributes().getNamedItem("type").getNodeValue());
    assertEquals("error", node.getTextContent());
  }
}
