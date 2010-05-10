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
package com.google.jstestdriver;

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.jstestdriver.hooks.PluginInitializer;
import com.google.jstestdriver.util.ManifestLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the loading of Plugins from the filesystem.
 * 
 * @author corysmith
 */
public class PluginLoader {

  public static class InitializerModule implements Module {
    private final Class<PluginInitializer> initializer;

    public InitializerModule(Class<PluginInitializer> initializer) {
      this.initializer = initializer;
    }

    public void configure(Binder binder) {
      newSetBinder(binder, PluginInitializer.class).addBinding().to(initializer);
    }
  }

  final ManifestLoader manifestLoader = new ManifestLoader();
  /**
   * For each plugin, the specified jar is loaded, then the specified class is
   * extracted from the Jar.
   * 
   * @return a list of {@code Module}
   */
  public List<Module> load(List<Plugin> plugins) {
    List<Module> modules = new LinkedList<Module>();
    for (Plugin plugin : plugins) {

      // TODO(corysmith): figure out how to test this...
      try {
        URLClassLoader urlClassLoader = new URLClassLoader(
            new URL[] { new URL("jar:file:" + plugin.getPathToJar() + "!/") },
            getClass().getClassLoader());
        Class<?> module = getPluginMainClass(plugin, urlClassLoader);

        modules.add(getModuleInstance(plugin, module));
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return modules;
  }

  // TODO(corysmith): Look for the elegant solution, or
  // just deprecate leading with a module.
  private Class<?> getPluginMainClass(Plugin plugin, URLClassLoader urlClassLoader)
      throws ClassNotFoundException {
    final String moduleName = plugin.getModuleName(manifestLoader);
    if (moduleName != null && moduleName.length() > 0) {
      return  Class.forName(moduleName, true, urlClassLoader);
    }
    final String initializerName = plugin.getInitializerName(manifestLoader);
    if (initializerName != null && initializerName.length() > 0) {
      return Class.forName(initializerName, true, urlClassLoader);
    }
    throw new IllegalArgumentException("Cannot determine main class for "
        + plugin.getName(manifestLoader) +
        " please see http://code.google.com/p/js-test-driver/wiki/Plugins#mainclass.");
  }

  @SuppressWarnings("unchecked")
  private Module getModuleInstance(Plugin plugin, Class<?> mainClass) {
    try {
      if (PluginInitializer.class.isAssignableFrom(mainClass)) {
        return new InitializerModule((Class<PluginInitializer>)mainClass);
      }

      Constructor<Module> argsConstructor =
          ((Class<Module>)mainClass).getConstructor(List.class);

      return argsConstructor.newInstance(plugin.getArgs());
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchMethodException e) {
      try {
        return ((Class<Module>) mainClass).newInstance();
      } catch (InstantiationException e1) {
        throw new RuntimeException(e1);
      } catch (IllegalAccessException e1) {
        throw new RuntimeException(e1);
      }
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
