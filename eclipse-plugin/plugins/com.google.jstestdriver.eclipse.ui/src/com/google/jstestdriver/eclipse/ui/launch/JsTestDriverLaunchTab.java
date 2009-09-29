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

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.google.jstestdriver.eclipse.internal.core.Logger;
import com.google.jstestdriver.eclipse.internal.core.ProjectHelper;
import com.google.jstestdriver.eclipse.ui.icon.Icons;

/**
 * UI elements for the Js Test Driver Launch Configuration Tab, along with information on what 
 * information to set on the launch configuration and retrieve from it.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JsTestDriverLaunchTab extends AbstractLaunchConfigurationTab {

  private final Logger logger = new Logger();
  private final JavascriptLaunchConfigurationHelper configurationHelper =
      new JavascriptLaunchConfigurationHelper();
  private Text projectText;
  private Text confFileText;
  private Button runOnEverySaveCheckbox;

  public void createControl(Composite parent) {
    Composite control = new Composite(parent, SWT.NONE);
    control.setLayout(new GridLayout(1, false));
    super.setControl(control);

    Group jstdPropertiesControl = new Group(control, SWT.NONE);
    jstdPropertiesControl.setLayout(new GridLayout(3, false));
    jstdPropertiesControl.setText("JSTD:");
    GridData jstdGridData = new GridData(GridData.FILL_HORIZONTAL);
    jstdPropertiesControl.setLayoutData(jstdGridData);

    createJstdPropreties(jstdPropertiesControl);

  }

  private void createJstdPropreties(Composite control) {
    Label projectLabel = new Label(control, SWT.NONE);
    projectLabel.setText("Project:");

    projectText = new Text(control, SWT.BORDER);
    GridData projectGridData = new GridData(GridData.FILL_HORIZONTAL);
    projectText.setLayoutData(projectGridData);
    projectText.addKeyListener(new KeyListener() {

      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Button projectBrowseButton = new Button(control, SWT.PUSH);
    projectBrowseButton.setText("Browse...");
    projectBrowseButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setUpBrowseProjectDialog();
      }
    });

    Label confFileLabel = new Label(control, SWT.NONE);
    confFileLabel.setText("Conf File:");

    confFileText = new Text(control, SWT.BORDER);
    GridData confFileGridData = new GridData(GridData.FILL_HORIZONTAL);
    confFileText.setLayoutData(confFileGridData);
    confFileText.addKeyListener(new KeyListener() {

      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Button confFileBrowseButton = new Button(control, SWT.PUSH);
    confFileBrowseButton.setText("Browse...");
    confFileBrowseButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setUpBrowseConfFileDialog();
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

  private void setUpBrowseProjectDialog() {
    ILabelProvider labelProvider = new BrowseProjectLabelProvider();
    IProject[] projects = new ProjectHelper().getAllProjects();
    ElementListSelectionDialog dialog = new ElementListSelectionDialog(
        getControl().getShell(), labelProvider);
    dialog.setMessage("Choose your project:");
    if (projects != null) {
      dialog.setElements(projects);
    }

    if (dialog.open() == Window.OK) {
      IProject project = (IProject) dialog.getFirstResult();
      projectText.setText(project.getName());
      setTabDirty();
    }
  }

  private void setUpBrowseConfFileDialog() {
    IProject selectedProject = getSelectedProject();
    if (selectedProject == null) {
      return;
    }
    ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
        getControl().getShell(), new BrowseConfFileLabelProvider(),
        new BrowseConfFileContentProvider());
    dialog.setInput(selectedProject);
    dialog.setMessage("Choose config file:");
    if (dialog.open() == Window.OK) {
      IResource resource = (IResource) dialog.getFirstResult();
      confFileText.setText(resource.getName());
      setTabDirty();
    }
  }

  private IProject getSelectedProject() {
    String projectName = projectText.getText();
    if (projectName != null && !"".equals(projectName.trim())) {
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
    boolean isTextBoxFilled = !"".equals(projectText.getText().trim())
        && !"".equals(confFileText.getText().trim());
    if (isTextBoxFilled) {
      String projectName = projectText.getText();
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

  public void initializeFrom(ILaunchConfiguration configuration) {
    try {
      String initProjectName = configuration.getAttribute(
          LaunchConfigurationConstants.PROJECT_NAME, "");
      if (initProjectName != null && !"".equals(initProjectName.trim())) {
        projectText.setText(initProjectName);
        IProject project = new ProjectHelper().getProject(initProjectName);
        if (project == null || !project.exists()) {
          setErrorMessage(MessageFormat
              .format(
                  "Project named {0} does not exist. Please choose another project.",
                  initProjectName));
        }
      } else {
        projectText.setText("");
      }

      String initConfFileName = configuration.getAttribute(
          LaunchConfigurationConstants.CONF_FILENAME, "");
      if (initConfFileName != null && !"".equals(initConfFileName.trim())) {
        confFileText.setText(initConfFileName);
      } else {
        confFileText.setText("");
      }
      
      boolean initRunOnEveryBuild = configuration.getAttribute(
          LaunchConfigurationConstants.RUN_ON_EVERY_SAVE, false);
      runOnEverySaveCheckbox.setSelection(initRunOnEveryBuild);
    } catch (CoreException e) {
      logger.logException(e);
    }
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    if (getSelectedProject() != null) {
      configuration.setAttribute(LaunchConfigurationConstants.PROJECT_NAME,
          projectText.getText());
      configuration.setAttribute(LaunchConfigurationConstants.CONF_FILENAME,
          confFileText.getText());
      configuration.setAttribute(LaunchConfigurationConstants.RUN_ON_EVERY_SAVE,
          runOnEverySaveCheckbox.getSelection());
    }
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(LaunchConfigurationConstants.PROJECT_NAME, "");
    configuration.setAttribute(LaunchConfigurationConstants.CONF_FILENAME, "");
    configuration.setAttribute(LaunchConfigurationConstants.RUN_ON_EVERY_SAVE, false);
  }

  private class BrowseConfFileLabelProvider extends LabelProvider {
    @Override
    public Image getImage(Object element) {
      if (element instanceof IResource
          && ((IResource) element).getName().endsWith(".conf")) {
        return new Icons().configurationFileIcon().createImage();
      }
      return null;
    }
    
    @Override
    public String getText(Object element) {
      if (element instanceof IResource
          && ((IResource) element).getName().endsWith(".conf")) {
        return ((IResource) element).getName();
      }
      return null;
    }
  }

  private class BrowseProjectLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object element) {
      if (element instanceof IProject) {
        return new Icons().projectIcon().createImage();
      }
      return null;
    }

    @Override
    public String getText(Object element) {
      if (element instanceof IProject) {
        return ((IProject) element).getName();
      }
      return null;
    }
  }
}
