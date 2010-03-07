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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionRunner {
  private static final Logger logger = LoggerFactory.getLogger(ActionRunner.class);

  private final List<Action> actions;

  @Inject
  public ActionRunner(List<Action> actions) {
    this.actions = actions;
  }

  public void runActions() {
    Iterator<Action> iterator = actions.iterator();

    while (iterator.hasNext()) {
      Action action = iterator.next();
      logger.debug("Starting {}", action);
      action.run();
      logger.debug("Finishing {}", action);
    }
  }
}
