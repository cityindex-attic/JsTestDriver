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

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.kohsuke.args4j.CmdLineException;

import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.browser.CommandLineBrowserRunner;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FlagsTest extends TestCase {

  public void testFlagsAreSet() throws Exception {
    Flags flags;
    FlagsParser parser = new Args4jFlagsParser(null);

    try {
      flags = parser.parseArgument(new String[] { "--port", "1234", "--server", "http://localhost:1234" });
      assertEquals(1234, (int) flags.getPort());
      assertEquals((int) flags.getPort(), 1234);
      assertEquals(flags.getServer(), "http://localhost:1234");

      flags = parser.parseArgument(new String[] { "--testOutput", "/path/file"});
      assertEquals("/path/file", flags.getTestOutput());
      flags = parser.parseArgument(new String[] { "--browser", "/path/browser,beep"});
      Set<BrowserRunner> browsers = flags.getBrowser();

      assertEquals(2, browsers.size());
      assertTrue(
          browsers.contains(
              new CommandLineBrowserRunner("/path/browser", null)));
      assertTrue(
          browsers.contains(
              new CommandLineBrowserRunner("beep", null)));

      flags = parser.parseArgument(new String[] { "--reset" });
      assertTrue(flags.getReset());
      flags = parser.parseArgument(new String[] { "--tests", "testCase.testName"});
      List<String> tests = flags.getTests();

      assertNotNull(tests);
      assertEquals(1, tests.size());
      assertEquals("testCase.testName", tests.get(0));
    } catch (CmdLineException e) {
      fail("Unexpected exception thrown: " + e);
    }
  }

  public void testDefaultFlagsAreSet() throws Exception {
    Flags flags;
    FlagsParser parser = new Args4jFlagsParser(null);

    try {
      flags = parser.parseArgument(new String[]{"--browser", "foo"});
      assertEquals(-1, (int) flags.getPort());
      assertNull(flags.getServer());
      assertNotNull(flags.getTestOutput());

      flags = parser.parseArgument(new String[]{"--port", "12345"});
      assertNotNull(flags.getBrowser());
      assertFalse(flags.getReset());
      assertNotNull(flags.getTests());
    } catch (CmdLineException e) {
      fail("Unexpected exception thrown: " + e);
    }
  }
}
