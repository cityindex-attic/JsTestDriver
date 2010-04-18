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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.google.common.collect.Sets;
import com.google.jstestdriver.browser.BrowserRunner;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class BrowserStartupActionTest extends TestCase {

  public void testProcessRun() throws Exception {
    CountDownLatchFake latch = new CountDownLatchFake(1, false, true);
    final FakeBrowser browser = new FakeBrowser(300);
    
    final String serverAddress = "http://foo:8080";
    
    final BrowserStartupAction action =
        new BrowserStartupAction(Sets.<BrowserRunner>newHashSet(browser),
            serverAddress, latch);

    action.run();

    assertTrue(browser.started);
    assertEquals(serverAddress + "/capture", browser.serverAddress);
    assertEquals(browser.getTimeout(), latch.getTimeoutPassed());
  }
  
  public void testProcessRunLongestTimeout() throws Exception {
    CountDownLatchFake latch = new CountDownLatchFake(1, false, true);
    final FakeBrowser browserOne = new FakeBrowser(300);
    final FakeBrowser browserTwo = new FakeBrowser(900);
    
    final String serverAddress = "http://foo:8080";
    
    final BrowserStartupAction action =
      new BrowserStartupAction(Sets.<BrowserRunner>newHashSet(browserOne,
                                                              browserTwo),
          serverAddress, latch);
    
    action.run();
    
    assertTrue(browserOne.started);
    assertTrue(browserTwo.started);
    assertEquals(serverAddress + "/capture", browserOne.serverAddress);
    assertEquals(serverAddress + "/capture", browserTwo.serverAddress);
    assertEquals(browserTwo.getTimeout(), latch.getTimeoutPassed());
  }
  
  public void testProcessRunLongestTimeoutReverseOrder() throws Exception {
    CountDownLatchFake latch = new CountDownLatchFake(1, false, true);
    final FakeBrowser browserOne = new FakeBrowser(300);
    final FakeBrowser browserTwo = new FakeBrowser(900);
    
    final String serverAddress = "http://foo:8080";
    
    final BrowserStartupAction action =
      new BrowserStartupAction(Sets.<BrowserRunner>newHashSet(browserTwo,
                                                              browserOne),
          serverAddress, latch);
    
    action.run();
    
    assertTrue(browserOne.started);
    assertTrue(browserTwo.started);
    assertEquals(serverAddress + "/capture", browserOne.serverAddress);
    assertEquals(serverAddress + "/capture", browserTwo.serverAddress);
    assertEquals(browserTwo.getTimeout(), latch.getTimeoutPassed());
  }

  public void testProcessStartFailure() throws Exception {
    CountDownLatchFake latch = new CountDownLatchFake(1, false, true);
    final FakeBrowser browser = new FakeBrowser(300);
    final BrowserRunner errorBrowser = new BrowserRunner() {

      public void stopBrowser() {
      }

      public void startBrowser(String serverAddress) {
      }

      public int getTimeout() {
        return 300;
      }
    };
    
    final String serverAddress = "http://foo:8080";
    final BrowserStartupAction action = new BrowserStartupAction(
      Sets.<BrowserRunner>newHashSet(browser, errorBrowser, browser),
      serverAddress,
      latch);
    
    action.run();

  }

  private final class FakeBrowser implements BrowserRunner {
    public String serverAddress;
    public boolean started;
    private final int timeout;

    public FakeBrowser(int timeout) {
      this.timeout = timeout;
    }

    public void stopBrowser() {
      started = false;
    }

    public void startBrowser(String serverAddress) {
      this.serverAddress = serverAddress;
      started = true;
    }

    public int getTimeout() {
      return timeout;
    }
  }


  static class CountDownLatchFake extends CountDownLatch{
    private final boolean awaitResponse;
    private int count;
    private long timeoutPassed;

    public CountDownLatchFake(int count, boolean wait, boolean awaitResponse) {
      super(0);
      this.count = count;
      this.awaitResponse = awaitResponse;
    }

    @Override
    public boolean await(long timeoutPassed, TimeUnit unit) {
      this.timeoutPassed = timeoutPassed;
      return awaitResponse;
    }

    public long getTimeoutPassed() {
      return timeoutPassed;
    }

    @Override
    public long getCount() {
      return count;
    }

    @Override
    public void countDown() {
      count--;
    }
  }
}
