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

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class TestResult {

  public enum Result {
    passed, failed, error
  }

  private String result;
  private String message;
  private String parsedMessage;
  private String stack;
  private String log;
  private String testCaseName;
  private String testName;
  private float time;
  private BrowserInfo browserInfo;

  public TestResult(BrowserInfo browser, String result, String message, String log,
      String testCaseName, String testName, float time) {
    this.browserInfo = browser;
    this.result = result;
    this.message = message;
    this.log = log;
    this.testCaseName = testCaseName;
    this.testName = testName;
    this.time = time;
  }

  public TestResult() {
  }  

  public Result getResult() {
    return Result.valueOf(result);
  }

  public String getMessage() {
    return message;
  }

  public String getTestCaseName() {
    return testCaseName;
  }

  public String getTestName() {
    return testName;
  }

  public float getTime() {
    return time;
  }

  public void setBrowserInfo(BrowserInfo browserInfo) {
    this.browserInfo = browserInfo;
  }

  public void setParsedMessage(String parsedMessage) {
    this.parsedMessage = parsedMessage;
  }

  public void setStack(String stack) {
    this.stack = stack;
  }

  public String getParsedMessage() {
    return parsedMessage;
  }

  public String getStack() {
    return stack;
  }

  public BrowserInfo getBrowserInfo() {
    return browserInfo;
  }

  public String getLog() {
    return log;
  }
}
