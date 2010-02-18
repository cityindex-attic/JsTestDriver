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
package com.google.eclipse.javascript.jstestdriver.ui.view.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.google.eclipse.javascript.jstestdriver.core.Server;

/**
 * Given the path to the browser, knows how to launch based on which OS is
 * currently running.
 * 
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class BrowserLaunchCapableSelectionListener implements SelectionListener {
  private static final Logger logger =
    Logger.getLogger(BrowserLaunchCapableSelectionListener.class.getCanonicalName());

  private final IPreferenceStore preferenceStore;
  private final String browserName;

  public BrowserLaunchCapableSelectionListener(
      IPreferenceStore preferenceStore, String browserName) {
    this.preferenceStore = preferenceStore;
    this.browserName = browserName;
  }

  public void widgetDefaultSelected(SelectionEvent e) {
  }

  public void widgetSelected(SelectionEvent e) {
    try {
      String pathToBrowser = preferenceStore.getString(browserName);
      if (pathToBrowser == null || "".equals(pathToBrowser.trim())) {
        return;
      }
      String serverUrl = Server.getInstance().getCaptureUrl();
      String os = System.getProperty("os.name");
      if (os.toLowerCase().contains("mac os")) {
        String[] cmd = new String[] { "open", "-a", pathToBrowser, serverUrl };
        Runtime.getRuntime().exec(cmd);
      } else {
        String[] cmd = new String[] { pathToBrowser, serverUrl };
        Runtime.getRuntime().exec(cmd);
      }
    } catch (IOException ioe) {
      logger.log(Level.SEVERE, "", ioe);
    }
  }

}
