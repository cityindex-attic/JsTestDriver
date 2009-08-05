/*
 * Copyright 2008 Google Inc.
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
package com.google.jstestdriver.eclipse.core;

import java.util.ArrayList;
import java.util.List;

import com.google.jstestdriver.SlaveBrowser;

/**
 * Information about slave browsers for a particular type of browser, like
 * Chrome, IE, etc. Knows about each slave browser, the name and what image to
 * display in the tree.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class SlaveBrowserSet {
  private final String browserName;
  private final List<SlaveBrowser> slaves;
  private final SlaveBrowserRootData parent;

  public SlaveBrowserSet(String browserName, SlaveBrowserRootData parent) {
    this.browserName = browserName;
    this.parent = parent;
    this.slaves = new ArrayList<SlaveBrowser>();
  }

  public SlaveBrowserRootData getParent() {
    return parent;
  }

  public String getBrowserName() {
    return browserName;
  }

  public List<SlaveBrowser> getSlaves() {
    return slaves;
  }

  public void addSlaveBrowser(SlaveBrowser browser) {
    slaves.add(browser);
  }

  public void clearSlaves() {
    slaves.clear();
  }

  public boolean hasSlaves() {
    return slaves.size() > 0;
  }

  public String toString() {
    return browserName + " - (" + slaves.size() + ")";
  }

  public String getImagePath() {
    return "icons/" + browserName + ".png";
  }

  public List<String> getBrowserIds() {
    List<String> ids = new ArrayList<String>();
    for (SlaveBrowser browser : slaves) {
      ids.add(browser.getBrowserInfo().getId().toString());
    }
    return ids;
  }
}
