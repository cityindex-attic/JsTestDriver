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

import com.google.jstestdriver.hooks.TestsPreProcessor;
import com.google.jstestdriver.model.RunData;

import java.util.List;
import java.util.Set;

/**
 * Run the tests in the browser.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class RunTestsAction implements BrowserAction {

  private final List<String> tests;
  private final boolean captureConsole;
  private final Set<TestsPreProcessor> preProcessors;
  private final ResponseStreamFactory responseStreamFactory;

  public RunTestsAction(ResponseStreamFactory responseStreamFactory,
                        List<String> tests,
                        boolean captureConsole,
                        Set<TestsPreProcessor> preProcessors) {
    this.responseStreamFactory = responseStreamFactory;
    this.tests = tests;
    this.captureConsole = captureConsole;
    this.preProcessors = preProcessors;
  }

  /**
   * @param id The Browser id to execute tests in.
   * @param client The client to run tests in.
   */
  public RunData run(String id, JsTestDriverClient client, RunData runData) {
    List<String> testsToRun = tests;
    for (TestsPreProcessor preProcessor : preProcessors) {
      // makes sure that the preProcessor doesn't modify the base test list
      // by providing an Iterator
      testsToRun = preProcessor.process(id, testsToRun.iterator());
    }
    ResponseStream runTestsActionResponseStream =
          responseStreamFactory.getRunTestsActionResponseStream(id);

    if (testsToRun.size() == 1 && testsToRun.get(0).equals("all")) {
      client.runAllTests(id, runTestsActionResponseStream, captureConsole, runData);
    } else if (testsToRun.size() > 0) {
      client.runTests(id, runTestsActionResponseStream, testsToRun, captureConsole, runData);
    }
    return runData.recordResponse(runTestsActionResponseStream);
  }

  public List<String> getTests() {
    return tests;
  }

  public boolean isCaptureConsole() {
    return captureConsole;
  }
}
