// Copyright 2009 Google Inc. All Rights Reserved.
package com.google.eclipse.javascript.jstestdriver.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Helper for getting all projects in the workspace or getting a {@link IProject} by name.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
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
