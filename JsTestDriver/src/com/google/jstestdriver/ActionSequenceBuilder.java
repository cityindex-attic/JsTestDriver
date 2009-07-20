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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * A builder for creating a sequence of {@link Action}s to be run by the
 * ActionRunner.
 * 
 * @author corysmith
 */
public class ActionSequenceBuilder {

  static private void require(Object obj, String msg) {
    if (obj == null) {
      throw new IllegalArgumentException(msg);
    }
    if (obj instanceof String && ((String) obj).length() < 1) {
      throw new IllegalArgumentException(msg);
    }
    if (obj instanceof Collection<?> && ((Collection<?>) obj).size() < 1) {
      throw new IllegalArgumentException(msg);
    }
  }

  private final ActionFactory actionFactory;
  private List<String> browsers = new LinkedList<String>();
  private boolean captureConsole;
  private CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
  private boolean dryRun;
  private Map<String, FileData> files = new HashMap<String, FileData>();
  private Set<String> fileSet;
  private Set<String> filesToServe;
  private int localServerPort = -1;
  private boolean preloadFiles = false;
  private String remoteServerAddress;
  private boolean reset;
  private List<String> tests = new LinkedList<String>();
  private List<ThreadedAction> threadedActions;
  private boolean verbose;

  private String xmlOutputDir;
  private List<String> commands = new LinkedList<String>();
  private FileReader fileReader;
  
  /** Begins the building of an action sequence. */
  public ActionSequenceBuilder(ActionFactory actionFactory, FileReader fileReader) {
    this.actionFactory = actionFactory;
    this.fileReader = fileReader;
  }

  /** Add the Browser startup and shutdown actions to the actions stack. */
  private void addBrowserControlActions(List<Action> actions) {
    if (!browsers.isEmpty()) {
      BrowserStartupAction browserStartupAction = new BrowserStartupAction(browsers,
          getServerAddress());
      capturedBrowsers.addObserver(browserStartupAction);
      actions.add(0, browserStartupAction);
      actions.add(new BrowserShutdownAction(browserStartupAction));
    }
  }

  /** Wraps the current sequence of actions with the server start and stop actions. */
  private void addServerActions(List<Action> actions, boolean leaveServerRunning) {
    if (preloadFiles) {
      for (String file : fileSet) {
        files.put(file, new FileData(file, fileReader.readFile(this, file), new File(
            file).lastModified()));
      }
    }
    ServerStartupAction serverStartupAction = actionFactory.getServerStartupAction(localServerPort,
        capturedBrowsers, new FilesCache(files));
    actions.add(0, serverStartupAction);
    if (!leaveServerRunning) {
      actions.add(new ServerShutdownAction(serverStartupAction));
    }
  }

  /**
   * Adds tests to the action sequence. 
   * @param tests The list of tests to be executed during this sequence.
   * @param xmlOutputDir The directory to store the test results in.
   * @param verbose Indicates if the test output should be verbose.
   * @param captureConsole Indicates if the console messaging should be included in the test.
   * @return the current builder.
   */
  public ActionSequenceBuilder addTests(List<String> tests, String xmlOutputDir, boolean verbose,
      boolean captureConsole) {
    this.tests.addAll(tests);
    this.xmlOutputDir = xmlOutputDir;
    this.verbose = verbose;
    this.captureConsole = captureConsole;
    return this;
  }

  /** Indicates that any tests should be executed as a dry run. */
  public ActionSequenceBuilder asDryRun(boolean dryRun) {
    this.dryRun = dryRun;
    return this;
  }

  /** Creates and returns a sequence of actions. */
  public List<Action> build() {
    List<Action> actions = new LinkedList<Action>();
    require(getServerAddress(), "Oh snap! the Server address was never defined!");
    JsTestDriverClient client = actionFactory.getJsTestDriverClient(fileSet, getServerAddress());
    
    threadedActions = createThreadedActions(client);

    if (!threadedActions.isEmpty()) {
      actions.add(new ThreadedActionsRunner(client,
          threadedActions, Executors.newCachedThreadPool()));
    }

    // wrap the actions with the setup/teardown actions.
    addBrowserControlActions(actions);
    if (needToStartServer()) {
      addServerActions(actions, leaveServerRunning());
    }
    return actions;
  }
  
  /** Method that derives whether or not to leave the server running. */
  private boolean leaveServerRunning() {
    return tests.isEmpty() && commands.isEmpty() && !dryRun && !reset;
  }

  /** Creates and returns all threaded actions. */
  private List<ThreadedAction> createThreadedActions(JsTestDriverClient client) {
    List<ThreadedAction> threadedActions = new ArrayList<ThreadedAction>();
    if (reset) {
      threadedActions.add(new ResetAction());
    }
    if (dryRun) {
      threadedActions.add(new DryRunAction());
    }
    if (!tests.isEmpty()) {
      threadedActions.add(new RunTestsAction(tests, new ResponsePrinterFactory(xmlOutputDir,
          System.out, client, verbose), captureConsole));
    }
    if (!commands.isEmpty()) {
      for (String cmd : commands) {
        threadedActions.add(new EvalAction(cmd));
      }
    }
    return threadedActions;
  }

  private String getServerAddress() {
    if (remoteServerAddress != null) {
      return remoteServerAddress;
    }
    if (localServerPort != -1) {
      return String.format("http://%s:%d", "127.0.0.1", localServerPort);
    }
    return null;
  }

  private boolean needToStartServer() {
    return localServerPort != -1;
  }

  /**
   * Indicates a list of browsers that the actions should be executed in. This is required.
   */
  public ActionSequenceBuilder onBrowsers(List<String> browsers) {
    this.browsers.addAll(browsers);
    return this;
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
   * @param fileSet The files to be loaded into the browser.
   * @param preloadFiles Indicates if thes files should be preloaded into the server.
   * @return The current builder.
   */
  public ActionSequenceBuilder usingFiles(Set<String> fileSet, boolean preloadFiles) {
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
   * Defines a remote server to run against.
   */
  public ActionSequenceBuilder withRemoteServer(String serverAddress) {
    this.remoteServerAddress = serverAddress;
    return this;
  }

  /** Adds a list commands to executed in the browser and the results to be returned. */
  public ActionSequenceBuilder addCommands(List<String> commands) {
    this.commands.addAll(commands);
    return this;
  }
}
