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

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.jstestdriver.browser.BrowserActionExecutorAction;
import com.google.jstestdriver.output.PrintXmlTestResultsAction;
import com.google.jstestdriver.output.XmlPrinter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A builder for creating a sequence of {@link Action}s to be run by the
 * ActionRunner.
 *
 * @author corysmith
 */
public class ActionSequenceBuilder {

  private final ActionFactory actionFactory;
  private final FileLoader fileLoader;
  private CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
  private HashMap<String, FileInfo> files = new LinkedHashMap<String, FileInfo>();
  private Set<FileInfo> fileSet;
  private int localServerPort = -1;
  private boolean preloadFiles = false;
  private boolean reset;
  private List<String> tests = new LinkedList<String>();
  private List<String> dryRunFor = new LinkedList<String>();
  private List<String> commands = new LinkedList<String>();
  private XmlPrinter xmlPrinter;
  private final BrowserActionExecutorAction browserActionsRunner;
  private final FailureCheckerAction failureCheckerAction;

  /**
   * Begins the building of an action sequence.
   * @param failureCheckerAction TODO
   */
  @Inject
  public ActionSequenceBuilder(ActionFactory actionFactory,
                               FileLoader fileLoader,
                               ResponseStreamFactory responseStreamFactory,
                               BrowserActionExecutorAction browserActionsRunner,
                               FailureCheckerAction failureCheckerAction) {
    this.actionFactory = actionFactory;
    this.fileLoader = fileLoader;
    this.browserActionsRunner = browserActionsRunner;
    this.failureCheckerAction = failureCheckerAction;
  }

  /**
   * Wraps the current sequence of actions with the server start and stop
   * actions.
   */
  private void addServerActions(List<Action> actions, boolean leaveServerRunning) {
    if (preloadFiles) {
      for (FileInfo file : fileLoader.loadFiles(fileSet, true)) {
        files.put(file.getFilePath(), file);
      }
    }
    ServerStartupAction serverStartupAction =
        actionFactory.getServerStartupAction(localServerPort, capturedBrowsers,
            new FilesCache(files));
    actions.add(0, serverStartupAction);
    if (!leaveServerRunning) {
      actions.add(new ServerShutdownAction(serverStartupAction));
    }
  }

  /**
   * Adds tests to the action sequence.
   *
   * @param tests
   *          The list of tests to be executed during this sequence.
   * @return the current builder.
   */
  public ActionSequenceBuilder addTests(List<String> tests) {
    this.tests.addAll(tests);
    return this;
  }

  /** Indicates that tests should be executed as a dry run. */
  public ActionSequenceBuilder asDryRunFor(List<String> dryRunFor) {
    this.dryRunFor.addAll(dryRunFor);
    return this;
  }

  /** Creates and returns a sequence of actions. */
  public List<Action> build() {
    List<Action> actions = new LinkedList<Action>();
    actions.add(browserActionsRunner);
    if (xmlPrinter != null) {
      actions.add(new PrintXmlTestResultsAction(xmlPrinter));
    }

    // wrap the actions with the setup/teardown actions.
    if (needToStartServer()) {
      addServerActions(actions, leaveServerRunning());
    }
    if (!tests.isEmpty()) {
      actions.add(failureCheckerAction);
    }
    return actions;
  }

  /** Method that derives whether or not to leave the server running. */
  private boolean leaveServerRunning() {
    return tests.isEmpty() && commands.isEmpty() && !reset && dryRunFor.isEmpty();
  }

  private boolean needToStartServer() {
    return localServerPort != -1;
  }

  /**
   * Indicates that the browser should be reset before executing the tests.
   */
  public ActionSequenceBuilder reset(boolean reset) {
    this.reset = reset;
    return this;
  }

  /**
   * Defines a list of files that should be loaded into the list of browsers.
   *
   * @param fileSet
   *          The files to be loaded into the browser.
   * @param preloadFiles
   *          Indicates if thes files should be preloaded into the server.
   * @return The current builder.
   */
  public ActionSequenceBuilder usingFiles(Set<FileInfo> fileSet, boolean preloadFiles) {
    this.fileSet = fileSet;
    this.preloadFiles = preloadFiles;
    return this;
  }

  /**
   * Indicates that a local server should be started on the provided port.
   */
  public ActionSequenceBuilder withLocalServerPort(int localServerPort) {
    this.localServerPort = localServerPort;
    return this;
  }

  /**
   * Adds a list commands to executed in the browser and the results to be
   * returned.
   */
  public ActionSequenceBuilder addCommands(List<String> commands) {
    this.commands.addAll(commands);
    return this;
  }

  public ActionSequenceBuilder printingResultsWhenFinished(XmlPrinter printer) {
    this.xmlPrinter = printer;
    return this;
  }
}
