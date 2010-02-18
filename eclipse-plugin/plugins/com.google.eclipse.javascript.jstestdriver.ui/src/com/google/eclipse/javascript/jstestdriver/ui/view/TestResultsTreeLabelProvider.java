// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.eclipse.javascript.jstestdriver.ui.view;

import com.google.eclipse.javascript.jstestdriver.core.model.ResultModel;
import com.google.eclipse.javascript.jstestdriver.ui.Activator;
import com.google.eclipse.javascript.jstestdriver.ui.Icons;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Describe how labels and images are provided for the test results tree.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
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
