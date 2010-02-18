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
package com.google.eclipse.javascript.jstestdriver.ui.view;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.eclipse.javascript.jstestdriver.core.model.ResultModel;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 *
 */
public class TestResultsTreeContentProvider implements
    IStructuredContentProvider, ITreeContentProvider {

  public Object[] getElements(Object inputElement) {
    return getChildren(inputElement);
  }

  public void dispose() {
  }

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
  }

  public Object[] getChildren(Object parentElement) {
    if (parentElement instanceof ResultModel) {
      return ((ResultModel) parentElement).getChildren().toArray();
    }
    return null;
  }

  public Object getParent(Object element) {
    if (element instanceof ResultModel) {
      return ((ResultModel) element).getParent();
    }
    return null;
  }

  public boolean hasChildren(Object element) {
    if (element instanceof ResultModel) {
      return ((ResultModel) element).hasChildren();
    }
    return false;
  }

}
