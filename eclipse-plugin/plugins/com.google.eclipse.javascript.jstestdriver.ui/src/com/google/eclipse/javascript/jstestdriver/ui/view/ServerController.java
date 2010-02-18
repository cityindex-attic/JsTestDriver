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
import com.google.eclipse.javascript.jstestdriver.core.model.SlaveBrowserRootData;
import com.google.eclipse.javascript.jstestdriver.ui.Activator;
import com.google.eclipse.javascript.jstestdriver.ui.prefs.WorkbenchPreferencePage;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
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
