// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.eclipse.javascript.jstestdriver.ui.view;

import com.google.eclipse.javascript.jstestdriver.core.Server;
import com.google.eclipse.javascript.jstestdriver.core.model.SlaveBrowserRootData;
import com.google.eclipse.javascript.jstestdriver.ui.Activator;
import com.google.eclipse.javascript.jstestdriver.ui.prefs.WorkbenchPreferencePage;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 *
 */
public class ServerController {

  public ServerController() {
    int port = Activator.getDefault().getPreferenceStore().getInt(
        WorkbenchPreferencePage.PREFERRED_SERVER_PORT);
    // make sure the server is started.
    Server.getInstance(port);
  }

  public void connectObservers(ServerInfoPanel view) {
    SlaveBrowserRootData data = SlaveBrowserRootData.getInstance();
    Server.getInstance().getCapturedBrowsers().addObserver(data);
    data.addObserver(view);
    data.addObserver(view.getBrowserButtonPanel());

  }

  public void disconnectObservers(ServerInfoPanel view) {
    SlaveBrowserRootData data = SlaveBrowserRootData.getInstance();
    if (Server.getInstance() != null) {
      Server.getInstance().getCapturedBrowsers().deleteObserver(data);
    }
    data.deleteObserver(view);
    data.deleteObserver(view.getBrowserButtonPanel());
  }
}
