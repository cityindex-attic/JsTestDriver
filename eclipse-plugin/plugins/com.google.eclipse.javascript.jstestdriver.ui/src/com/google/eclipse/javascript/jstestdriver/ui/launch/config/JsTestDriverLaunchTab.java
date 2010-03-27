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
package com.google.eclipse.javascript.jstestdriver.ui.launch.config;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.eclipse.javascript.jstestdriver.core.ProjectHelper;
import com.google.eclipse.javascript.jstestdriver.ui.launch.JavascriptLaunchConfigurationHelper;
import com.google.eclipse.javascript.jstestdriver.ui.launch.LaunchConfigurationConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UI elements for the Js Test Driver Launch Configuration Tab, along with information on what 
 * information to set on the launch configuration and retrieve from it.
 * 
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class JsTestDriverLaunchTab extends AbstractLaunchConfigurationTab {

  private final Logger logger =
      Logger.getLogger(JsTestDriverLaunchTab.class.getName());
  private Combo projectCombo;
  private Combo confFileCombo;
  private Button runOnEverySaveCheckbox;
  private JavascriptLaunchConfigurationHelper configurationHelper =
      new JavascriptLaunchConfigurationHelper();

  public void createControl(Composite parent) {
    Composite control = new Composite(parent, SWT.NONE);
    control.setLayout(new GridLayout(1, false));
    super.setControl(control);

    Group jstdPropertiesControl = new Group(control, SWT.NONE);
    jstdPropertiesControl.setLayout(new GridLayout(2, false));
    jstdPropertiesControl.setText("JSTD:");
    GridData jstdGridData = new GridData(GridData.FILL_HORIZONTAL);
    jstdPropertiesControl.setLayoutData(jstdGridData);

    createJstdPropreties(jstdPropertiesControl);
    setUpProjectCombo();
  }
  
  private String[] getConfigurationFiles(IProject project) {
    try {
      List<String> resources = Lists.newLinkedList();
      for (IResource resource : project.members()) {
        if (resource.getName().endsWith(".conf")) {
          resources.add(resource.getName());
        }
      }
      Collections.sort(resources);
      return resources.toArray(new String[resources.size()]);
    } catch (CoreException e) {
      logger.log(Level.SEVERE, "", e);
      return new String[] {};
    }
  }

  private void createJstdPropreties(Composite control) {
    Label projectLabel = new Label(control, SWT.NONE);
    projectLabel.setText("Project:");

    projectCombo = new Combo(control, SWT.BORDER);
    GridData projectGridData = new GridData(GridData.FILL_HORIZONTAL);
    projectCombo.setLayoutData(projectGridData);
    projectCombo.addKeyListener(new KeyListener() {

      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    projectCombo.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setUpConfFileCombo();
        setTabDirty();
      }
    });

    Label confFileLabel = new Label(control, SWT.NONE);
    confFileLabel.setText("Conf File:");

    confFileCombo = new Combo(control, SWT.BORDER);
    GridData confFileGridData = new GridData(GridData.FILL_HORIZONTAL);
    confFileCombo.setLayoutData(confFileGridData);
    confFileCombo.addKeyListener(new KeyListener() {

      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    confFileCombo.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setTabDirty();
      }
    });
    
    GridData runOnEverySaveButtonGridData = new GridData(GridData.FILL_HORIZONTAL);
    runOnEverySaveButtonGridData.horizontalSpan = 3;
    runOnEverySaveCheckbox = new Button(control, SWT.CHECK);
    runOnEverySaveCheckbox.setLayoutData(runOnEverySaveButtonGridData);
    runOnEverySaveCheckbox.setText("Run on Every Save");
    runOnEverySaveCheckbox.addSelectionListener(new SelectionListener() {
      
      public void widgetSelected(SelectionEvent e) {
        setTabDirty();
      }
      
      public void widgetDefaultSelected(SelectionEvent e) {
      }
    });
  }

  private void setUpProjectCombo() {
    IProject[] projects = new ProjectHelper().getAllProjects();
    if (projects != null) {
      String[] projectNames = Lists.transform(Arrays.asList(projects),
          new Function<IProject, String>() {
        @Override
        public String apply(IProject project) {
          return project.getName();
        }
      }).toArray(new String[projects.length]);
      Arrays.sort(projectNames);
      projectCombo.setItems(projectNames);
    }
  }
  
  private void setUpConfFileCombo() {
    IProject project = getSelectedProject();
    if (project != null) {
      confFileCombo.setItems(getConfigurationFiles(project));
    }
  }

  private IProject getSelectedProject() {
    String projectName = getSelectedComboString(projectCombo);
    if (projectName != null && !"".equals(projectName)) {
      return new ProjectHelper().getProject(projectName);
    } else {
      return null;
    }
  }

  private void setTabDirty() {
    setDirty(true);
    updateLaunchConfigurationDialog();
  }

  public String getName() {
    return "JsTestDriver";
  }

  @Override
  public boolean isValid(ILaunchConfiguration launchConfig) {
    boolean isSelected =
        !"".equals(getSelectedComboString(projectCombo))
        && !"".equals(getSelectedComboString(confFileCombo));
    if (isSelected) {
      String projectName = getSelectedComboString(projectCombo);
      if (configurationHelper.isExistingLaunchConfigWithRunOnSaveOtherThanCurrent(projectName,
          launchConfig.getName()) && runOnEverySaveCheckbox.getSelection()) {
        setErrorMessage(MessageFormat.format("Project named {0} already has another active"
            + " configuration with Run on every save set.", projectName));
        return false;
      }
      setErrorMessage(null);
      return true;
    }
    return false;
  }

  @Override
  public void initializeFrom(ILaunchConfiguration configuration) {
    try {
      String initProjectName = configuration.getAttribute(
          LaunchConfigurationConstants.PROJECT_NAME, "");
      if (initProjectName != null && !"".equals(initProjectName.trim())) {
        selectComboItem(projectCombo, initProjectName);
        IProject project = new ProjectHelper().getProject(initProjectName);
        if (project == null || !project.exists()) {
          setErrorMessage(MessageFormat
              .format(
                  "Project named {0} does not exist. Please choose another project.",
                  initProjectName));
        }
        confFileCombo.setItems(getConfigurationFiles(project));
        String initConfFileName = configuration.getAttribute(
            LaunchConfigurationConstants.CONF_FILENAME, "");
        selectComboItem(confFileCombo, initConfFileName);
        runOnEverySaveCheckbox.setSelection(
            configuration.getAttribute(
                LaunchConfigurationConstants.RUN_ON_EVERY_SAVE,false));
      }


    } catch (CoreException e) {
      logger.log(Level.WARNING, "Core exception occured", e);
    }
  }

  private void selectComboItem(Combo combo, String item) {
    combo.select(Arrays.binarySearch(combo.getItems(), item));
  }
  
  private String getSelectedComboString(Combo combo) {
    int selectionIndex = combo.getSelectionIndex();
    if (selectionIndex != -1) {
      return combo.getItem(selectionIndex).trim();
    }
    return "";
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    if (getSelectedProject() != null) {
      configuration.setAttribute(LaunchConfigurationConstants.PROJECT_NAME,
          getSelectedComboString(projectCombo));
      configuration.setAttribute(LaunchConfigurationConstants.CONF_FILENAME,
          getSelectedComboString(confFileCombo));
      configuration.setAttribute(LaunchConfigurationConstants.RUN_ON_EVERY_SAVE,
          runOnEverySaveCheckbox.getSelection());
    }
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(LaunchConfigurationConstants.PROJECT_NAME, "");
    configuration.setAttribute(LaunchConfigurationConstants.CONF_FILENAME, "");
    configuration.setAttribute(LaunchConfigurationConstants.RUN_ON_EVERY_SAVE, false);
  }
}
