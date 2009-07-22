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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

/**
 * @author shyamseshadri
 */
public class ServerStartStopViewActionDelegate implements IViewActionDelegate {

  private boolean isRunning = false;
  private final Server server;
  private final Icons icons;
  
  public ServerStartStopViewActionDelegate() {
    server = new Server();
    icons = new Icons();
  }

  public ServerStartStopViewActionDelegate(Server server, Icons icons) {
    this.server = server;
    this.icons = icons;
  }

  public void init(IViewPart view) {
  }

  public void run(IAction action) {
    isRunning = !isRunning;
    if (isRunning) {
      server.start();
      setStopServerState(action);
    } else {
      server.stop();
      setStartServerState(action);
    }
  }
  
  private void setStopServerState(IAction action) {
    action.setText("Stop Server");
    action.setToolTipText("Stop Server");
    action.setImageDescriptor(icons.stopServerIcon());
  }

  private void setStartServerState(IAction action) {
    action.setText("Start Server");
    action.setToolTipText("Start Server");
    action.setImageDescriptor(icons.startServerIcon());
  }

  public void selectionChanged(IAction action, ISelection selection) {

  }

}
