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
package com.google.jstestdriver.eclipse.internal.core;

import org.eclipse.core.runtime.Status;

import com.google.jstestdriver.eclipse.core.Activator;

/**
 * Utility class which provides integration with Eclipse's loggin framework.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class Logger {

  public void logException(String msg, Throwable e) {
    Activator.getDefault().getLog().log(
        new Status(Status.ERROR, Activator.PLUGIN_ID, msg, e));
  }

  public void logException(Throwable e) {
    Activator.getDefault().getLog().log(
        new Status(Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
  }
}
