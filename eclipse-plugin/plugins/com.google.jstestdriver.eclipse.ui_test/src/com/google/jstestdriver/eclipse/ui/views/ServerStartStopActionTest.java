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

import com.google.jstestdriver.eclipse.ui.icon.Icons;

import junit.framework.TestCase;

public class ServerStartStopActionTest extends TestCase {

    public void testClickingOnActionToglesIconAndTextBetweenStartStop() {
        ServerStartStopAction action = new ServerStartStopAction(new Icons());
        assertEquals("Start Server", action.getText());
        assertTrue(action.getImageDescriptor().toString().contains("StartServer.png"));

        action.run();

        assertEquals("Stop Server", action.getText());
        assertTrue(action.getImageDescriptor().toString().contains("StopServer.png"));

        action.run();

        assertEquals("Start Server", action.getText());
        assertTrue(action.getImageDescriptor().toString().contains("StartServer.png"));
    }

}
