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

import com.google.eclipse.javascript.jstestdriver.core.model.Browser;
import com.google.eclipse.javascript.jstestdriver.core.model.SlaveBrowserRootData;
import com.google.eclipse.javascript.jstestdriver.ui.Activator;
import com.google.eclipse.javascript.jstestdriver.ui.Icons;
import com.google.eclipse.javascript.jstestdriver.ui.prefs.WorkbenchPreferencePage;
import com.google.eclipse.javascript.jstestdriver.ui.view.actions.BrowserLaunchCapableSelectionListener;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import java.util.Observable;
import java.util.Observer;

/**
 * Shows a list of browser buttons, highlighted if captured.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class BrowserButtonPanel extends Composite implements Observer {

  private final Button safariIcon;
  private final Button chromeIcon;
  private final Button ieIcon;
  private final Button ffIcon;
  private final Button operaIcon;
  private final Icons icons;

  public BrowserButtonPanel(Composite parent, int style) {
    super(parent, style);
    icons = Activator.getDefault().getIcons();
    IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
    GridLayout gridLayout = new GridLayout(5, true);
    gridLayout.horizontalSpacing = 0;
    gridLayout.makeColumnsEqualWidth = true;
    gridLayout.marginWidth = 0;
    setLayout(gridLayout);
    GridData browserLayoutData = new GridData();
    browserLayoutData.grabExcessHorizontalSpace = true;
    browserLayoutData.horizontalAlignment = SWT.FILL;
    setLayoutData(browserLayoutData);

    GridData buttonGridData = new GridData();
    buttonGridData.horizontalAlignment = SWT.CENTER;
    buttonGridData.grabExcessHorizontalSpace = true;
    buttonGridData.minimumWidth = 42;
    ffIcon = new Button(this, SWT.FLAT);
    ffIcon.setImage(icons.getFirefoxDisabledIcon());
    ffIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore, WorkbenchPreferencePage.FIREFOX_PATH));
    ffIcon.setLayoutData(buttonGridData);

    chromeIcon = new Button(this, SWT.FLAT);
    chromeIcon.setImage(icons.getChromeDisabledIcon());
    chromeIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore, WorkbenchPreferencePage.CHROME_PATH));
    chromeIcon.setLayoutData(buttonGridData);

    safariIcon = new Button(this, SWT.FLAT);
    safariIcon.setImage(icons.getSafariDisabledIcon());
    safariIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore, WorkbenchPreferencePage.SAFARI_PATH));
    safariIcon.setLayoutData(buttonGridData);

    ieIcon = new Button(this, SWT.FLAT);
    ieIcon.setImage(icons.getIEDisabledIcon());
    ieIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore, WorkbenchPreferencePage.IE_PATH));
    ieIcon.setLayoutData(buttonGridData);

    operaIcon = new Button(this, SWT.FLAT);
    operaIcon.setImage(icons.getOperaDisabledIcon());
    operaIcon.addSelectionListener(new BrowserLaunchCapableSelectionListener(
        preferenceStore, WorkbenchPreferencePage.OPERA_PATH));
    operaIcon.setLayoutData(buttonGridData);
  }

  /**
   * Gets notified when the {@link SlaveBrowserRootData} changes. It is passed a reference of
   * the same, and is expected to update the images based on whether browsers are captured or not.
   */
  @Override
  public void update(Observable o, Object arg) {
    final SlaveBrowserRootData data = (SlaveBrowserRootData) arg;
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        if (!ffIcon.isDisposed()) {
          ffIcon.setImage(getImage(Browser.FIREFOX.getImagePath(),
              data.getSlaves(Browser.FIREFOX).isEmpty()));
          ffIcon.update();
          ffIcon.redraw();
        }
        if (!chromeIcon.isDisposed()) {
          chromeIcon.setImage(getImage(Browser.CHROME.getImagePath(),
              data.getSlaves(Browser.CHROME).isEmpty()));
          chromeIcon.update();
          chromeIcon.redraw();
        }
        if (!ieIcon.isDisposed()) {
          ieIcon.setImage(getImage(Browser.IE.getImagePath(),
              data.getSlaves(Browser.IE).isEmpty()));
        }
        if (!safariIcon.isDisposed()) {
          safariIcon.setImage(getImage(Browser.SAFARI.getImagePath(),
              data.getSlaves(Browser.SAFARI).isEmpty()));
        }
        if (!operaIcon.isDisposed()) {
          operaIcon.setImage(getImage(Browser.OPERA.getImagePath(),
              data.getSlaves(Browser.OPERA).isEmpty()));
        }
      }
    });
  }

  private Image getImage(String path, boolean empty) {
    return empty ? icons.getGrayedIcon(path) : icons.getImage(path);
  }
}