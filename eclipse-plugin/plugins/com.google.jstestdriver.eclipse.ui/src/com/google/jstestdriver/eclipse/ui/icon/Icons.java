package com.google.jstestdriver.eclipse.ui.icon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

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
    return new Image(Display.getCurrent(), Activator.getDefault()
        .getImageDescriptor("icons/Chrome.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getIEDisabledIcon() {
    return new Image(Display.getCurrent(), Activator.getDefault()
        .getImageDescriptor("icons/IE.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getFirefoxDisabledIcon() {
    return new Image(Display.getCurrent(), Activator.getDefault()
        .getImageDescriptor("icons/Firefox.png").createImage(),
        SWT.IMAGE_GRAY);
  }

  public Image getSafariDisabledIcon() {
    return new Image(Display.getCurrent(), Activator.getDefault()
        .getImageDescriptor("icons/Safari.png").createImage(),
        SWT.IMAGE_GRAY);
  }

  public Image getOperaDisabledIcon() {
    return new Image(Display.getCurrent(), Activator.getDefault()
        .getImageDescriptor("icons/Opera.png").createImage(),
        SWT.IMAGE_GRAY);
  }

  public Image getImage(String imagePath) {
    return Activator.getDefault().getImageDescriptor(imagePath).createImage();
  }

}
