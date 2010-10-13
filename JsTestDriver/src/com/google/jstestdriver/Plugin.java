package com.google.jstestdriver;

import com.google.jstestdriver.util.ManifestLoader;
import com.google.jstestdriver.util.ManifestLoader.ManifestNotFound;

import java.util.List;
import java.util.jar.Manifest;

/**
 * A representation of a parsed plugin.
 * @author corysmith
 */
public class Plugin {

  private final String pathToJar;
  private final String moduleName;
  private final String name;
  private final List<String> args;

  public Plugin(String name, String pathToJar, String moduleName, List<String> args) {
    this.name = name;
    this.pathToJar = pathToJar;
    this.moduleName = moduleName;
    this.args = args;
  }

  /**
   * This is accessor for the plugin name, used to identify the plugin.
   * It can be read either from the configuration file, or from the 
   * jar manifest.
   */
  public String getName(ManifestLoader manifestLoader) {
    if (name == null) {
      try {
        Manifest manifest = manifestLoader.load(pathToJar);
        return manifest.getAttributes("jstd").getValue("plugin-name");
      } catch (ManifestNotFound e) {
        throw new RuntimeException(e);
      }
    }
    return name;
  }

  /** The path to the jar containing the plugin.*/
  public String getPathToJar() {
    return pathToJar;
  }

  /** The fully qualified class path of the plugins module. Exclusive of the plugin-initializer.*/
  public String getModuleName(ManifestLoader manifestLoader) {
    if (moduleName == null) {
      try {
        Manifest manifest = manifestLoader.load(pathToJar);
        return manifest.getAttributes("jstd").getValue("plugin-module");
      } catch (ManifestNotFound e) {
        throw new RuntimeException(e);
      }
    }
    return moduleName;
  }

  /** The fully qualified name of the PluginInitializer class. Exclusive of the plugin-module. */
  public String getInitializerName(ManifestLoader manifestLoader) {
    try {
      Manifest manifest = manifestLoader.load(pathToJar);
      return manifest.getAttributes("jstd").getValue("plugin-initializer");
    } catch (ManifestNotFound e) {
      throw new RuntimeException(e);
    }
  }

  public List<String> getArgs() {
    return args;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((pathToJar == null) ? 0 : pathToJar.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Plugin other = (Plugin) obj;
    if (moduleName == null) {
      if (other.moduleName != null)
        return false;
    } else if (!moduleName.equals(other.moduleName))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (pathToJar == null) {
      if (other.pathToJar != null)
        return false;
    } else if (!pathToJar.equals(other.pathToJar))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Plugin [moduleName=" + moduleName + ", name=" + name + ", pathToJar=" + pathToJar
        + ", args=" + args + "]";
  }

  /** A factory method to return a new plugin with a new path. */
  public Plugin getPluginFromPath(String resolvedPath) {
    return new Plugin(name, resolvedPath, moduleName, args);
  }
}
