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
package com.google.jstestdriver.output;

import com.google.jstestdriver.FileResult;
import com.google.jstestdriver.TestResult;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public interface TestResultListener {
  /**
   * Called on the browser thread.
   */
  public void onTestComplete(TestResult testResult);

  /**
   * Called on the browser thread.
   */
  public void onFileLoad(String browser, FileResult fileResult);

  /**
   * To be called from the main thread, after all the browser threads have completed running.
   * TODO: this method can go away, if the summary stdout printing happens in its own action
   */
  public void finish();
}
