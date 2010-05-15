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

import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.CapturedBrowsers;
import com.google.jstestdriver.MockTime;
import com.google.jstestdriver.SlaveBrowser;

import junit.framework.TestCase;

/**
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class BrowserReaperTest extends TestCase {

  public void testReapDeadBrowser() throws Exception {
    final CapturedBrowsers browsers = new CapturedBrowsers();
    final MockTime time = new MockTime(1);
    final SlaveBrowser browserOne =
        new SlaveBrowser(time, "1", new BrowserInfo(), 20);
    browsers.addSlave(browserOne);
    final SlaveBrowser browserTwo =
        new SlaveBrowser(time, "2", new BrowserInfo(), 20);
    browsers.addSlave(browserTwo);
    final BrowserReaper browserReaper = new BrowserReaper(browsers);
    time.add(8000);
    browserOne.heartBeat();
    assertFalse(browserTwo.isAlive());
    browserReaper.run();
    assertEquals(1, browsers.getBrowsers().size());
  }

  public void testDontReapDeadBrowserWithCommandRunning() throws Exception {
    final CapturedBrowsers browsers = new CapturedBrowsers();
    final MockTime time = new MockTime(1);
    final SlaveBrowser browserOne =
      new SlaveBrowser(time, "1", new BrowserInfo(), 20);
    browsers.addSlave(browserOne);
    final SlaveBrowser browserTwo =
      new SlaveBrowser(time, "2", new BrowserInfo(), 20);
    browsers.addSlave(browserTwo);
    final BrowserReaper browserReaper = new BrowserReaper(browsers);
    time.add(40);
    browserOne.heartBeat();
    browserTwo.createCommand("foo");
    browserTwo.dequeueCommand();
    browserReaper.run();
    assertEquals(2, browsers.getBrowsers().size());
  }
}
