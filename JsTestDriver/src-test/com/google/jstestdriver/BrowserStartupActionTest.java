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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import com.google.common.collect.Sets;
import com.google.inject.internal.Lists;
import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.model.JstdTestCase;
import com.google.jstestdriver.util.NullStopWatch;

/**
 * @author corysmith@google.com (Cory Smith)
 * Integration test for the browser startup action.
 */
public class BrowserStartupActionTest extends TestCase {
  /**
   * 
   * 
   * @author Cory Smith (corbinrsmith@gmail.com)
   */
  private final class FakeJsTestDriverClient implements JsTestDriverClient {
    private final Collection<BrowserInfo> capturedBrowsers;
    private final String nextId;

    /**
     * @param capturedBrowsers
     * @param nextId
     */
    public FakeJsTestDriverClient(Collection<BrowserInfo> capturedBrowsers, String nextId) {
      this.capturedBrowsers = capturedBrowsers;
      this.nextId = nextId;
    }

    public Collection<BrowserInfo> listBrowsers() {
      return capturedBrowsers;
    }

    public void eval(String id, ResponseStream responseStream, String cmd, JstdTestCase testCase) {

    }

    public void runAllTests(String id, ResponseStream responseStream, boolean captureConsole,
        JstdTestCase testCase) {

    }

    public void reset(String id, ResponseStream responseStream, JstdTestCase testCase) {
    }

    public void runTests(String id, ResponseStream responseStream, List<String> tests,
        boolean captureConsole, JstdTestCase testCase) {
    }

    public void dryRun(String id, ResponseStream responseStream, JstdTestCase testCase) {
    }

    public void dryRunFor(String id, ResponseStream responseStream, List<String> expressions,
        JstdTestCase testCase) {
    }

    public String getNextBrowserId() {
      return nextId;
    }

    public void uploadFiles(String browserId, JstdTestCase testCase) {
    }
  }

  /**
   * 
   * 
   * @author Cory Smith (corbinrsmith@gmail.com)
   */
  private final class BrowserRunnerStub implements BrowserRunner {
    public String serverAddress;

    public void startBrowser(String serverAddress) {
      this.serverAddress = serverAddress;
    }

    public void stopBrowser() {
      // TODO Auto-generated method stub

    }

    public int getTimeout() {
      // TODO Auto-generated method stub
      return 10;
    }

    public int getNumStartupTries() {
      // TODO Auto-generated method stub
      return 1;
    }

    public long getHeartbeatTimeout() {
      // TODO Auto-generated method stub
      return 10;
    }

    public int getUploadSize() {
      // TODO Auto-generated method stub
      return 0;
    }
  }

  public void testRun() throws Exception {
    String nextId = "123";
    BrowserRunnerStub browserRunner = new BrowserRunnerStub();
    Set<BrowserRunner> browsers = Sets.<BrowserRunner>newHashSet(browserRunner);

    BrowserInfo browserInfo = new BrowserInfo();
    browserInfo.setId(Long.parseLong(nextId));
    browserInfo.setServerReceivedHeartbeat(true);
    Collection<BrowserInfo> capturedBrowsers = Lists.newArrayList(browserInfo);

    String serverAddress = "http://localhost";
    BrowserStartupAction action = new BrowserStartupAction(browsers,
        new NullStopWatch(),
        new FakeJsTestDriverClient(capturedBrowsers, nextId),
        serverAddress,
        Executors.newSingleThreadExecutor());
    
    action.run(null);
    assertEquals(serverAddress + "/capture/id/123/timeout/10/upload_size/0/", browserRunner.serverAddress);
  }
}
