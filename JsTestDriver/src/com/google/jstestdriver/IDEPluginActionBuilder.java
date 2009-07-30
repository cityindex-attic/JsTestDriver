// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * A builder for IDE's to use. Minimizes the surface area of the API which needs to
 * be maintained on the IDE plugin side.
 * TODO(jeremiele) We should rename this for other API uses. Refactor the crap out of this.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IDEPluginActionBuilder {

  private final ConfigurationParser configParser;
  private final String serverAddress;
  private final ActionFactory actionFactory;

  private List<String> tests = new LinkedList<String>();

  @Inject
  public IDEPluginActionBuilder(ConfigurationParser configParser, String serverAddress,
                                ActionFactory actionFactory) {
    this.configParser = configParser;
    this.serverAddress = serverAddress;
    this.actionFactory = actionFactory;
  }

  public IDEPluginActionBuilder addAllTests() {
    tests.add("all");
    return this;
  }

  public ActionRunner build() {
    List<Action> actions = new LinkedList<Action>();
    configParser.parse();
    JsTestDriverClient client = actionFactory.getJsTestDriverClient(configParser.getFilesList(),
        serverAddress);

    List<ThreadedAction> threadedActions = createThreadedActions(client);

    if (!threadedActions.isEmpty()) {
      actions.add(new ThreadedActionsRunner(client, threadedActions, Executors
          .newCachedThreadPool()));
    }
    return new ActionRunner(actions);
  }

  private List<ThreadedAction> createThreadedActions(JsTestDriverClient client) {
    List<ThreadedAction> threadedActions = new ArrayList<ThreadedAction>();

    if (!tests.isEmpty()) {
      threadedActions.add(new RunTestsAction(tests,
          new ResponsePrinterFactory("", System.out, client, false), true));
    }
    return threadedActions;
  }

  public IDEPluginActionBuilder sendTestResultsTo(ResponseStream responseStream) {
    return this;
  }
}
