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
package com.google.jstestdriver;

import com.google.inject.ImplementedBy;
import com.google.jstestdriver.model.JstdTestCase;

import java.util.Collection;
import java.util.List;


/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
@ImplementedBy(JsTestDriverClientImpl.class)
public interface JsTestDriverClient {

  /** Lists the current browsers captured by the server. */
  public Collection<BrowserInfo> listBrowsers();

  public void eval(String id, ResponseStream responseStream, String cmd,
      JstdTestCase testCase);

  public void runAllTests(String id, ResponseStream responseStream, boolean captureConsole,
      JstdTestCase testCase);

  public void reset(String id, ResponseStream responseStream, JstdTestCase testCase);

  public void runTests(String id, ResponseStream responseStream, List<String> tests,
      boolean captureConsole, JstdTestCase testCase);

  public void dryRun(String id, ResponseStream responseStream, JstdTestCase testCase);

  public void dryRunFor(String id, ResponseStream responseStream, List<String> expressions,
      JstdTestCase testCase);

  /** Asks the server for the next browser id. */
  public String getNextBrowserId();

  /** uploads the files to the server and requests the browser to load as well.*/
  public void uploadFiles(String browserId, JstdTestCase testCase);
}
