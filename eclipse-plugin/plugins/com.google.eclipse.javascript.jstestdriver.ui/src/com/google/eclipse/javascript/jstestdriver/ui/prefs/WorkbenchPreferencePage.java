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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.eclipse.javascript.jstestdriver.ui.Activator;

/**
 * Page to display and set global settings and preferences for JS Test Driver.
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class WorkbenchPreferencePage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage {

  public static final String OPERA_PATH = "com.google.jstestdriver.eclipse.ui.operaPath";
  public static final String IE_PATH = "com.google.jstestdriver.eclipse.ui.iePath";
  public static final String CHROME_PATH = "com.google.jstestdriver.eclipse.ui.chromePath";
  public static final String FIREFOX_PATH = "com.google.jstestdriver.eclipse.ui.firefoxPath";
  public static final String SAFARI_PATH = "com.google.jstestdriver.eclipse.ui.safariPath";
  public static final String PREFERRED_SERVER_PORT =
      "com.google.jstestdriver.eclipse.ui.serverPort";

  public WorkbenchPreferencePage() {
    super(GRID);
  }

  public void init(IWorkbench workbench) {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("JS Test Driver Preferences");
  }

  @Override
  protected void createFieldEditors() {
    addField(new IntegerFieldEditor(PREFERRED_SERVER_PORT, "Port to start server on",
        getFieldEditorParent()));
    addField(new BrowserChooserEditorField(SAFARI_PATH, "Path to Safari", getFieldEditorParent()));
    addField(new BrowserChooserEditorField(FIREFOX_PATH, "Path to Firefox", getFieldEditorParent()));
    addField(new BrowserChooserEditorField(CHROME_PATH, "Path to Chrome", getFieldEditorParent()));
    addField(new BrowserChooserEditorField(IE_PATH, "Path to IE", getFieldEditorParent()));
    addField(new BrowserChooserEditorField(OPERA_PATH, "Path to Opera", getFieldEditorParent()));
  }

}
