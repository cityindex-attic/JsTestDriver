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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.ViewPart;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

public class JsTestDriverView extends ViewPart {

  public static final String ID = "com.google.jstestdriver.eclipse.ui.views.JsTestDriverView";

  private final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench()
      .getHelpSystem();

  private TreeViewer viewer;
  private Action startStopServer;

  private Icons icons = new Icons();

  public void createPartControl(Composite parent) {
    viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.setSorter(new NameSorter());
    viewer.setInput(getViewSite());

    helpSystem.setHelp(viewer.getControl(),
        "com.google.jstestdriver.eclipse.ui.viewer");

    createActions();
    contributeToActionBars();
  }

  private void createActions() {
    Server server = new Server();
    startStopServer = new ServerStartStopAction(server, icons);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(startStopServer);
  }

  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(startStopServer);
  }

  public void setFocus() {
    viewer.getControl().setFocus();
  }
}
