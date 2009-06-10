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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionParser {

  private final CapturedBrowsers capturedBrowsers = new CapturedBrowsers();
  private final ActionFactory actionFactory;

  public ActionParser(ActionFactory actionFactory) {
    this.actionFactory = actionFactory;
  }

  public List<Action> parseFlags(Flags flags, Set<String> fileSet, String defaultServerAddress) {
    List<Action> actions = new LinkedList<Action>();
    ServerStartupAction serverStartupAction = null;
    BrowserStartupAction browserStartupAction = null;
    Set<String> clientFiles = null;
    JsTestDriverClient client = null;
    String serverAddress = defaultServerAddress;

    if ((flags.getPort() != -1 && (flags.getTests().size() > 0 || flags.getBrowser().size() > 0))) {
      serverAddress = String.format("http://%s:%d", "127.0.0.1", flags.getPort());
    } else if (flags.getServer() != null) {
      serverAddress = flags.getServer();
    }
    if (flags.getPort() != -1) {
      Map<String, String> files = new HashMap<String, String>();

      if (flags.getPreloadFiles()) {
        for (String file : fileSet) {
          files.put(file, readFile(file));
        }
      }
      serverStartupAction =
        actionFactory.getServerStartupAction(flags.getPort(), capturedBrowsers, new FilesCache(
            files));

      actions.add(serverStartupAction);
    }
    // client
    if (flags.getTests().size() > 0 || flags.getReset() || !flags.getArguments().isEmpty()) {
      if (serverAddress != null && serverAddress.length() > 0) {
        client = actionFactory.getJsTestDriverClient(fileSet, serverAddress);
      } else {
        throw new RuntimeException("Oh snap ! the Server address was never defined !");
      }
    }
    List<String> browserPath = flags.getBrowser();

    if (!browserPath.isEmpty()) {
      browserStartupAction = new BrowserStartupAction(browserPath, serverAddress);

      capturedBrowsers.addObserver(browserStartupAction);
      actions.add(browserStartupAction);
    }
    List<ThreadedAction> threadedActions = new ArrayList<ThreadedAction>();
    if (flags.getReset()) {
      ResetAction resetAction = new ResetAction();

      threadedActions.add(resetAction);
    }
    RunTestsAction runTestsAction = null;
    List<String> tests = flags.getTests();

    if (tests.size() > 0) {
      runTestsAction = new RunTestsAction(tests, new ResponsePrinterFactory(
          flags.getTestOutput(), System.out, client, flags.getVerbose()),
          flags.getCaptureConsole());

      threadedActions.add(runTestsAction);
    }
    List<String> commands = flags.getArguments();

    if (!commands.isEmpty()) {
      for (String cmd : commands) {
        EvalAction evalAction =  new EvalAction(cmd);

        threadedActions.add(evalAction);
      }
    }
    if (threadedActions.size() > 0) {
      ThreadedActionsRunner actionsRunner = new ThreadedActionsRunner(client, threadedActions,
          Executors.newCachedThreadPool());

      actions.add(actionsRunner);
    }
    if (flags.getPort() != -1 && flags.getTests().size() > 0) {
      Action browserShutdownAction = new BrowserShutdownAction(browserStartupAction);
      Action serverShutdownAction = new ServerShutdownAction(serverStartupAction,
          runTestsAction);

      actions.add(browserShutdownAction);
      actions.add(serverShutdownAction);
    }
    return actions;
  }

  private String readFile(String file) {
    BufferedInputStream bis = null;
    try {
      bis = new BufferedInputStream(new FileInputStream(file));
      StringBuilder sb = new StringBuilder();

      for (int c = bis.read(); c != -1; c = bis.read()) {
        sb.append((char) c);
      }
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException("Impossible to read file: " + file, e);
    } finally {
      if (bis != null) {
        try {
          bis.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }
}
