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

import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ThreadedActionRunner implements Runnable {

  private final String id;
  private final JsTestDriverClient client;
  private final CountDownLatch latch;
  private final List<ThreadedAction> actions;

  public ThreadedActionRunner(String id, JsTestDriverClient client, CountDownLatch latch,
      List<ThreadedAction> actions) {
    this.id = id;
    this.client = client;
    this.latch = latch;
    this.actions = actions;
  }

  public void run() {
    try {
      for (ThreadedAction action : actions) {
        action.run(id, client);
      }
    } finally {
      latch.countDown();
    }
  }
  
  public List<ThreadedAction> getActions() {
    return actions;
  }
}
