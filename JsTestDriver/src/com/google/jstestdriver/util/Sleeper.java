// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.util;

/**
 * Externalizes the Thread.sleep commands.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class Sleeper {
  /**
   * Stops execution in the current thread for the definied milliseconds.
   * @throws InterruptedException 
   */
  public void sleep(long milliseconds) throws InterruptedException {
    Thread.sleep(milliseconds);
  }
}
