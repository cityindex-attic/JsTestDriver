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
package com.google.eclipse.javascript.jstestdriver.core;

import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Gets notified of JSTestDriver launches.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public interface JstdLaunchListener {

  /**
   * Notifies all listeners about the pending launch.
   * @param launchConfiguration The launchConfiguration with which the launch is about to happen.
   */
  void aboutToLaunch(ILaunchConfiguration launchConfiguration);
}
