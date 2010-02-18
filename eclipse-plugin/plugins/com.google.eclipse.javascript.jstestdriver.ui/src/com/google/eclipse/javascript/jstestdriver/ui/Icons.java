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
package com.google.eclipse.javascript.jstestdriver.ui;

import com.google.common.collect.Maps;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import java.net.URL;
import java.util.Map;

/**
 * Convenient wrapper for all of JsTestDriver's icons. Handles making sure that only one
 * instance of any particular image is shopped around. Keeps a reference around to ensure it
 * can clean up the images at the end of the plugin's life cyle. The {@link Activator} has that
 * responsibility.
 *
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class Icons {

  private static final String OPERA_ICON_PATH = "icons/Opera.png";
  private static final String SAFARI_ICON_PATH = "icons/Safari.png";
  private static final String FIREFOX_ICON_PATH = "icons/Firefox.png";
  private static final String IE_ICON_PATH = "icons/IE.png";
  private static final String CHROME_ICON_PATH = "icons/Chrome.png";

  private Map<String, Image> images = Maps.newHashMap();
  private Map<String, Image> greyedImages = Maps.newHashMap();

  /**
   * The icon used in the button to Start the server.
   */
  public ImageDescriptor startServerIcon() {
    return ImageDescriptor.createFromURL(getImageFileUrl("icons/startServer.png"));
  }

  /**
   * The icon used in the button to Stop the server.
   */
  public ImageDescriptor stopServerIcon() {
    return ImageDescriptor.createFromURL(getImageFileUrl("icons/stopServer.png"));
  }

  private URL getImageFileUrl(String imagePath) {
    final Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
    final Path imageFilePath = new Path(imagePath);
    return FileLocator.find(bundle, imageFilePath, null);
  }

  /**
   * Icon used alongside a project in the Select Project dialog.
   */
  public Image projectIcon() {
    return getImage("icons/projects.gif");
  }

  /**
   * Icon used alongside a project in the Select Project dialog.
   */
  public Image configurationFileIcon() {
    return getImage("icons/configuration.png");
  }

  /**
   * Gets the grayed out Chrome icon to be displayed when no Chrome browsers are captured.
   */
  public Image getChromeDisabledIcon() {
    return getGrayedIcon(CHROME_ICON_PATH);
  }

  /**
   * Gets the grayed out IE icon to be displayed when no IE browsers are captured.
   */
  public Image getIEDisabledIcon() {
    return getGrayedIcon(IE_ICON_PATH);
  }

  /**
   * Gets the grayed out Firefox icon to be displayed when no Firefox browsers are captured.
   */
  public Image getFirefoxDisabledIcon() {
    return getGrayedIcon(FIREFOX_ICON_PATH);
  }

  /**
   * Gets the grayed out Safari icon to be displayed when no Safari browsers are captured.
   */
  public Image getSafariDisabledIcon() {
    return getGrayedIcon(SAFARI_ICON_PATH);
  }

  /**
   * Gets the grayed out Opera icon to be displayed when no Opera browsers are captured.
   */
  public Image getOperaDisabledIcon() {
    return getGrayedIcon(OPERA_ICON_PATH);
  }

  /**
   * Gets the grayed out icon for the given icon image.
   * @param iconPath the path to the icon.
   * @return the grayed out image.
   */
  public Image getGrayedIcon(String iconPath) {
    synchronized (this) {
      if (!greyedImages.containsKey(iconPath)) {
        greyedImages.put(iconPath, new Image(Display.getCurrent(),
            ImageDescriptor.createFromURL(getImageFileUrl(iconPath)).createImage(),
            SWT.IMAGE_GRAY));
      }
      return greyedImages.get(iconPath);
    }
  }

  /**
   * Returns an image from the given path. Makes sure that only one instance of an image is passed
   * around even if multiple requests are made for the path. Callers should not worry about
   * releasing the image resource, as that will be handled by the Activator at the end of the
   * plugin's lifecycle.
   *
   * @param imagePath The path to the image.
   * @return the image
   */
  public Image getImage(String imagePath) {
    synchronized (this) {
      if (!images.containsKey(imagePath)) {
        images.put(imagePath,
            ImageDescriptor.createFromURL(getImageFileUrl(imagePath)).createImage());
      }
      return images.get(imagePath);
    }
  }

  /**
   * Disposes all the images that have been handed out by this instance.
   */
  public void disposeAllImages() {
    synchronized (this) {
      for (Image image : images.values()) {
        image.dispose();
      }
      for (Image image : greyedImages.values()) {
        image.dispose();
      }
      images.clear();
      greyedImages.clear();
    }
  }
}