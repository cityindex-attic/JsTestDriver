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
package com.google.jstestdriver.eclipse.ui.views;

import static java.lang.String.format;

import java.io.IOException;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.ui.Activator;
import com.google.jstestdriver.eclipse.ui.WorkbenchPreferencePage;

/**
 * Given the path to the browser, knows how to launch based on which OS is
 * currently running.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class BrowserLaunchCapableSelectionListener implements SelectionListener {

  private final String pathToBrowser;

  public BrowserLaunchCapableSelectionListener(String pathToBrowser) {
    this.pathToBrowser = pathToBrowser;
  }

  public void widgetDefaultSelected(SelectionEvent e) {
  }

  public void widgetSelected(SelectionEvent e) {
    try {
      if (pathToBrowser == null || "".equals(pathToBrowser.trim())) {
        return;
      }
      int port = Activator.getDefault().getPreferenceStore().getInt(
          WorkbenchPreferencePage.PREFERRED_SERVER_PORT);
      String serverUrl = format(Server.SERVER_CAPTURE_URL, port);
      String os = System.getProperty("os.name");
      if (os.toLowerCase().contains("mac os")) {
        String[] cmd = new String[] { "open", "-a", pathToBrowser, serverUrl };
        Runtime.getRuntime().exec(cmd);
      } else {
        String[] cmd = new String[] { pathToBrowser, serverUrl };
        Runtime.getRuntime().exec(cmd);
      }
    } catch (IOException ioe) {
    }
  }

}
