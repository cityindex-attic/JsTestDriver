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
package com.google.eclipse.javascript.jstestdriver.ui.prefs;

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class BrowserChooserEditorField extends FileFieldEditor {

  public BrowserChooserEditorField(String name, String labelText,
      Composite parent) {
    super(name, labelText, parent);
  }

  /**
   * Overriding the parent checkState method to just check if the selected file exists. Not checking
   * for directory or file, both of which cause issues in one or the other OS. The code is almost
   * entirely identical, except we check for file.exists() instead of file.isFile() or
   * file.isDirectory()
   */
  @Override
  protected boolean checkState() {
    String msg = null;

    String path = getTextControl().getText();
    if (path != null) {
      path = path.trim();
    } else {
      path = "";
    }
    if (path.length() == 0) {
      if (!isEmptyStringAllowed()) {
        msg = getErrorMessage();
      }
    } else {
      File file = new File(path);
      if (!file.exists()) {
        msg = getErrorMessage();
      }
    }

    if (msg != null) { 
      showErrorMessage(msg);
      return false;
    }

    clearErrorMessage();
    return true;
  }
}
