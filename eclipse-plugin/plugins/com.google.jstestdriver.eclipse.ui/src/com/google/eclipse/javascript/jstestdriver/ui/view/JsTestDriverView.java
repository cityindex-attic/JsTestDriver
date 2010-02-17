// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.ui.view;

import com.google.eclipse.javascript.jstestdriver.core.Server;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * The main JS Test Driver View.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JsTestDriverView extends ViewPart {

  /**
   * ID of the JsTestDriver view extension
   */
  public static final String ID =
      "com.google.eclipse.javascript.jstestdriver.ui.views.jsTestDriverView";

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
