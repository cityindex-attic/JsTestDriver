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
package com.google.eclipse.javascript.jstestdriver.ui.launch;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class LaunchConfigurationConstants {

  private LaunchConfigurationConstants() {}
  
  public static final String PROJECT_NAME = "com.google.jstestdriver.eclipse.ui.projectName";
  public static final String CONF_FILENAME = "com.google.jstestdriver.eclipse.ui.confFileName";
  public static final String TESTS_TO_RUN = "com.google.jstestdriver.eclipse.ui.testsToRun";
  public static final String RUN_ON_EVERY_SAVE =
      "com.google.jstestdriver.eclipse.ui.runOnEverySave";
  public static final String JSTD_LAUNCH_CONFIGURATION_TYPE =
    "com.google.eclipse.javascript.jstestdriver.ui.JstdTestDriverLaunchConfiguration";
}
