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
package com.google.eclipse.javascript.jstestdriver.core.model;

/**
 * The browsers that JsTestDriver works with.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public enum Browser {
  FIREFOX("icons/Firefox.png"),
  IE("icons/IE.png"),
  OPERA("icons/Opera.png"),
  CHROME("icons/Chrome.png"),
  SAFARI("icons/Safari.png");

  private final String imagePath;

  private Browser(String imagePath) {
    this.imagePath = imagePath;
  }
  /**
   * Absolute path to the icon representing the browser.
   *
   * @return the absolute path to the icon representing the browser
   */
  public String getImagePath() {
    return imagePath;
  }
}
