package com.google.jstestdriver.eclipse.ui.icon;

import org.eclipse.jface.resource.ImageDescriptor;

import com.google.jstestdriver.eclipse.ui.Activator;

public class Icons {

  public ImageDescriptor startServerIcon() {
    return Activator.getDefault().getImageDescriptor("icons/startServer.png");
  }

  public ImageDescriptor stopServerIcon() {
    return Activator.getDefault().getImageDescriptor("icons/stopServer.png");
  }
  
  public ImageDescriptor configurationFileIcon() {
    return Activator.getDefault().getImageDescriptor("icons/configuration.png");
  }

  public ImageDescriptor projectIcon() {
    return Activator.getDefault().getImageDescriptor("icons/projects.gif");
  }
}
