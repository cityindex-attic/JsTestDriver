/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.protocol;

import com.google.jstestdriver.BrowserInfo;


/**
 * Represents a log message from a browser.
 *
 * @author corbinrsmith@gmail.com
 *
 */
public class BrowserLog {
  String source = "";
  String message = "";
  int level = 1;
  BrowserInfo browser = null;

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public BrowserInfo getBrowser() {
    return browser;
  }

  public void setBrowser(BrowserInfo browser) {
    this.browser = browser;
  }

  @Override
  public String toString() {
    return "BrowserLog [source=" + source + ",\n message=" + message + ",\n level=" + level
        + ",\n browser=" + browser + "\n]";
  }

}
