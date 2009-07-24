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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

/**
 * The main JS Test Driver View.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JsTestDriverView extends ViewPart {

  public static final String ID = "com.google.jstestdriver.eclipse.ui.views.JsTestDriverView";

  private final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
      .getHelpSystem();

  private ServerInfoPanel serverInfoPanel;

  public void createPartControl(Composite parent) {
    serverInfoPanel = new ServerInfoPanel(parent, SWT.NONE);

//    helpSystem.setHelp(viewer.getControl(),
//        "com.google.jstestdriver.eclipse.ui.viewer");
  }

  public void setServerStatus(String status) {
    serverInfoPanel.setServerStatus(status);
  }

  public void setFocus() {
    serverInfoPanel.getParent().setFocus();
    //viewer.getControl().setFocus();
  }

  public ServerInfoPanel getServerInfoPanel() {
    return serverInfoPanel;
  }
}
