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

import com.google.gson.Gson;
import com.google.jstestdriver.browser.BrowserPanicException;
import com.google.jstestdriver.output.TestResultListener;

import java.util.Collection;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class RunTestsActionResponseStream implements ResponseStream {

  private final TestResultGenerator testResultGenerator;
  private final TestResultListener listener;
  private final FailureAccumulator accumulator;
  private final Gson gson = new Gson();

  public RunTestsActionResponseStream(TestResultGenerator testResultGenerator,
      TestResultListener listener, FailureAccumulator accumulator) {
    this.testResultGenerator = testResultGenerator;
    this.listener = listener;
    this.accumulator = accumulator;
  }

  public void stream(Response response) {
    switch(response.getResponseType()) {
      case TEST_RESULT:
        Collection<TestResult> testResults =
            testResultGenerator.getTestResults(response);
        for (TestResult result : testResults) {
          if (result.getResult() == TestResult.Result.failed
              || result.getResult() == TestResult.Result.error) {
            accumulator.add();
          }
          listener.onTestComplete(result);
        }
        break;
      case FILE_LOAD_RESULT:
        LoadedFiles files = gson.fromJson(response.getResponse(),
                                          response.getGsonType());
        for (FileResult result : files.getLoadedFiles()) {
          if (!result.isSuccess()) {
            accumulator.add();
          }
          listener.onFileLoad(response.getBrowser().toString(), result);
        }
        break;
      case BROWSER_PANIC:
        BrowserPanic panic = gson.fromJson(response.getResponse(), response.getGsonType());
        throw new BrowserPanicException(panic.getBrowserInfo(), panic.getCause());
    }
  }

  public void finish() {
    listener.finish();
  }
}
