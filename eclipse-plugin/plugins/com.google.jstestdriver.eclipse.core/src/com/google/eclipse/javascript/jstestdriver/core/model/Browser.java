// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.core.model;

/**
 * The browsers that JsTestDriver works with.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
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
