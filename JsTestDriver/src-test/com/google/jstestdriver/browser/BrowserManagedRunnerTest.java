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

package com.google.jstestdriver.browser;

import com.google.common.collect.Lists;
import com.google.jstestdriver.BrowserActionRunner;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.FakeJsTestDriverClient;
import com.google.jstestdriver.ResponseStream;
import com.google.jstestdriver.util.NullStopWatch;

import junit.framework.TestCase;

/**
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class BrowserManagedRunnerTest extends TestCase {

  public void testCall() throws Exception {
    String browserId = "5";
    String serverAddress = "http://foo";
    
    final BrowserInfo browserInfo = new BrowserInfo();
    browserInfo.setId(Integer.parseInt(browserId));
    final FakeJsTestDriverClient client = new FakeJsTestDriverClient(Lists.newArrayList(browserInfo));
    final FakeBrowserRunner runner = new FakeBrowserRunner();
    final FakeBrowserActionRunner browserActionRunner = new FakeBrowserActionRunner();
    final BrowserManagedRunner browserRunner = new BrowserManagedRunner(runner, browserId, serverAddress, client, browserActionRunner, new NullStopWatch());
    browserRunner.call();
  }
  
  private static final class FakeBrowserActionRunner extends BrowserActionRunner {
    public FakeBrowserActionRunner() {
      super(null, null, null, new NullStopWatch());
    }
    
    @Override
    public ResponseStream call() {
      return null;
    }
  }
  
  private static final class FakeBrowserRunner implements BrowserRunner {
    public void stopBrowser() {
      // TODO Auto-generated method stub
      
    }

    public void startBrowser(String serverAddress) {
      // TODO Auto-generated method stub
      
    }

    public int getTimeout() {
      // TODO Auto-generated method stub
      return 0;
    }
  }
}
