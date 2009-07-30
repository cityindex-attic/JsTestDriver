package com.google.jstestdriver.eclipse.ui.icon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

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
  
  public Image getChromeDisabledIcon() {
    return Activator.getDefault().getImageDescriptor("icons/Chrome_None.png").createImage();
  }
  public Image getIEDisabledIcon() {
    return Activator.getDefault().getImageDescriptor("icons/IE_None.png").createImage();
  }
  public Image getFirefoxDisabledIcon() {
    return Activator.getDefault().getImageDescriptor("icons/Firefox_None.png").createImage();
  }
  public Image getSafariDisabledIcon() {
    return Activator.getDefault().getImageDescriptor("icons/Safari_None.png").createImage();
  }
  

  public Image getImage(String imagePath) {
    return Activator.getDefault().getImageDescriptor(imagePath).createImage();
  }
}
