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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class FlagsTest extends TestCase {

  public void testFlagsAreSet() throws Exception {
    Flags flags = new FlagsImpl();
    CmdLineParser parser = new CmdLineParser(flags);

    try {
      parser.parseArgument(new String[] { "--port", "1234" });
      assertEquals((int) flags.getPort(), 1234);
      parser.parseArgument(new String[] { "--server", "http://localhost:1234" });
      assertEquals((int) flags.getPort(), 1234);
      assertEquals(flags.getServer(), "http://localhost:1234");

      parser.parseArgument(new String[] { "--testOutput", "/path/file"});
      assertEquals("/path/file", flags.getTestOutput());
      parser.parseArgument(new String[] { "--browser", "/path/browser,beep"});
      List<String> browserPath = flags.getBrowser();

      assertEquals(2, browserPath.size());
      assertEquals("/path/browser", browserPath.get(0));
      assertEquals("beep", browserPath.get(1));
      parser.parseArgument(new String[] { "--reset" });
      assertTrue(flags.getReset());
      parser.parseArgument(new String[] { "--tests", "testCase.testName"});
      List<String> tests = flags.getTests();

      assertNotNull(tests);
      assertEquals(1, tests.size());
      assertEquals("testCase.testName", tests.get(0));
    } catch (CmdLineException e) {
      fail("Unexpected exception thrown: " + e);
    }
  }

  public void testDefaultFlagsAreSet() throws Exception {
    Flags flags = new FlagsImpl();
    CmdLineParser parser = new CmdLineParser(flags);

    try {
      parser.parseArgument(new String[0]);
      assertEquals(-1, (int) flags.getPort());
      assertNull(flags.getServer());
      assertNotNull(flags.getTestOutput());
      assertNotNull(flags.getBrowser());
      assertFalse(flags.getReset());
      assertNotNull(flags.getTests());
    } catch (CmdLineException e) {
      fail("Unexpected exception thrown: " + e);
    }
  }
}
