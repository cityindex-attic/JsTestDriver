package com.google.jstestdriver.eclipse.ui.icon;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class Icons {

  public ImageDescriptor startServerIcon() {
    return ImageDescriptor.createFromFile(getClass(), "icons/startServer.png");
  }

  public ImageDescriptor stopServerIcon() {
    return ImageDescriptor.createFromFile(getClass(), "icons/stopServer.png");
  }

  public ImageDescriptor configurationFileIcon() {
    return ImageDescriptor
        .createFromFile(getClass(), "icons/configuration.png");
  }

  public ImageDescriptor projectIcon() {
    return ImageDescriptor.createFromFile(getClass(), "icons/projects.gif");
  }

  public Image getChromeDisabledIcon() {
    return new Image(Display.getCurrent(), ImageDescriptor.createFromFile(
        getClass(), "icons/Chrome.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getIEDisabledIcon() {
    return new Image(Display.getCurrent(), ImageDescriptor.createFromFile(
        getClass(), "icons/IE.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getFirefoxDisabledIcon() {
    return new Image(Display.getCurrent(), ImageDescriptor.createFromFile(
        getClass(), "icons/Firefox.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getSafariDisabledIcon() {
    return new Image(Display.getCurrent(), ImageDescriptor.createFromFile(
        getClass(), "icons/Safari.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getOperaDisabledIcon() {
    return new Image(Display.getCurrent(), ImageDescriptor.createFromFile(
        getClass(), "icons/Opera.png").createImage(), SWT.IMAGE_GRAY);
  }

  public Image getImage(String imagePath) {
    return ImageDescriptor.createFromFile(getClass(), imagePath).createImage();
  }
}
