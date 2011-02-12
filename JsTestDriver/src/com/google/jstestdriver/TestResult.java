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

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
// TODO(corysmith): move to protocol package.
// TODO(corysmith): factor out a parsed test result.
public class TestResult {

  public enum Result {
    passed, failed, error, started
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
  private Map<String, String> data = Maps.newHashMap();

  public Map<String, String> getData() {
    return data;
  }

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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
      + ((browserInfo == null) ? 0 : browserInfo.hashCode());
    result = prime * result + ((log == null) ? 0 : log.hashCode());
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result
      + ((parsedMessage == null) ? 0 : parsedMessage.hashCode());
    result = prime * result
      + ((this.result == null) ? 0 : this.result.hashCode());
    result = prime * result + ((stack == null) ? 0 : stack.hashCode());
    result = prime * result
      + ((testCaseName == null) ? 0 : testCaseName.hashCode());
    result = prime * result + ((testName == null) ? 0 : testName.hashCode());
    result = prime * result + Float.floatToIntBits(time);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TestResult other = (TestResult) obj;
    if (browserInfo == null) {
      if (other.browserInfo != null)
        return false;
    } else if (!browserInfo.equals(other.browserInfo))
      return false;
    if (log == null) {
      if (other.log != null)
        return false;
    } else if (!log.equals(other.log))
      return false;
    if (message == null) {
      if (other.message != null)
        return false;
    } else if (!message.equals(other.message))
      return false;
    if (parsedMessage == null) {
      if (other.parsedMessage != null)
        return false;
    } else if (!parsedMessage.equals(other.parsedMessage))
      return false;
    if (result == null) {
      if (other.result != null)
        return false;
    } else if (!result.equals(other.result))
      return false;
    if (stack == null) {
      if (other.stack != null)
        return false;
    } else if (!stack.equals(other.stack))
      return false;
    if (testCaseName == null) {
      if (other.testCaseName != null)
        return false;
    } else if (!testCaseName.equals(other.testCaseName))
      return false;
    if (testName == null) {
      if (other.testName != null)
        return false;
    } else if (!testName.equals(other.testName))
      return false;
    if (Float.floatToIntBits(time) != Float.floatToIntBits(other.time))
      return false;
    return true;
  }
  @Override
  public String toString() {
    return String.format("%s: %s; %s; %s; %s; %s,; %s; %s", 
                         getClass().getSimpleName(),
                         browserInfo,
                         result,
                         message,
                         log,
                         testCaseName,
                         testName,
                         time);
  }
}
