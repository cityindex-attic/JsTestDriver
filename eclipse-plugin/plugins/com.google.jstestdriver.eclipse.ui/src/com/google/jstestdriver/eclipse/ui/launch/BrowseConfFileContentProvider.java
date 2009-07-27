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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.jstestdriver.eclipse.internal.core.Logger;

/**
 * Tree Content Provider which only displays the ".conf" files in a project.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class BrowseConfFileContentProvider implements ITreeContentProvider {
  
  private final Logger logger = new Logger();

  public Object[] getChildren(Object parentElement) {
    List<IResource> resources = new ArrayList<IResource>();
    if (parentElement instanceof IProject) {
      try {
        for (IResource resource : ((IProject) parentElement).members()) {
          if (resource.getName().endsWith(".conf")) {
            resources.add(resource);
          }
        }
      } catch (CoreException e) {
        logger.logException(e);
      }
    }
    return resources.toArray();
  }

  public Object getParent(Object element) {
    return null;
  }

  public boolean hasChildren(Object element) {
    return getChildren(element).length > 0;
  }

  public Object[] getElements(Object inputElement) {
    return getChildren(inputElement);
  }

  public void dispose() {
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }

}
