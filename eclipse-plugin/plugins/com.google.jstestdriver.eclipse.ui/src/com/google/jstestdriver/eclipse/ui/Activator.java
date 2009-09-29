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
package com.google.jstestdriver.eclipse.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.jstestdriver.eclipse.ui.launch.save.JavascriptOnSaveTestRunner;

public class Activator extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "com.google.jstestdriver.eclipse.ui";
  private Map<String, ImageDescriptor> descriptors = new HashMap<String, ImageDescriptor>();
  private static Activator plugin;

  public void start(BundleContext context) throws Exception {
    super.start(context);
    ResourcesPlugin.getWorkspace().addResourceChangeListener(new JavascriptOnSaveTestRunner(),
        IResourceChangeEvent.POST_BUILD);
    plugin = this;
  }

  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static Activator getDefault() {
    return plugin;
  }

  public ImageDescriptor getImageDescriptor(String path)
      throws ImageNotFoundException {
    ImageDescriptor descriptor = descriptors.get(path);
    if (descriptor == null) {
      String pluginLocation = Activator.getDefault().getBundle().getLocation();
      if (pluginLocation.startsWith("reference:")) {
        pluginLocation = pluginLocation.substring(10);
      }
      URL url;
      try {
        url = new URL(pluginLocation + path);
      } catch (MalformedURLException e) {
        throw new ImageNotFoundException("Image : " + path + " not found");
      }

      descriptor = ImageDescriptor.createFromURL(url);
      descriptors.put(path, descriptor);
    }
    return descriptor;
  }
}
