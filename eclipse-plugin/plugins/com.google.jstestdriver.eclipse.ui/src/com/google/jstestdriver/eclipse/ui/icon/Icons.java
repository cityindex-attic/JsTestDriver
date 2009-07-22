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

}
