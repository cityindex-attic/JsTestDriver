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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.jstestdriver.ProcessFactory;

/**
 * Runs a browser from the command line.
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class CommandLineBrowserRunner implements BrowserRunner {
  private static final Logger logger =
      LoggerFactory.getLogger(CommandLineBrowserRunner.class);
  private final String browserPath;
  private final ProcessFactory processFactory;
  private Process process;

  public CommandLineBrowserRunner(String browserPath,
                                  ProcessFactory processFactory) {
    this.browserPath = browserPath;
    this.processFactory = processFactory;
  }

  public void startBrowser(String serverAddress) {
    try {
      process = processFactory.start(browserPath, serverAddress);
    } catch (IOException e) {
      logger.error("Could not start: {} because {}", browserPath, e.toString());
      throw new RuntimeException(e);
    }
  }

  public void stopBrowser() {
    try {
      process.destroy();
      if (process.exitValue() != 0) {
        logger.warn("Unexpected shutdown " + process + " " + process.exitValue());
      }
    } catch (IllegalThreadStateException e) {
      logger.warn("Process refused to exit" + process);
    }
  }

  public int getTimeout() {
    return 30;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((browserPath == null) ? 0 : browserPath.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CommandLineBrowserRunner)) {
      return false;
    }
    CommandLineBrowserRunner other = (CommandLineBrowserRunner) obj;
    if (browserPath == null) {
      if (other.browserPath != null) {
        return false;
      }
    } else if (!browserPath.equals(other.browserPath)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "CommandLineBrowserRunner [browserPath=" + browserPath
        + ", process=" + process + ", processFactory=" + processFactory + "]";
  }
}
