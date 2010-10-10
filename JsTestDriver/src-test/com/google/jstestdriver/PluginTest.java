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
package com.google.jstestdriver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.jar.Manifest;

import junit.framework.TestCase;

import com.google.jstestdriver.util.ManifestLoader;
import com.google.jstestdriver.util.ManifestLoader.ManifestNotFound;

/**
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class PluginTest extends TestCase {
  public void testGetName() throws Exception {
    final String name = "foo";
    final Plugin plugin = new Plugin(name, "somePath", null, null);
    assertEquals(name, plugin.getName(null));
  }
  public void testGetNameFallBackToManifest() throws Exception {
    final String name = "foo";
    final Plugin plugin = new Plugin(null, "somePath", null, null);
    assertEquals(name, plugin.getName(new ManifestLoader(){
      @SuppressWarnings("unused")
      @Override
      public Manifest load(String jarPath) throws ManifestNotFound {
        return createManifest("jstd", "fpp", name);
      }
    }));
  }

  public void testGetModuleName() throws Exception {
    final String moduleName = "moduleName";
    final Plugin plugin = new Plugin("foo", "somePath", moduleName, null);
    assertEquals(moduleName, plugin.getModuleName(null));
  }

  public void testGetModuleNameFromManifest() throws Exception {
    final String moduleName = "moduleName";
    final Plugin plugin = new Plugin("foo", "somePath", null, null);
    assertEquals(moduleName, plugin.getModuleName(new ManifestLoader(){
      @SuppressWarnings("unused")
      @Override
      public Manifest load(String jarPath) throws ManifestNotFound {
        return createManifest("jstd", moduleName, "foo");
      }
    }));
  }

  public void testGetModuleNameNotFoundFromManifest() throws Exception {
    final Plugin plugin = new Plugin("foo", "somePath", null, null);
    try {
      plugin.getModuleName(new ManifestLoader(){
        @Override
        public Manifest load(String jarPath) throws ManifestNotFound {
          throw new ManifestNotFound("");
        }
      });
    } catch (RuntimeException e) {
      assertEquals(ManifestNotFound.class,
                   e.getCause().getClass());
    }
  }

  private Manifest createManifest(final String sectionName, 
                                  final String moduleName, String name) {
    try {
      return new Manifest(new ByteArrayInputStream(
          ("Manifest-Version: 1.0\n" +
          "Ant-Version: Apache Ant 1.7.1\n" +
          "Created-By: 14.3-b01-101 (Apple Inc.)\n" +
          "Class-Path: ../JsTestDriver.jar\n" +
          "\n" +
          "Name: " + sectionName + "\n" +
          "plugin-name: " + name + "\n" +
          "plugin-module: " + moduleName + "\n").getBytes()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
