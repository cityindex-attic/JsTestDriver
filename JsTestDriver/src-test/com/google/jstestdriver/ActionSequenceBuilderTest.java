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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.google.inject.util.Providers;
import com.google.jstestdriver.guice.DefaultThreadedActionProvider;

public class ActionSequenceBuilderTest extends TestCase {

  private LinkedHashSet<FileInfo> files = new LinkedHashSet<FileInfo>();
  ActionFactory actionFactory = new ActionFactory(null);

  public void testAddTestsWithRemoteServerAddress() throws Exception {
    List<String> tests = tests();
    boolean captureConsole = true;
    List<String> browsers = browsers();
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(actionFactory, null, null, threadedActionProvider(tests,
            Collections.<String> emptyList(), false, Collections.<String> emptyList(),
            captureConsole), Providers.<JsTestDriverClient> of(null),
            Providers.<URLTranslator>of(null), Providers.<URLRewriter>of(null));

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
    expectedActions.add(BrowserShutdownAction.class);
    builder.withRemoteServer("http://foo").usingFiles(files, false).onBrowsers(browsers);

    List<Action> sequence = builder.addTests(tests).build();

    assertSequence(expectedActions, sequence);

    ThreadedActionsRunner runner = (ThreadedActionsRunner) sequence.get(1);
    assertEquals(1, runner.getActions().size());
    RunTestsAction testAction = (RunTestsAction) runner.getActions().get(0);
    assertEquals(tests, testAction.getTests());
    assertEquals(captureConsole, testAction.isCaptureConsole());
  }

  private DefaultThreadedActionProvider threadedActionProvider(List<String> tests,
      List<String> commands, boolean reset, List<String> dryRunFor,
      boolean captureConsole) {
    return new DefaultThreadedActionProvider(actionFactory, null, reset, dryRunFor,
        captureConsole, tests, commands);
  }

  public void testAddTestsWithLocalServer() throws Exception {
    List<String> tests = tests();
    boolean captureConsole = true;
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null), null, null, threadedActionProvider(
            tests, Collections.<String> emptyList(), false, Collections.<String> emptyList(),
            captureConsole), Providers.<JsTestDriverClient> of(null), Providers
            .<URLTranslator> of(null), Providers.<URLRewriter> of(null));
    List<String> browsers = browsers();

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);
    builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(browsers);

    List<Action> sequence = builder.addTests(tests).build();

    assertSequence(expectedActions, sequence);

    ThreadedActionsRunner runner = (ThreadedActionsRunner) sequence.get(2);
    assertEquals(1, runner.getActions().size());
    RunTestsAction testAction = (RunTestsAction) runner.getActions().get(0);
    assertEquals(tests, testAction.getTests());
    assertEquals(captureConsole, testAction.isCaptureConsole());
  }

  public void testAddTestsAndDryRunFor() throws Exception {
    List<String> tests = tests();
    List<String> dryRunFor = new LinkedList<String>();

    dryRunFor.add("test.testFoo");
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null), null, null, threadedActionProvider(
            tests, Collections.<String> emptyList(), false, dryRunFor, false), Providers
            .<JsTestDriverClient> of(null), Providers.<URLTranslator> of(null), Providers
            .<URLRewriter> of(null));
    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addTests(tests).asDryRunFor(dryRunFor).build();

    ThreadedActionsRunner runner = findAction(sequence, ThreadedActionsRunner.class);
    assertEquals(2, runner.getActions().size());
    List<Class<? extends ThreadedAction>> expectedThreadedActions =
        new ArrayList<Class<? extends ThreadedAction>>();
    expectedThreadedActions.add(DryRunAction.class);
    expectedThreadedActions.add(RunTestsAction.class);
    this.<ThreadedAction> assertSequence(expectedThreadedActions, runner.getActions());
  }

  public void testAddTestsAndReset() throws Exception {
    List<String> tests = tests();
    boolean reset = true;
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null), null, null,
            threadedActionProvider(tests, Collections.<String> emptyList(), reset, Collections
                .<String> emptyList(), false), Providers.<JsTestDriverClient> of(null), Providers
                .<URLTranslator> of(null), Providers.<URLRewriter>of(null));

    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addTests(tests).reset(reset).build();

    ThreadedActionsRunner runner = findAction(sequence, ThreadedActionsRunner.class);
    assertEquals(2, runner.getActions().size());
    List<Class<? extends ThreadedAction>> expectedThreadedActions =
        new ArrayList<Class<? extends ThreadedAction>>();
    expectedThreadedActions.add(ResetAction.class);
    expectedThreadedActions.add(RunTestsAction.class);
    this.<ThreadedAction> assertSequence(expectedThreadedActions, runner.getActions());
  }
  
  public void testAddTestsWithDryRunForAndReset() throws Exception {
    List<String> tests = tests();
    List<String> dryRunFor = new LinkedList<String>();

    dryRunFor.add("test.testFoo");
    boolean reset = true;
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null), null, null, threadedActionProvider(
            tests, Collections.<String> emptyList(), reset, dryRunFor, false), Providers
            .<JsTestDriverClient> of(null), Providers.<URLTranslator> of(null), Providers
            .<URLRewriter> of(null));

    List<Action> sequence = builder.withLocalServerPort(1001).usingFiles(files, false).onBrowsers(
        browsers()).addTests(tests).asDryRunFor(dryRunFor).reset(reset).build();

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
    List<String> commands = Arrays.asList("'foo'+'bar'", "1+1");
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null), null, null, threadedActionProvider(
            Collections.<String> emptyList(), commands, false, Collections.<String> emptyList(),
            false), Providers.<JsTestDriverClient> of(null), Providers.<URLTranslator> of(null),
            Providers.<URLRewriter> of(null));
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
    List<String> tests = tests();
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null), null, null,
            threadedActionProvider(tests, Collections.<String> emptyList(), false, Collections
                .<String> emptyList(), false), Providers.<JsTestDriverClient> of(null), Providers
                .<URLTranslator> of(null), Providers.<URLRewriter> of(null));

    List<Action> actions = builder.addTests(tests).withLocalServerPort(999)
        .usingFiles(files, false).build();
    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
//    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(ThreadedActionsRunner.class);
//    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);
    this.<Action>assertSequence(expectedActions, actions);
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
