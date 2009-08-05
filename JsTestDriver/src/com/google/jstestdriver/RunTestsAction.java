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

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class RunTestsAction extends ThreadedAction {

  private final List<String> tests;
  private final boolean captureConsole;

  public RunTestsAction(ResponseStreamFactory responseStreamFactory,
      List<String> tests, boolean captureConsole) {
    super(responseStreamFactory);
    this.tests = tests;
    this.captureConsole = captureConsole;
  }

  @Override
  public void run(String id, JsTestDriverClient client) {
    ResponseStream runTestsActionResponseStream =
        responseStreamFactory.getRunTestsActionResponseStream(id);
    if (tests.size() == 1 && tests.get(0).equals("all")) {
      client.runAllTests(id, runTestsActionResponseStream, captureConsole);
    } else if (tests.size() > 0) {
      client.runTests(id, runTestsActionResponseStream, tests, captureConsole);
    }
  }

  public List<String> getTests() {
    return tests;
  }

  public boolean isCaptureConsole() {
    return captureConsole;
  }
}
