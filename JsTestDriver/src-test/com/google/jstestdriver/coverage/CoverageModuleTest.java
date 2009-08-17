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
package com.google.jstestdriver.coverage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.ActionRunner;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.FlagsImpl;
import com.google.jstestdriver.JsTestDriverModule;
import com.google.jstestdriver.guice.PrintStreamClientModule;

import junit.framework.TestCase;

/**
 * Smoke test. If you see smoke, it failed.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageModuleTest extends TestCase {
  public void testGetActionRunner() throws Exception {
    CoverageModule coverage = new CoverageModule();
    PrintStreamClientModule printStream = new PrintStreamClientModule(System.out);
    FlagsImpl flags = new FlagsImpl();
    flags.setTests(Arrays.asList("test"));
    flags.setBrowser(Arrays.asList("ff"));
    JsTestDriverModule jsTestDriverModule = new JsTestDriverModule(flags,
                           Collections.<FileInfo>emptySet(),
                           Arrays.<Module>asList(coverage,printStream),
                           "");
    Guice.createInjector(jsTestDriverModule).getInstance(ActionRunner.class);
    List<Action> actions =
        Guice.createInjector(jsTestDriverModule).getInstance(new Key<List<Action>>(){});
    assertTrue(actions.get(actions.size() - 1) instanceof CoverageReporterAction);
  }
}
