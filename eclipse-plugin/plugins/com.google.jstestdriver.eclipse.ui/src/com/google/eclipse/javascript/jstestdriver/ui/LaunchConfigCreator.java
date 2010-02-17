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
package com.google.eclipse.javascript.jstestdriver.ui;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Other plugins can contribute potential Launch Configurations which the Launch Shortcuts
 * will defer to first before trying the default behavior.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public interface LaunchConfigCreator {

  ILaunchConfiguration getLaunchConfiguration(String projectName, List<IFile> selectedFiles);
}
