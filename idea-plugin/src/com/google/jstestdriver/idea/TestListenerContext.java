package com.google.jstestdriver.idea;

import com.google.jstestdriver.idea.TestRunnerState.ProcessData;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.testframework.sm.runner.ui.SMTestRunnerResultsForm;

import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Dariusz Kordonski (dariusz.kordonski@googlemail.com)
 */
public final class TestListenerContext {

  private final Future<TestRunnerState.ProcessData> processData;

  public TestListenerContext(Future<ProcessData> processData) {
    this.processData = processData;
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
      return processData.get(10, SECONDS);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
