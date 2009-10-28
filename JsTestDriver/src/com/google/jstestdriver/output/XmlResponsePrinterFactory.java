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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.RunData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Provides a printer that prints the responses to an xml file that JUnit understands.
 * 
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 * @author corysmith@google.com (Cory Smith)
 */
public class XmlResponsePrinterFactory implements ResponsePrinterFactory {

  private final String xmlDir;

  private final JsTestDriverClient client;

  private final AtomicInteger numberOfBrowsers = new AtomicInteger(-1);
  private final ConcurrentHashMap<String, RunData> browsersRunData =
    new ConcurrentHashMap<String, RunData>();

  @Inject
  public XmlResponsePrinterFactory(@Named("testOutput") String xmlDir,
                                   JsTestDriverClient client) {
    this.xmlDir = xmlDir;
    this.client = client;
  }

  public TestResultPrinter getResponsePrinter(String xmlFile) {
    if (numberOfBrowsers.get() == -1) {
      numberOfBrowsers.set(client.listBrowsers().size());
    }
    try {
      return new XmlPrinter(new TestXmlSerializer(new FileOutputStream(
          String.format("%s/%s", xmlDir, xmlFile))), numberOfBrowsers, browsersRunData);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
