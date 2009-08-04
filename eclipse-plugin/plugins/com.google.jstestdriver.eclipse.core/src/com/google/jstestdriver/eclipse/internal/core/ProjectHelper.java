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
package com.google.jstestdriver.eclipse.internal.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import com.google.jstestdriver.ConfigurationParser;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class ProjectHelper {

  private Logger logger = new Logger();
  
  public IProject[] getAllProjects() {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return workspaceRoot.getProjects();
  }
  
  public IProject getProject(String projectName) {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return workspaceRoot.getProject(projectName);
  }
  
  public ConfigurationParser getConfigurationParser(String projectName,
      String confFileName) {
    IProject project = getProject(projectName);
    IResource confFileResource = project.findMember(confFileName);
    File configFile = confFileResource.getLocation().toFile();
    File parentDir = configFile.getParentFile();
    ConfigurationParser configurationParser = null;
    try {
      configurationParser = new ConfigurationParser(parentDir, new FileReader(configFile));
    } catch (FileNotFoundException e) {
      logger.logException(e);
    }
    return configurationParser;
  }
}
