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

import org.eclipse.jface.action.Action;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

public final class ServerStartStopAction extends Action {
  private final Icons icons;
  private boolean isRunning = false;
  private final Server server;

  public ServerStartStopAction(Server server, Icons icons) {
    this.server = server;
    this.icons = icons;
    setStartServerState();
  }

  @Override
  public void run() {
    isRunning = !isRunning;
    if (isRunning) {
      setStopServerState();
      server.start();
    } else {
      setStartServerState();
      server.stop();
    }
  }

  private void setStopServerState() {
    setText("Stop Server");
    setImageDescriptor(icons.stopServerIcon());
  }

  private void setStartServerState() {
    setText("Start Server");
    setImageDescriptor(icons.startServerIcon());
  }
}
