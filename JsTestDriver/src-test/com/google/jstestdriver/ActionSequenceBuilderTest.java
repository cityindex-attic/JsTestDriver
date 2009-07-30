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

import com.google.inject.util.Providers;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class ActionSequenceBuilderTest extends TestCase {

  private LinkedHashSet<FileInfo> files = new LinkedHashSet<FileInfo>();

  public void testAddTestsWithRemoteServerAddress() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);
    String xmlOutputDir = "/out";
    boolean verbose = true;
    boolean captureConsole = true;
    List<String> browsers = browsers();
    List<String> tests = tests();

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
    expectedActions.add(BrowserShutdownAction.class);
    builder.withRemoteServer("http://foo").usingFiles(files, false).onBrowsers(browsers);

    List<Action> sequence = builder.addTests(tests, xmlOutputDir, verbose, captureConsole).build();

    assertSequence(expectedActions, sequence);

    ThreadedActionsRunner runner = (ThreadedActionsRunner) sequence.get(1);
    assertEquals(1, runner.getActions().size());
    RunTestsAction testAction = (RunTestsAction) runner.getActions().get(0);
    assertEquals(tests, testAction.getTests());
    assertEquals(captureConsole, testAction.isCaptureConsole());
  }

  public void testAddTestsWithLocalServer() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);
    String xmlOutputDir = "/out";
    boolean verbose = true;
    boolean captureConsole = true;
    List<String> browsers = browsers();
    List<String> tests = tests();

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);
    builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(browsers);

    List<Action> sequence = builder.addTests(tests, xmlOutputDir, verbose, captureConsole).build();

    assertSequence(expectedActions, sequence);

    ThreadedActionsRunner runner = (ThreadedActionsRunner) sequence.get(2);
    assertEquals(1, runner.getActions().size());
    RunTestsAction testAction = (RunTestsAction) runner.getActions().get(0);
    assertEquals(tests, testAction.getTests());
    assertEquals(captureConsole, testAction.isCaptureConsole());
  }

  public void testAddTestsAndDryrun() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);
    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addTests(tests(), "/out", false, false).asDryRun(true).build();

    ThreadedActionsRunner runner = findAction(sequence, ThreadedActionsRunner.class);
    assertEquals(2, runner.getActions().size());
    List<Class<? extends ThreadedAction>> expectedThreadedActions =
        new ArrayList<Class<? extends ThreadedAction>>();
    expectedThreadedActions.add(DryRunAction.class);
    expectedThreadedActions.add(RunTestsAction.class);
    this.<ThreadedAction> assertSequence(expectedThreadedActions, runner.getActions());
  }

  public void testAddTestsAndReset() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);

    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addTests(tests(), "/out", false, false).reset(true).build();

    ThreadedActionsRunner runner = findAction(sequence, ThreadedActionsRunner.class);
    assertEquals(2, runner.getActions().size());
    List<Class<? extends ThreadedAction>> expectedThreadedActions =
        new ArrayList<Class<? extends ThreadedAction>>();
    expectedThreadedActions.add(ResetAction.class);
    expectedThreadedActions.add(RunTestsAction.class);
    this.<ThreadedAction> assertSequence(expectedThreadedActions, runner.getActions());
  }

  public void testAddTestsWithDryRunAndReset() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);

    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addTests(tests(), "/out", false, false).asDryRun(true).reset(true).build();

    ThreadedActionsRunner runner = findAction(sequence, ThreadedActionsRunner.class);
    assertEquals(3, runner.getActions().size());
    List<Class<? extends ThreadedAction>> expectedThreadedActions =
        new ArrayList<Class<? extends ThreadedAction>>();
    expectedThreadedActions.add(ResetAction.class);
    expectedThreadedActions.add(DryRunAction.class);
    expectedThreadedActions.add(RunTestsAction.class);
    this.<ThreadedAction> assertSequence(expectedThreadedActions, runner.getActions());
  }

  public void testAddCommands() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);
    List<String> commands = Arrays.asList("'foo'+'bar'", "1+1");
    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addCommands(commands).build();

    ThreadedActionsRunner runner = findAction(sequence, ThreadedActionsRunner.class);
    assertNotNull(runner);
    List<ThreadedAction> actions = runner.getActions();
    assertEquals(2, actions.size());
    List<Class<? extends ThreadedAction>> expectedThreadedActions =
        new ArrayList<Class<? extends ThreadedAction>>();
    expectedThreadedActions.add(EvalAction.class);
    expectedThreadedActions.add(EvalAction.class);
    this.<ThreadedAction> assertSequence(expectedThreadedActions, actions);
    assertEquals(commands.get(0), ((EvalAction) actions.get(0)).getCmd());
    assertEquals(commands.get(1), ((EvalAction) actions.get(1)).getCmd());
  }

  public void testNoBrowsers() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);

    List<Action> actions = builder.addTests(tests(), "/out", false, false).withLocalServerPort(999)
        .usingFiles(files, false).build();
    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
//    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
//    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);
    this.<Action>assertSequence(expectedActions, actions);
  }

  public void testNoServer() throws Exception {
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new DefaultResponseStreamFactory(), new ActionFactory(null,
            Providers.<Server> of(new HttpServer())), null);

    try {
      builder.addTests(tests(), "/out", false, false).onBrowsers(browsers()).usingFiles(files,
          false).build();
      fail("expected and exception because we have no server.");
    } catch (IllegalArgumentException e) {
      // pass
    }
  }

  private List<String> tests() {
    List<String> tests = new ArrayList<String>();
    tests.add("test.testFoo");
    return tests;
  }

  private List<String> browsers() {
    List<String> browsers = new ArrayList<String>();
    browsers.add("foo");
    return browsers;
  }

  private <T> void assertSequence(List<Class<? extends T>> expectedActions, List<T> actions) {
    List<Class<?>> actual = new ArrayList<Class<?>>();
    for (T action : actions) {
      actual.add(action.getClass());
    }
    assertEquals(expectedActions, actual);
  }

  private <T> T findAction(List<?> actions, Class<T> actionClass) {
    for (Object action : actions) {
      if (actionClass.isInstance(action)) {
        return actionClass.cast(action);
      }
    }
    return null;
  }
}
