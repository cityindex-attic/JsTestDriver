// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.core;

import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Gets notified of JSTestDriver launches.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public interface JstdLaunchListener {

  /**
   * Notifies all listeners about the pending launch.
   * @param launchConfiguration The launchConfiguration with which the launch is about to happen.
   */
  void aboutToLaunch(ILaunchConfiguration launchConfiguration);
}
