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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ResponsePrinterFactory {

  private final String xmlDir;
  private DefaultPrinter defaultPrinter;
  private final PrintStream out;
  private final JsTestDriverClient client;
  private final boolean verbose;
  private final AtomicInteger numberOfBrowsers = new AtomicInteger(-1);
  private final ConcurrentHashMap<String, RunData> browsersRunData =
    new ConcurrentHashMap<String, RunData>();

  public ResponsePrinterFactory(String xmlDir, PrintStream out, JsTestDriverClient client,
      boolean verbose) {
    this.xmlDir = xmlDir;
    this.out = out;
    this.client = client;
    this.verbose = verbose;
  }

  public TestResultPrinter getResponsePrinter(String xmlFile) {
    if (numberOfBrowsers.get() == -1) {
      numberOfBrowsers.set(client.listBrowsers().size());
    }
    if (xmlDir.trim().length() > 0) {
      try {
        return new XmlPrinter(System.out, new TestXmlSerializer(new FileOutputStream(
            String.format("%s/%s", xmlDir, xmlFile))), numberOfBrowsers, browsersRunData);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    } else {
      synchronized (ResponsePrinterFactory.class) {
        if (defaultPrinter == null) {
          defaultPrinter = new DefaultPrinter(out, client.listBrowsers().size(), verbose);
        }
      }
      return defaultPrinter;    
    }
  }
}
