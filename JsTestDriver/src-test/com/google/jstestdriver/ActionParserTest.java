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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.inject.util.Providers;

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionParserTest extends TestCase {

  public void testParseFlagsAndCreateActionQueue() throws Exception {
    List<String> browsers = Arrays.asList("browser");
    ActionListProvider parser = createProvider(browsers, 9876, null, false, Collections.<String>emptyList());
    List<Action> actions = parser.get();

    ArrayList<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);
    assertEquals(browsers, findAction(actions, BrowserStartupAction.class).getBrowserPath());
  }

  private ActionListProvider createProvider(List<String> browsers,
      int port, String serverAddress, boolean reset, List<String> tests) {
    return new ActionListProvider(new ActionFactory(null, Providers.<Server>of(null)),
        null,
        tests,
        null,
        false,
        false,
        Collections.<String>emptyList(),
        browsers,
        reset,
        false,
        false,
        port,
        Collections.<FileInfo>emptySet(),
        serverAddress,
        null);
  }

  public void testParseWithServerAndReset() throws Exception {
    String serverAddress = "http://otherserver:8989";
    ActionListProvider parser = createProvider(Arrays.asList("browser1"), -1, serverAddress, true, Collections.<String>emptyList());

    FlagsImpl flags = new FlagsImpl();
    flags.setServer(serverAddress);
    flags.setBrowser("browser1");
    flags.setReset(true);

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
    expectedActions.add(BrowserShutdownAction.class);

    List<Action> actions = parser.get();
    assertSequence(expectedActions, actions);

    ThreadedActionsRunner action = findAction(actions, ThreadedActionsRunner.class);
    assertEquals(1, action.getActions().size());
    ThreadedAction threadedAction = action.getActions().get(0);
    assertTrue("Expected ResetAction, found " + threadedAction,
        threadedAction instanceof ResetAction);
  }

  public void testParseFlagsWithServer() throws Exception {
    List<String> browserPaths = new ArrayList<String>();
    browserPaths.add("browser");
    browserPaths.add("browser2");
    String serverAddress = "http://otherserver:8989";
    ActionListProvider parser = createProvider(browserPaths, -1, serverAddress, false, Collections.<String>emptyList());

    List<Action> actions = parser.get();

    BrowserStartupAction action = findAction(actions, BrowserStartupAction.class);
    assertNotNull("Server action not created", action);
    assertEquals(serverAddress, action.getServerAddress());
  }

  public void testParseFlagsNoPortNoServer() throws Exception {
    ActionListProvider parser = createProvider(Arrays.asList("browser1"), -1, null, false, Arrays.asList("foo.testBar"));
    FlagsImpl flags = new FlagsImpl();

    flags.setBrowser("browser");
    flags.setTests("foo.testBar");
    try {
      parser.get();
      fail("expected no server and no port exception");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }

  public void testParseFlagsAndCreateTestActions() throws Exception {
    List<String> tests = Arrays.asList("foo.testBar");
    ActionListProvider parser = createProvider(Arrays.asList("browser"), 9876, null, false, tests);

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);

    List<Action> actions = parser.get();
    assertSequence(expectedActions, actions);

    ThreadedActionsRunner testRunner = findAction(actions, ThreadedActionsRunner.class);
    assertNotNull("Test action not found", testRunner);
    assertEquals(1, testRunner.getActions().size());
    assertTrue(testRunner.getActions().get(0) instanceof RunTestsAction);
    assertEquals(tests, ((RunTestsAction)testRunner.getActions().get(0)).getTests());
  }

  private void assertSequence(List<Class<? extends Action>> expectedActions,
      List<Action> actions) {
    List<Class<? extends Action>> actual = new ArrayList<Class<? extends Action>>();
    for (Action action : actions) {
      actual.add(action.getClass());
    }
    assertEquals(expectedActions, actual);
  }

  private <T extends Action> T findAction(List<Action> actions, Class<T> type) {
    for (Action action: actions) {
      if (type.isInstance(action)) {
        return type.cast(action);
      }
    }
    return null;
  }
}
