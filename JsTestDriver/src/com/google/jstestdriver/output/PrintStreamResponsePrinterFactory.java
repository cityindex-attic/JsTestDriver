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

import java.io.PrintStream;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.jstestdriver.DefaultPrinter;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.ResponsePrinterFactory;
import com.google.jstestdriver.TestResultPrinter;

/**
 * Provides a printer that prints the responses to the provided PrintStream.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 * @author corysmith@google.com (Cory Smith)
 */
public class PrintStreamResponsePrinterFactory implements ResponsePrinterFactory {
  private DefaultPrinter defaultPrinter;
  private final boolean verbose;
  private final JsTestDriverClient client;
  private final PrintStream out;

  @Inject
  public PrintStreamResponsePrinterFactory(@Named("outputStream") PrintStream out,
                                    JsTestDriverClient client,
                                    @Named("verbose") boolean verbose) {
    this.out = out;
    this.client = client;
    this.verbose = verbose;
  }
  
  public TestResultPrinter getResponsePrinter(String xmlFile) {
    synchronized (XmlResponsePrinterFactory.class) {
      if (defaultPrinter == null) {
        defaultPrinter = new DefaultPrinter(out, client.listBrowsers().size(), verbose);
      }
    }
    return defaultPrinter;
  }
}
