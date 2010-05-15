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

import com.google.common.collect.Sets;
import com.google.inject.util.Providers;
import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.browser.CommandLineBrowserRunner;
import com.google.jstestdriver.hooks.TestsPreProcessor;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ActionSequenceBuilderTest extends TestCase {

  private LinkedHashSet<FileInfo> files = new LinkedHashSet<FileInfo>();
  ActionFactory actionFactory = new ActionFactory(null, Collections.<TestsPreProcessor>emptySet(), SlaveBrowser.TIMEOUT);

  public void testAddTestsWithRemoteServerAddress() throws Exception {
    List<String> tests = tests();
    boolean captureConsole = true;
    Set<BrowserRunner> browsers = browsers();
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(actionFactory, null, null,
            new BrowserActionsRunner(null, null, null, null, null, 0),
            Providers.<URLTranslator> of(null), Providers.<URLRewriter> of(null),
            new FailureAccumulator());

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(BrowserActionsRunner.class);
    expectedActions.add(FailureCheckerAction.class);
    builder.usingFiles(files, false);

    List<Action> sequence = builder.addTests(tests).build();

    assertSequence(expectedActions, sequence);
  }

  public void testAddTestsWithLocalServer() throws Exception {
    List<String> tests = tests();
    boolean captureConsole = true;
    ActionSequenceBuilder builder = new ActionSequenceBuilder(
        new ActionFactory(
            null,
            Collections.<TestsPreProcessor> emptySet(), SlaveBrowser.TIMEOUT),
            null, null, new BrowserActionsRunner(null, null, null, null, null, 0),
            Providers.<URLTranslator> of(null),
            Providers.<URLRewriter> of(null),
        new FailureAccumulator());
    Set<BrowserRunner> browsers = browsers();

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserActionsRunner.class);
    expectedActions.add(ServerShutdownAction.class);
    expectedActions.add(FailureCheckerAction.class);
    builder.withLocalServerPort(1001).usingFiles(files, false);

    List<Action> sequence = builder.addTests(tests).build();

    assertSequence(expectedActions, sequence);
  }

  public void testNoBrowsers() throws Exception {
    List<String> tests = tests();
    ActionSequenceBuilder builder =
        new ActionSequenceBuilder(new ActionFactory(null, Collections.<TestsPreProcessor>emptySet(), SlaveBrowser.TIMEOUT), null, null,
            new BrowserActionsRunner(null, null, null, null, null, 0), Providers
                .<URLTranslator> of(null), Providers.<URLRewriter> of(null),
            new FailureAccumulator());

    List<Action> actions = builder.addTests(tests).withLocalServerPort(999)
        .usingFiles(files, false).build();
    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
//    expectedActions.add(BrowserStartupAction.class);
    expectedActions.add(BrowserActionsRunner.class);
//    expectedActions.add(BrowserShutdownAction.class);
    expectedActions.add(ServerShutdownAction.class);
    expectedActions.add(FailureCheckerAction.class);
    this.<Action>assertSequence(expectedActions, actions);
  }

  private List<String> tests() {
    List<String> tests = new ArrayList<String>();
    tests.add("test.testFoo");
    return tests;
  }

  private Set<BrowserRunner> browsers() {
    return Sets.<BrowserRunner>newHashSet(
        new CommandLineBrowserRunner("foo", null));
  }

  private <T> void assertSequence(List<Class<? extends T>> expectedActions, List<T> actions) {
    List<Class<?>> actual = new ArrayList<Class<?>>();
    for (T action : actions) {
      actual.add(action.getClass());
    }
    assertEquals(expectedActions, actual);
  }
}
