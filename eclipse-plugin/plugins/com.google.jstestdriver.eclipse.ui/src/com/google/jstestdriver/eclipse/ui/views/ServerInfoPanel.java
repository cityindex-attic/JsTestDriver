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

import java.util.Observable;
import java.util.Observer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.jstestdriver.eclipse.core.SlaveBrowserRootData;

/**
 * Panel which displays info about the server, incuding status, capture url and browsers captured.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class ServerInfoPanel extends Composite implements Observer {

  private Text serverUrlText;
  private TreeViewer browserTree;

  public ServerInfoPanel(Composite parent, int style) {
    super(parent, style);
    // TODO(shyamseshadri): YUCK, work in the constructor. Figure out best way around this.
//    Group serverPropertiesControl = new Group(parent, SWT.NONE);
//    serverPropertiesControl.setLayout(new GridLayout(2, false));
//    serverPropertiesControl.setText("Server:");
//    serverPropertiesControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // TODO(shyamseshadri): UI Looks weird, something invisible seems to be getting on it.
    parent.setLayout(new GridLayout(3, false));
    Label serverLabel = new Label(parent, SWT.NONE);
    serverLabel.setText("Server :");
    serverUrlText = new Text(parent, SWT.NONE);
    serverUrlText.setEditable(false);
    serverUrlText.setText("Down");
    serverUrlText.pack();
    
    browserTree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    BrowserInfoContentProvider provider = new BrowserInfoContentProvider();
    browserTree.setContentProvider(provider);
    browserTree.setLabelProvider(new BrowserInfoLabelProvider());
    browserTree.setSorter(new NameSorter());
    browserTree.setInput(SlaveBrowserRootData.getInstance());
    GridData treeLayoutData = new GridData();
    treeLayoutData.grabExcessHorizontalSpace = true;
    treeLayoutData.widthHint = 300;
    treeLayoutData.heightHint = 300;
    treeLayoutData.horizontalSpan = 3;
    browserTree.getTree().setLayoutData(treeLayoutData);
  }

  public void update(Observable o, Object arg) {
    new Thread(new Runnable() {
      public void run() {
        Display.getDefault().asyncExec(new Runnable() {
          public void run() {
            browserTree.refresh();
          }
        });
      }
    }).start();
  }

  public void setServerStatus(String status) {
    serverUrlText.setText(status);
    serverUrlText.pack();
  }

}
