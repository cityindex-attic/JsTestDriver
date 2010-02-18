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
package com.google.eclipse.javascript.jstestdriver.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Helper for getting all projects in the workspace or getting a {@link IProject} by name.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class ProjectHelper {

  /**
   * Gets all the {@link IProject}s in the current open workspace.
   *
   * @return All the Projects in the current workspace
   */
  public IProject[] getAllProjects() {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return workspaceRoot.getProjects();
  }

  /**
   * Returns an {@link IProject} which matches the given name in the open workspace.
   *
   * @param projectName the project name as a string
   * @return the {@link IProject} which matches the given project name.
   */
  public IProject getProject(String projectName) {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    return workspaceRoot.getProject(projectName);
  }
}
