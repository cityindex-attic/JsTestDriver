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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.jstestdriver.eclipse.core.SlaveBrowserRootData;
import com.google.jstestdriver.eclipse.core.SlaveBrowserSet;
import com.google.jstestdriver.eclipse.ui.Activator;
import com.google.jstestdriver.eclipse.ui.WorkbenchPreferencePage;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class BrowserButtonPanel extends Composite implements Observer {

  private Button safariIcon;
  private Button chromeIcon;
  private Button ieIcon;
  private Button ffIcon;
  private Button operaIcon;
  private Icons icons;

  public BrowserButtonPanel(Composite parent, int style) {
    super(parent, style);
    icons = new Icons();
    IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
    setLayout(new GridLayout(5, true));

    GridData ffButtonGridData = new GridData();
    ffButtonGridData.horizontalAlignment = SWT.CENTER;
    ffIcon = new Button(this, SWT.FLAT);
    ffIcon.setImage(icons.getFirefoxDisabledIcon());
    ffIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore.getString(WorkbenchPreferencePage.FIREFOX_PATH)));
    ffIcon.setLayoutData(ffButtonGridData);
    
    GridData chromeButtonGridData = new GridData();
    chromeButtonGridData.horizontalAlignment = SWT.CENTER;
    chromeIcon = new Button(this, SWT.FLAT);
    chromeIcon.setImage(icons.getChromeDisabledIcon());
    chromeIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore.getString(WorkbenchPreferencePage.CHROME_PATH)));
    chromeIcon.setLayoutData(chromeButtonGridData);
    
    GridData safariButtonGridData = new GridData();
    safariButtonGridData.horizontalAlignment = SWT.CENTER;
    safariIcon = new Button(this, SWT.FLAT);
    safariIcon.setImage(icons.getSafariDisabledIcon());
    safariIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore.getString(WorkbenchPreferencePage.SAFARI_PATH)));
    safariIcon.setLayoutData(safariButtonGridData);
    
    GridData ieButtonGridData = new GridData();
    ieButtonGridData.horizontalAlignment = SWT.CENTER;
    ieIcon = new Button(this, SWT.FLAT);
    ieIcon.setImage(icons.getIEDisabledIcon());
    ieIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore.getString(WorkbenchPreferencePage.IE_PATH)));
    ieIcon.setLayoutData(ieButtonGridData);
    
    GridData operaButtonGridData = new GridData();
    operaButtonGridData.horizontalAlignment = SWT.CENTER;
    operaIcon = new Button(this, SWT.FLAT);
    operaIcon.setImage(icons.getOperaDisabledIcon());
    operaIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore.getString(WorkbenchPreferencePage.OPERA_PATH)));
    operaIcon.setLayoutData(operaButtonGridData);
  }

  public void update(Observable o, Object arg) {
    final SlaveBrowserRootData data = (SlaveBrowserRootData) arg;
    new Thread(new Runnable() {
      public void run() {
        Display.getDefault().asyncExec(new Runnable() {
          public void run() {
            ffIcon.setImage(getImage(data.getFirefoxSlaves()));
            chromeIcon.setImage(getImage(data.getChromeSlaves()));
            ieIcon.setImage(getImage(data.getIeSlaves()));
            safariIcon.setImage(getImage(data.getSafariSlaves()));
            operaIcon.setImage(getImage(data.getOperaSlaves()));
          }
        });
      }
    }).start();
  }

  public Image getImage(SlaveBrowserSet slave) {
    Image colored = icons.getImage(slave.getImagePath());
    if (slave.hasSlaves()) {
      return colored;
    } else {
      return new Image(Display.getCurrent(), colored, SWT.IMAGE_GRAY);
    }
  }

}
