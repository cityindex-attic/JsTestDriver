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
package com.google.eclipse.javascript.jstestdriver.ui.view;

import com.google.eclipse.javascript.jstestdriver.core.model.SlaveBrowserRootData;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * Panel which displays info about the server, including status, capture url and
 * browsers captured.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class ServerInfoPanel extends Composite implements Observer {

  private static final Color NOT_RUNNING = new Color(Display.getCurrent(), 255, 102, 102);
  private static final Color NO_BROWSERS = new Color(Display.getCurrent(), 255, 255, 102);
  private static final Color READY = new Color(Display.getCurrent(), 102, 204, 102);
  private static final String SERVER_DOWN = "NOT RUNNING";

  private final Text serverUrlText;
  private final BrowserButtonPanel browserButtonPanel;

  public ServerInfoPanel(Composite parent, int style) {
    super(parent, style);
    setLayout(new GridLayout(1, true));
    GridData layoutData = new GridData();
    layoutData.grabExcessHorizontalSpace = true;
    layoutData.horizontalAlignment = SWT.FILL;
    setLayoutData(layoutData);

    GridData textGridData = new GridData();
    textGridData.grabExcessHorizontalSpace = true;
    textGridData.horizontalAlignment = SWT.FILL;
    serverUrlText = new Text(this, SWT.CENTER);
    serverUrlText.setText(SERVER_DOWN);
    serverUrlText.setBackground(NOT_RUNNING);
    serverUrlText.setLayoutData(textGridData);
    serverUrlText.setEditable(false);
    serverUrlText.setOrientation(SWT.HORIZONTAL);

    browserButtonPanel = new BrowserButtonPanel(this, SWT.NONE);
  }

  /**
   * Notified when a browser has been captured. Updates the Server url text background color
   * to green to signify that we are now ready to run tests. The browser capture url still remains
   * the same and is not to be changed.
   */
  @Override
  public void update(Observable o, final Object arg) {
    final SlaveBrowserRootData data = (SlaveBrowserRootData) arg;
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        if (data.hasSlaves()) {
          serverUrlText.setBackground(READY);
        }
      }
    });
  }

  /**
   * Gets the browser button panel.
   * @return the browser button panel
   */
  public BrowserButtonPanel getBrowserButtonPanel() {
    return browserButtonPanel;
  }

  /**
   * Sets the state of the server info panel to "Server Started". This means that the color is
   * set to Yellow while waiting for browsers to capture, and the text displayed is the Server
   * capture url that can be copy pasted into a browser.
   * @param serverUrl the url to be used to capture a browser
   */
  public void setServerStartedAndWaitingForBrowsers(String serverUrl) {
    serverUrlText.setText(serverUrl);
    serverUrlText.setBackground(NO_BROWSERS);
  }

  /**
   * Sets the state of the server info panel to stopped.
   */
  public void setServerStopped() {
    serverUrlText.setText(SERVER_DOWN);
    serverUrlText.setBackground(NOT_RUNNING);
  }
}