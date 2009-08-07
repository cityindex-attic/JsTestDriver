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

import junit.framework.TestCase;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.google.jstestdriver.eclipse.core.Server;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

public class ServerStartStopViewActionDelegateTest extends TestCase {

  protected boolean startServerIconCalled = false;
  protected boolean stopServerIconCalled = false;

  public void testClickingOnActionToglesIconAndTextBetweenStartStop() {
    Server server = new Server(4224);
    ServerStartStopViewActionDelegate delegate = new ServerStartStopViewActionDelegate(
        server, new Icons() {

          @Override
          public ImageDescriptor startServerIcon() {
            startServerIconCalled = true;
            stopServerIconCalled = false;
            return null;
          }

          @Override
          public ImageDescriptor stopServerIcon() {
            startServerIconCalled = false;
            stopServerIconCalled = true;
            return null;
          }

        });
    Action action = new Action() {
    };

    delegate.run(action);

    assertEquals("Stop Server", action.getText());
    assertTrue(stopServerIconCalled);
    assertFalse(startServerIconCalled);

    delegate.run(action);

    assertEquals("Start Server", action.getText());
    assertTrue(startServerIconCalled);
    assertFalse(stopServerIconCalled);
  }

}
