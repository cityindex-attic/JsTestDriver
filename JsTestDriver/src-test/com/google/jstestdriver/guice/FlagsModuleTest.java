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

package com.google.jstestdriver.guice;

import java.util.Arrays;

import junit.framework.TestCase;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import com.google.jstestdriver.FlagsImpl;

/**
 * @author corysmith
 */
public class FlagsModuleTest extends TestCase {

  public void testBindFlag() throws Exception {
    final FlagsImpl flags = new FlagsImpl();
    flags.setCaptureConsole(true);
    Injector injector = Guice.createInjector(new FlagsModule(flags));
    
    assertEquals(Boolean.valueOf(flags.getCaptureConsole()),
        injector.getInstance(Key.get(Boolean.class, Names.named("captureConsole"))));
  }

  public void testBindNullFlag() throws Exception {
    final FlagsImpl flags = new FlagsImpl();
    flags.setTestOutput(null);
    Injector injector = Guice.createInjector(new FlagsModule(flags));
    assertEquals(flags.getServer(),
        injector.getInstance(Key.get(String.class, Names.named("testOutput"))));
  }

  public void testBindParameterized() throws Exception {
    final FlagsImpl flags = new FlagsImpl();
    flags.setTests(Arrays.asList("foo","bar","baz"));
    Injector injector = Guice.createInjector(new FlagsModule(flags));
    assertEquals(flags.getTests(),
        injector.getInstance(Key.get(Types.listOf(String.class), Names.named("tests"))));
  }
}
