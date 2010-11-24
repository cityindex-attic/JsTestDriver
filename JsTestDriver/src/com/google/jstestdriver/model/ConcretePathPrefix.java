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
package com.google.jstestdriver.model;

/** Represents a prefix for a path. */
public class ConcretePathPrefix implements HandlerPathPrefix {
  private final String prefix;

  public ConcretePathPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String prefixPath(String path) {
    if (path.startsWith("/" + prefix)) {
      return path;
    }
    return "/" + prefix + path;
  }

  public String prefixPath(String path, String defaultPrefix) {
    return prefixPath(path);
  }

  public String suffixServer(String server) {
    return server + "/" + prefix;
  }
}
