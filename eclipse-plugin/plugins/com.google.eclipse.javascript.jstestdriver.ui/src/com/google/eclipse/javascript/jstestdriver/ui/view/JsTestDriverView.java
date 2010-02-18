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

import com.google.eclipse.javascript.jstestdriver.core.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * The main JS Test Driver View.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class JsTestDriverView extends ViewPart {

  /**
   * ID of the JsTestDriver view extension
   */
  public static final String ID =
      "com.google.eclipse.javascript.jstestdriver.ui.view.JsTestDriverView";

  private ServerInfoPanel serverInfoPanel;
  private TestResultsPanel testResultsPanel;
  private final ServerController serverController = new ServerController();

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new GridLayout(1, false));
    serverInfoPanel = new ServerInfoPanel(parent, SWT.NONE);
    testResultsPanel = new TestResultsPanel(parent, SWT.NONE);
    serverController.connectObservers(serverInfoPanel);
  }

  @Override
  public void setFocus() {
    serverInfoPanel.getParent().setFocus();
  }

  /**
   * Gets the Server info panel created as part of the JsTestDriverView.
   */
  public ServerInfoPanel getServerInfoPanel() {
    return serverInfoPanel;
  }

  /**
   * Gets the Test results panel created as part of the JsTestDriverView.
   */
  public TestResultsPanel getTestResultsPanel() {
    return testResultsPanel;
  }

  @Override
  public void dispose() {
    serverController.disconnectObservers(serverInfoPanel);
    Server server = Server.getInstance();
    if (server != null && server.isStarted()) {
      server.stop();
    }
    serverInfoPanel.dispose();
    testResultsPanel.dispose();
  }
}
