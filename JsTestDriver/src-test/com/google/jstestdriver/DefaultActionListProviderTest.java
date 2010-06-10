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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.google.common.collect.Sets;
import com.google.inject.util.Providers;
import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.browser.CommandLineBrowserRunner;
import com.google.jstestdriver.hooks.ActionListProcessor;
import com.google.jstestdriver.hooks.TestsPreProcessor;
import com.google.jstestdriver.output.PrintXmlTestResultsAction;
import com.google.jstestdriver.output.XmlPrinter;
import com.google.jstestdriver.output.XmlPrinterImpl;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 * @author corysmith@google.com (Cory Smith)
 */
public class DefaultActionListProviderTest extends TestCase {
  Set<BrowserRunner> browsers =
    Sets.<BrowserRunner>newHashSet(
        new CommandLineBrowserRunner("browser", null));

  public void testParseFlagsAndCreateActionQueue() throws Exception {
    DefaultActionListProvider parser =
        createProvider(9876, false, Collections.<String> emptyList(), Collections
            .<ActionListProcessor> emptySet(), "", null);
    List<Action> actions = parser.get();

    ArrayList<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserActionsRunner.class);
    expectedActions.add(FailureCheckerAction.class);
    assertSequence(expectedActions, actions);
  }

  private DefaultActionListProvider createProvider(int port,
                                                   boolean reset,
                                                   List<String> tests,
                                                   Set<ActionListProcessor> processors,
                                                   String testOutput,
                                                   XmlPrinter xmlPrinter) {
    ActionFactory actionFactory =
        new ActionFactory(null, Collections.<TestsPreProcessor>emptySet(), SlaveBrowser.TIMEOUT);
    return new DefaultActionListProvider(
        actionFactory,
        null,
        tests,
        Collections.<String>emptyList(),
        reset,
        Collections.<String>emptyList(),
        false,
        port,
        Collections.<FileInfo>emptySet(),
        testOutput,
        new File("."),
        null,
        new BrowserActionsRunner(null, null, null, null, null, 0),
        Providers.<URLTranslator>of(null),
        Providers.<URLRewriter>of(null),
        new FailureAccumulator(), processors,
        xmlPrinter);
  }

  public void testParseWithServerAndReset() throws Exception {
    String serverAddress = "http://otherserver:8989";
    DefaultActionListProvider parser =
        createProvider(-1, true, Collections
            .<String> emptyList(), Collections.<ActionListProcessor>emptySet(), "", null);

    FlagsImpl flags = new FlagsImpl();
    flags.setServer(serverAddress);
    flags.setBrowser(Arrays.asList("browser1"));
    flags.setReset(true);

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(BrowserActionsRunner.class);
    expectedActions.add(FailureCheckerAction.class);

    List<Action> actions = parser.get();
    assertSequence(expectedActions, actions);
  }


  public void testParseFlagsAndCreateTestActions() throws Exception {
    List<String> tests = Arrays.asList("foo.testBar");
    DefaultActionListProvider parser =
        createProvider(9876, false, tests, Collections
            .<ActionListProcessor> emptySet(), "", null);

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserActionsRunner.class);
    expectedActions.add(ServerShutdownAction.class);
    expectedActions.add(FailureCheckerAction.class);

    List<Action> actions = parser.get();
    assertSequence(expectedActions, actions);
  }

  public void testXmlTestResultsActionIsAddedIfTestOutputFolderIsSet() throws Exception {
    List<String> tests = Arrays.asList("foo.testBar");
    DefaultActionListProvider parser =
        createProvider(9876, false, tests, Collections
            .<ActionListProcessor> emptySet(), ".", new XmlPrinterImpl(null, null));

    List<Class<? extends Action>> expectedActions = new ArrayList<Class<? extends Action>>();
    expectedActions.add(ServerStartupAction.class);
    expectedActions.add(BrowserActionsRunner.class);
    expectedActions.add(PrintXmlTestResultsAction.class);
    expectedActions.add(ServerShutdownAction.class);
    expectedActions.add(FailureCheckerAction.class);

    List<Action> actions = parser.get();
    assertSequence(expectedActions, actions);
  }

  private void assertSequence(List<Class<? extends Action>> expectedActions,
      List<Action> actions) {
    List<Class<? extends Action>> actual = new ArrayList<Class<? extends Action>>();
    for (Action action : actions) {
      actual.add(action.getClass());
    }
    assertEquals(expectedActions, actual);
  }
}
