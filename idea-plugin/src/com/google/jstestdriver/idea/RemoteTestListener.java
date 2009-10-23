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

import com.intellij.execution.testframework.sm.runner.SMTestProxy;
import com.intellij.execution.testframework.sm.runner.ui.SMTestRunnerResultsForm;
import com.intellij.util.concurrency.SwingWorker;

import com.google.jstestdriver.TestResult;
import static com.google.jstestdriver.TestResult.Result;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;

/**
 * Sits on the IDE side of the socket communication of test results from the test runner.
 * It updates the IDEA test runner framework when test results are available.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class RemoteTestListener {
  private final SMTestRunnerResultsForm testRunnerResultsForm;
  private ServerSocket socket;
  private Socket client;
  private ObjectInputStream in;
  private Map<String, BrowserNode> browserMap = new HashMap<String, BrowserNode>();

  public RemoteTestListener(SMTestRunnerResultsForm testRunnerResultsForm) {
    this.testRunnerResultsForm = testRunnerResultsForm;
  }

  public void onTestStarted(TestResultProtocolMessage message) {
    if (!browserMap.containsKey(message.browser)) {
      SMTestProxy browserNode = new SMTestProxy(message.browser, true, null);
      browserMap.put(message.browser, new BrowserNode(browserNode));
      onSuiteStarted(testRunnerResultsForm.getTestsRootNode(), browserNode);
    }
    RemoteTestListener.BrowserNode browser = browserMap.get(message.browser);

    if (!browser.testCaseMap.containsKey(message.testCase)) {
      SMTestProxy testCaseNode = new SMTestProxy(message.testCase, true, null);
      browser.testCaseMap.put(message.testCase, new TestCaseNode(testCaseNode));
      onSuiteStarted(browser.node, testCaseNode);
    }
    RemoteTestListener.TestCaseNode testCase = browser.testCaseMap.get(message.testCase);

    SMTestProxy testNode = new SMTestProxy(message.testName, false, null);
    testCase.testProxyMap.put(message.testName, testNode);
    testCase.node.addChild(testNode);
    testRunnerResultsForm.onTestStarted(testNode);
  }

  public void onTestFinished(TestResultProtocolMessage message) {
    RemoteTestListener.BrowserNode browserNode = browserMap.get(message.browser);
    RemoteTestListener.TestCaseNode testCaseNode = browserNode.testCaseMap.get(message.testCase);
    SMTestProxy testNode = testCaseNode.testProxyMap.get(message.testName);
    testNode.addStdOutput(message.log);
    testNode.setDuration(Math.round(message.duration));
    Result result = Result.valueOf(message.result);
    if (result == Result.passed) {
      testNode.setFinished();
      testRunnerResultsForm.onTestFinished(testNode);
    } else {
      boolean isError = result == Result.error;
      testNode.setTestFailed(message.message, message.stack, isError);
      testRunnerResultsForm.onTestFailed(testNode);
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

  public void onSuiteFinished(SMTestProxy node) {
    node.setFinished();
    testRunnerResultsForm.onSuiteFinished(node);
  }

  public void onSuiteStarted(SMTestProxy parent, SMTestProxy node) {
    parent.addChild(node);
    testRunnerResultsForm.onSuiteStarted(node);
  }

  public void listen(final int port) {
    SwingWorker worker = new SwingWorker() {
      public Object construct() {
        try {
          socket = new ServerSocket(port);
          return socket.accept();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      @Override
      public void finished() {
        client = (Socket) getValue();
        try {
          in = new ObjectInputStream(client.getInputStream());
          while (true) {
            try {
              TestResultProtocolMessage message = (TestResultProtocolMessage) in.readObject();
              if (message.isDryRun()) {
                onTestStarted(message);
              } else {
                onTestFinished(message);
              }
            } catch (EOFException e) {
              break;
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    };
    worker.start();
  }

  public void shutdown() {
    try {
      if (socket != null) {
        socket.close();
      }
      if (client != null) {
        client.close();
      }
      if (in != null) {
        in.close();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class BrowserNode {
    public Map<String, TestCaseNode> testCaseMap = new HashMap<String, TestCaseNode>();
    private final SMTestProxy node;
    private Result worstResult = Result.passed;

    public BrowserNode(SMTestProxy node) {
      this.node = node;
    }

    public boolean allTestCasesComplete() {
      for (TestCaseNode testCaseNode : testCaseMap.values()) {
        if (!testCaseNode.allTestsComplete()) {
          return false;
        }
      }
      return true;
    }

    public void setTestFailed(TestResult.Result result) {
      if (result == Result.error && worstResult != Result.error) {
        node.setTestFailed("", "", true);
      } else if (result == Result.failed && worstResult == Result.passed) {
        node.setTestFailed("", "", false);
      }
    }
  }

  private class TestCaseNode {
    public Map<String, SMTestProxy> testProxyMap = new HashMap<String, SMTestProxy>();
    private final SMTestProxy node;
    private Result worstResult = Result.passed;

    public TestCaseNode(SMTestProxy node) {
      this.node = node;
    }

    public boolean allTestsComplete() {
      for (SMTestProxy testProxy : testProxyMap.values()) {
        if (testProxy.isInProgress()) {
          return false;
        }
      }
      return true;
    }

    public void setTestFailed(TestResult.Result result) {
      if (result == Result.error && worstResult != Result.error) {
        node.setTestFailed("", "", true);
      } else if (result == Result.failed && worstResult == Result.passed) {
        node.setTestFailed("", "", false);
      }
    }
  }
}
