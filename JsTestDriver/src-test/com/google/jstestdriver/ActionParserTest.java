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

import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionParserTest extends TestCase {

  public void testParseFlagsAndCreateActionQueue() throws Exception {
    ActionParser parser = new ActionParser(new ActionFactory());
    FlagsImpl flags = new FlagsImpl();

    flags.setPort(9876);
    flags.setBrowser("browser");
    List<Action> actions = parser.parseFlags(flags, new LinkedHashSet<String>(), "");

    assertEquals(2, actions.size());
    Action action = actions.get(0);

    assertNotNull(action);
    assertTrue(action instanceof ServerStartupAction);
    action = actions.get(1);
    assertTrue(action instanceof BrowserStartupAction);
  }
}
