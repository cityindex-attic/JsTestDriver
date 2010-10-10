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

import com.google.inject.Guice;
import com.google.jstestdriver.guice.DebugModule;
import com.google.jstestdriver.guice.TestResultPrintingModule;

import junit.framework.TestCase;

import java.io.File;
import java.util.Collections;

/**
 * @author corysmith
 */
public class JsTestDriverModuleTest extends TestCase {
  public void testGetActionRunnerWithoutXmlPrinter() throws Exception {
    FlagsImpl flags = new FlagsImpl();
    Guice.createInjector(new TestResultPrintingModule(""),
        new DebugModule(false),
        new JsTestDriverModule(flags,
        Collections.<FileInfo>emptySet(),
        "http://foo",
        System.out,
        new File(""),
        2 * 60 * 60,
        Collections.<FileInfo>emptyList())).getInstance(ActionRunner.class);
  }

  public void testGetActionRunnerWithXmlWriter() throws Exception {
    FlagsImpl flags = new FlagsImpl();
    Guice.createInjector(new TestResultPrintingModule("."),
        new DebugModule(false),
        new JsTestDriverModule(flags,
        Collections.<FileInfo>emptySet(),
        "http://foo",
        System.out,
        new File(""),
        2 * 60 * 60,
        Collections.<FileInfo>emptyList())).getInstance(ActionRunner.class);
  }
}
