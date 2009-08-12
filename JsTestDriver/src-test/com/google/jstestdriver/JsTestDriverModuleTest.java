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

import java.util.Collections;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.jstestdriver.output.PrintStreamResponsePrinterFactory;

/**
 * @author corysmith
 *
 */
public class JsTestDriverModuleTest extends TestCase {
  public void testGetActionRunner() throws Exception {
    FlagsImpl flags = new FlagsImpl();
    Guice.createInjector(new JsTestDriverModule(flags,
        Collections.<FileInfo>emptySet(),
        Collections.<Class<? extends Module>>emptyList(),
        "http://foo",
        PrintStreamResponsePrinterFactory.class)).getInstance(ActionRunner.class);
  }
}
