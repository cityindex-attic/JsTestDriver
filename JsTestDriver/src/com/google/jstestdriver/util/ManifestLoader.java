/*
 * Copyright 2010 Google Inc.
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
package com.google.jstestdriver.util;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Simple loader for retrieving manifests form jar files.
 *
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class ManifestLoader {
  public class ManifestNotFound extends Exception {
    private static final long serialVersionUID = 3137031869991435071L;

    public ManifestNotFound(String message) {
      super(message);
    }

    public ManifestNotFound(String message, IOException e) {
      super(message, e);
    }
  }

  public Manifest load(String jarPath) throws ManifestNotFound {
    final File jarFile = new File(jarPath);
    try {
      JarFile jar = new JarFile(jarFile);
      Manifest manifest = jar.getManifest();
      if (manifest == null) {
        throw new ManifestNotFound("Could not load manifest from jar:" + jarPath);
      }
      return manifest;
    } catch (IOException e) {
      throw new ManifestNotFound(
          "Could not load manifest from jar:" + jarPath, e);
    }

  }
}
