package com.google.jstestdriver.idea;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.testframework.sm.runner.ui.SMTestRunnerResultsForm;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * TODO: Document this class / interface here
 *
 * @since v4.2
 */
public final class TestListenerContext {

  private final Future<TestRunnerState.ProcessData> processData;
  private final CountDownLatch gate;

  public TestListenerContext(Future<TestRunnerState.ProcessData> processData, CountDownLatch gate) {
    this.processData = processData;
    this.gate = gate;
  }


  public void socketStarted() {
    gate.countDown();
  }

  public SMTestRunnerResultsForm resultsForm() {
    return consoleView().getResultsViewer();
  }

  public SMTRunnerConsoleView consoleView() {
    return getData().consoleView;
  }

  public ProcessHandler processHandler() {
    return getData().processHandler;
  }

  private TestRunnerState.ProcessData getData() {
    try {
      return processData.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
