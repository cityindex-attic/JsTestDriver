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

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.jstestdriver.eclipse.core.SlaveBrowserRootData;
import com.google.jstestdriver.eclipse.core.SlaveBrowserSet;

/**
 * Content Provider for the Tree which displays the captured browsers.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class BrowserInfoContentProvider implements IStructuredContentProvider,
    ITreeContentProvider {


  public void inputChanged(Viewer v, Object oldInput, Object newInput) {
  }

  public void dispose() {
  }

  public Object[] getElements(Object parent) {
    return getChildren(parent);
  }

  public Object getParent(Object child) {
    if (child instanceof SlaveBrowserSet) {
      return ((SlaveBrowserSet) child).getParent();
    }
    return null;
  }

  public Object[] getChildren(Object parent) {
    if (parent instanceof SlaveBrowserRootData) {
      SlaveBrowserRootData slaveBrowserData = (SlaveBrowserRootData) parent;
      return new Object[] { slaveBrowserData.getFirefoxSlaves(),
          slaveBrowserData.getChromeSlaves(), slaveBrowserData.getIeSlaves(),
          slaveBrowserData.getSafariSlaves() };
    } else if (parent instanceof SlaveBrowserSet) {
      SlaveBrowserSet browserSet = (SlaveBrowserSet) parent;
      return browserSet.getSlaves().toArray();
    }
    return new Object[0];
  }

  public boolean hasChildren(Object parent) {
    return getChildren(parent).length > 0;
  }

}
