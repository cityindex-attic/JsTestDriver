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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.model.RunData;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class BrowserShutdownAction implements Action {
  private static final Logger logger =
      LoggerFactory.getLogger(BrowserShutdownAction.class);

  private final Set<BrowserRunner> browsers;

  public BrowserShutdownAction(Set<BrowserRunner> browsers) {
    this.browsers = browsers;
  }

  public RunData run(RunData runData) {
    for (BrowserRunner browser : browsers) {
      logger.debug("Stopping {}", browser);
      browser.stopBrowser();
    }
    return runData;
  }
}
