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
package com.google.eclipse.javascript.jstestdriver.ui.view.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.google.eclipse.javascript.jstestdriver.ui.view.JsTestDriverView;
import com.google.eclipse.javascript.jstestdriver.ui.view.TestResultsPanel;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class FilterTestResultsActionDelegate implements IViewActionDelegate {

  private TestResultsPanel view;
  private boolean shouldFilter = false;
  private ViewerFilter filter;
  
  public FilterTestResultsActionDelegate() {
    this(new FailureOnlyViewerFilter());
  }

  public FilterTestResultsActionDelegate(ViewerFilter filter) {
    this.filter = filter;
  }

  public void init(IViewPart view) {
    if (view instanceof JsTestDriverView) {
      this.view = ((JsTestDriverView) view).getTestResultsPanel();
    }
  }

  public void run(IAction action) {
    if (!shouldFilter) {
      shouldFilter = true;
      view.setTreeFilter(filter);
    } else {
      shouldFilter = false;
      view.clearTreeFilter();
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
  }

}
