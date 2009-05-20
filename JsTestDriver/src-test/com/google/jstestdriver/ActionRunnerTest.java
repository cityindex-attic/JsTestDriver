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

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class ActionRunnerTest extends TestCase {

  private static class TestRanAction implements Action {

    private boolean ran = false;

    public void run() {
      ran = true;
    }

    public boolean actionRan() {
      return ran;
    }
  }

  public void testCreateCommandQueueFromFlags() throws Exception {
    List<Action> actions =  new LinkedList<Action>();
    TestRanAction action =  new TestRanAction();

    actions.add(action);
    ActionRunner runner = new ActionRunner(actions);

    runner.runActions();
    assertTrue(action.actionRan());
  }
}
