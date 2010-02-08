/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.jstestdriver.eclipse.core;

import java.util.Observable;
import java.util.Observer;

import com.google.jstestdriver.BrowserCaptureEvent;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.SlaveBrowser;

/**
 * Root object to hold information about all the slave browsers. Is registered
 * as a listener so that it knows whenever a slave browser is captured.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class SlaveBrowserRootData extends Observable implements Observer {

  private final SlaveBrowserSet firefoxSlaves;
  private final SlaveBrowserSet chromeSlaves;
  private final SlaveBrowserSet ieSlaves;
  private final SlaveBrowserSet safariSlaves;
  private final SlaveBrowserSet operaSlaves;

  private SlaveBrowserRootData() {
    firefoxSlaves = new SlaveBrowserSet("Firefox", this);
    chromeSlaves = new SlaveBrowserSet("Chrome", this);
    ieSlaves = new SlaveBrowserSet("IE", this);
    safariSlaves = new SlaveBrowserSet("Safari", this);
    operaSlaves = new SlaveBrowserSet("Opera", this);
  }

  // TODO(shyamseshadri): ICKY. GET RID OF THIS ASAP. HACK FOR NOW TO GET THINGS
  // WORKING.
  private static SlaveBrowserRootData instance = null;

  public static SlaveBrowserRootData getInstance() {
    if (instance == null) {
      instance = new SlaveBrowserRootData();
    }
    return instance;
  }

  public SlaveBrowserSet getFirefoxSlaves() {
    return firefoxSlaves;
  }

  public SlaveBrowserSet getChromeSlaves() {
    return chromeSlaves;
  }

  public SlaveBrowserSet getIeSlaves() {
    return ieSlaves;
  }

  public SlaveBrowserSet getSafariSlaves() {
    return safariSlaves;
  }

  public SlaveBrowserSet getOperaSlaves() {
    return operaSlaves;
  }

  public int getNumberOfSlaves() {
    return firefoxSlaves.getNumberOfSlaves() + ieSlaves.getNumberOfSlaves()
        + chromeSlaves.getNumberOfSlaves() + safariSlaves.getNumberOfSlaves()
        + operaSlaves.getNumberOfSlaves();
  }

  public void update(Observable o, Object arg) {
    if (arg == null) {
      return;
    }
    SlaveBrowser slave = ((BrowserCaptureEvent) arg).getBrowser();
    BrowserInfo browserInfo = slave.getBrowserInfo();
    if (browserInfo == null) {
      return;
    }
    if (browserInfo.getName().contains("Firefox")) {
      firefoxSlaves.addSlaveBrowser(slave);
    } else if (browserInfo.getName().contains("Chrome")) {
      chromeSlaves.addSlaveBrowser(slave);
    } else if (browserInfo.getName().contains("Safari")) {
      safariSlaves.addSlaveBrowser(slave);
    } else if (browserInfo.getName().contains("Microsoft Internet Explorer")) {
      ieSlaves.addSlaveBrowser(slave);
    } else if (browserInfo.getName().contains("Opera")) {
      operaSlaves.addSlaveBrowser(slave);
    }
    setChanged();
    notifyObservers(this);
  }

  public void clear() {
    firefoxSlaves.clearSlaves();
    ieSlaves.clearSlaves();
    chromeSlaves.clearSlaves();
    safariSlaves.clearSlaves();
    operaSlaves.clearSlaves();
    setChanged();
    notifyObservers(this);
  }

  public boolean hasSlaves() {
    return firefoxSlaves.hasSlaves() || chromeSlaves.hasSlaves()
        || ieSlaves.hasSlaves() || safariSlaves.hasSlaves()
        || operaSlaves.hasSlaves();
  }

  @Override
  public String toString() {
    return "Browsers";
  }
}
