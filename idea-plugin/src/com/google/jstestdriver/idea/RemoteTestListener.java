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
package com.google.jstestdriver.idea;

import com.google.jstestdriver.idea.ui.BrowserNode;
import com.google.jstestdriver.idea.ui.TestCaseNode;
import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.openapi.util.Key;

import java.util.HashMap;
import java.util.Map;

import static com.google.jstestdriver.TestResult.Result;

/**
 * Updates the Test Result UI panel in the IDE with Test Results. The results are streaming from the JSTD runner, so
 * we update the UI as each event arrives, to give better user feedback.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class RemoteTestListener {
  private final TestListenerContext context;
  private final Map<String, BrowserNode> browserMap = new HashMap<String, BrowserNode>();

  public RemoteTestListener(TestListenerContext ctx) {
    this.context = ctx;
  }

  // This method must only be called on the AWT event thread, as it updates the UI.
  public void onTestStarted(TestResultProtocolMessage message) {
    if (!browserMap.containsKey(message.browser)) {
      SMTestProxy browserNode = new SMTestProxy(message.browser, true, null);
      browserMap.put(message.browser, new BrowserNode(browserNode));
      onSuiteStarted(context.resultsForm().getTestsRootNode(), browserNode);
    }
    BrowserNode browser = browserMap.get(message.browser);

    if (!browser.testCaseMap.containsKey(message.testCase)) {
      SMTestProxy testCaseNode = new SMTestProxy(message.testCase, true, null);
      browser.testCaseMap.put(message.testCase, new TestCaseNode(testCaseNode));
      onSuiteStarted(browser.node, testCaseNode);
    }
    TestCaseNode testCase = browser.testCaseMap.get(message.testCase);

    SMTestProxy testNode = new SMTestProxy(message.testName, false, null);
    testCase.testProxyMap.put(message.testName, testNode);
    testCase.node.addChild(testNode);
    context.resultsForm().onTestStarted(testNode);
  }

  // This method must only be called on the AWT event thread, as it updates the UI.
  public void onTestFinished(TestResultProtocolMessage message) {
    BrowserNode browserNode = browserMap.get(message.browser);
    TestCaseNode testCaseNode = browserNode.testCaseMap.get(message.testCase);
    SMTestProxy testNode = testCaseNode.testProxyMap.get(message.testName);
    testNode.addStdOutput(message.log, Key.create("result"));
    testNode.setDuration(Math.round(message.duration));
    Result result = Result.valueOf(message.result);
    if (result == Result.passed) {
      testNode.setFinished();
      context.resultsForm().onTestFinished(testNode);
    } else {
      boolean isError = result == Result.error;
      testNode.setTestFailed(message.message, message.stack, isError);
      context.resultsForm().onTestFailed(testNode);
      testCaseNode.setTestFailed(result);
      browserNode.setTestFailed(result);
    }
    if (testCaseNode.allTestsComplete()) {
      onSuiteFinished(testCaseNode.node);
    }
    if (browserNode.allTestCasesComplete()) {
      onSuiteFinished(browserNode.node);
    }
  }

  // This method must only be called on the AWT event thread, as it updates the UI.
  public void onSuiteFinished(SMTestProxy node) {
    node.setFinished();
    context.resultsForm().onSuiteFinished(node);
  }

  // This method must only be called on the AWT event thread, as it updates the UI.
  public void onSuiteStarted(SMTestProxy parent, SMTestProxy node) {
    parent.addChild(node);
    context.resultsForm().onSuiteStarted(node);
  }
}
