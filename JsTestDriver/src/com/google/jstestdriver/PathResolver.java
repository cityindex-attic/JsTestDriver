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

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class PathResolver {

  private static final String PATH_SEPARATOR = File.separator;

  public String resolvePath(String path) {
    Stack<String> resolvedPath = new Stack<String>();
    String pathSeparator = PATH_SEPARATOR;

    if (!pathSeparator.equals("/")) {
      pathSeparator = "\\\\";
    }
    String[] tokenizedPath = path.split(pathSeparator);

    for (String token : tokenizedPath) {
      if (token.equals("..")) {
        if (!resolvedPath.isEmpty()) {
          resolvedPath.pop();
          continue;
        }
      }
      resolvedPath.push(token);
    }
    return join(resolvedPath);
  }

  private String join(Collection<String> collection) {
    StringBuilder sb = new StringBuilder();
    Iterator<String> iterator = collection.iterator();

    if (iterator.hasNext()) {
      sb.append(iterator.next());

      while (iterator.hasNext()) {
        sb.append(PATH_SEPARATOR);
        sb.append(iterator.next());
      }
    }
    return sb.toString();
  }
}
