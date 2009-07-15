package com.google.jstestdriver;

/**
 * A representation of a parsed plugin.
 * @author corysmith
 */
public class Plugin {

  private final String pathToJar;
  private final String moduleName;
  private final String name;

  public Plugin(String name, String pathToJar, String moduleName) {
    this.name = name;
    this.pathToJar = pathToJar;
    this.moduleName = moduleName;
  }

  public String getName() {
    return name;
  }

  public String getPathToJar() {
    return pathToJar;
  }

  public String getModuleName() {
    return moduleName;
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
    return "Plugin [moduleName=" + moduleName + ", name=" + name + ", pathToJar=" + pathToJar + "]";
  }
}
