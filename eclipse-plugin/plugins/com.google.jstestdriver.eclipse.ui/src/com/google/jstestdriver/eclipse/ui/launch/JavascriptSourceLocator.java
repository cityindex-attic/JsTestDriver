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
package com.google.jstestdriver.eclipse.ui.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JavascriptSourceLocator implements IPersistableSourceLocator {
  // PLACEHOLDER, will fill in when source code is needed.
  public String getMemento() throws CoreException {
    return null;
  }

  public void initializeDefaults(ILaunchConfiguration configuration)
      throws CoreException {
  }

  public void initializeFromMemento(String memento) throws CoreException {

  }

  public Object getSourceElement(IStackFrame stackFrame) {
    return null;
  }

}
