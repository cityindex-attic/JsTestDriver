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

import com.google.eclipse.javascript.jstestdriver.core.model.ResultModel;
import com.google.eclipse.javascript.jstestdriver.ui.Activator;
import com.google.eclipse.javascript.jstestdriver.ui.Icons;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Describe how labels and images are provided for the test results tree.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class TestResultsTreeLabelProvider extends LabelProvider {
  private Icons icons = Activator.getDefault().getIcons();

  @Override
  public String getText(Object obj) {
    if (obj instanceof ResultModel) {
      return ((ResultModel) obj).getDisplayLabel();
    }
    return obj.toString();
  }

  @Override
  public Image getImage(Object obj) {
    if (obj instanceof ResultModel) {
      ResultModel resultModel = (ResultModel) obj;
      return icons.getImage(resultModel.getDisplayImagePath());
    }
    return null;
  }
}
