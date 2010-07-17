/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.action;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.browser.BrowserSessionManager;
import com.google.jstestdriver.model.RunData;

import java.util.List;

/**
 * Uploads the test files to the server. Used by the standalone runner.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class UploadAction implements Action {

  private final JsTestDriverClient client;
  private final BrowserSessionManager sessionManager;

  @Inject
  public UploadAction(JsTestDriverClient client, BrowserSessionManager sessionManager) {
    this.client = client;
    this.sessionManager = sessionManager;
  }
  
  public RunData run(RunData runData) {
    for (BrowserInfo browser : client.listBrowsers()) {
      final String browserId = browser.getId().toString();
      final String sessionId = sessionManager.startSession(browserId);
      client.uploadFiles(browserId, runData);
      sessionManager.stopSession(sessionId, browserId);
    }
    return runData;
  }
}
