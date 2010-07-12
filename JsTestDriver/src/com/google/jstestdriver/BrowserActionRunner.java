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

import com.google.jstestdriver.model.RunData;
import com.google.jstestdriver.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
// TODO(corysmith): Work out a better return value.
public class BrowserActionRunner implements Callable<RunData> {
  private static final Logger logger = LoggerFactory.getLogger(BrowserActionRunner.class);

  private final String id;
  private final JsTestDriverClient client;
  private final List<BrowserAction> actions;

  private final StopWatch stopWatch;

  private final RunData runData;

  public BrowserActionRunner(String id, JsTestDriverClient client, List<BrowserAction> actions,
      StopWatch stopWatch, RunData runData) {
    this.id = id;
    this.client = client;
    this.actions = actions;
    this.stopWatch = stopWatch;
    this.runData = runData;
  }

  public RunData call() {
    RunData currentRunData = runData;
    for (BrowserAction action : actions) {
      stopWatch.start("run %s", action);
      logger.debug("Running BrowserAction {}", action);
      currentRunData = action.run(id, client, currentRunData);
      stopWatch.stop("run %s", action);
    }
    return currentRunData;
  }
}
