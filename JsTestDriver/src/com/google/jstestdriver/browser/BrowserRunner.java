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
package com.google.jstestdriver.browser;

/**
 * Defines an interface for starting and stopping a browser.
 * @author corbinsmith@gmail.com (Cory Smith)
 */
public interface BrowserRunner {
  /** Starts a browser and points it towards the server for capturing. */
  public void startBrowser(String serverAddress);

  /** Shuts down the browser. */
  public void stopBrowser();
  
  /** The seconds needed for this browser to start up. */
  public int getTimeout();

  /**
   * Number of times to try starting up the browser before giving up due to the
   * browser not responding.
   */
  public int getNumStartupTries();

  /**
   * The number of ms before the browser is dead.
   */
  public long getHeartbeatTimeout();

  /**
   * The number of files to upload at one time.
   */
  public int getUploadSize();
}
