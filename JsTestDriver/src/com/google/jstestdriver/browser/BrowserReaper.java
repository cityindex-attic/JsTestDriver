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
package com.google.jstestdriver.browser;

import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.SlaveBrowser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * Removes dead browsers from the CapturedBrowsers,
 * if there is no command running.
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class BrowserReaper extends TimerTask {
  private static final Logger logger = LoggerFactory.getLogger(BrowserReaper.class);
  private final CapturedBrowsers capturedBrowsers;

  public BrowserReaper(CapturedBrowsers capturedBrowsers) {
    this.capturedBrowsers = capturedBrowsers;
  }

  @Override
  public void run() {
    for (SlaveBrowser browser : capturedBrowsers.getSlaveBrowsers()) {
      if (!browser.isAlive() && !browser.isCommandRunning()) {
        logger.debug("Reaping dead {}.", browser);
        capturedBrowsers.removeSlave(browser.getId());
      }
    }
  }
}
