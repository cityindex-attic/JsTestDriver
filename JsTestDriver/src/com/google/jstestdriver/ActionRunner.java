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

import com.google.inject.Inject;
import com.google.jstestdriver.util.StopWatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionRunner {
  private static final Logger stopWatchLogger =
      LoggerFactory.getLogger(StopWatch.class);
  private static final Logger logger =
    LoggerFactory.getLogger(ActionRunner.class);

  private final List<Action> actions;

  private final StopWatch stopWatch;

  @Inject
  public ActionRunner(List<Action> actions, StopWatch stopWatch) {
    this.actions = actions;
    this.stopWatch = stopWatch;
  }

  public void runActions() {
    Iterator<Action> iterator = actions.iterator();

    while (iterator.hasNext()) {
      Action action = iterator.next();
      stopWatch.start(action.toString());
      logger.trace("Running {}", action);
      action.run();
      logger.trace("Finished {}", action);
      stopWatch.stop(action.toString());
    }

    Writer writer = new StringWriter();
    stopWatch.print(writer);
    try {
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
    stopWatchLogger.info(writer.toString());
  }
}
