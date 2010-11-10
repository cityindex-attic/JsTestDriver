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
package com.google.jstestdriver.util;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Parses parameter from a request object.
 * 
 * @author corbinrsmith@gmail.com (Cory Smith)
 */
public class ParameterParser {
  private final HttpServletRequest request;

  @Inject
  public ParameterParser(HttpServletRequest request) {
    this.request = request;
  }
  
  @SuppressWarnings("unchecked")
  public Map<String, String> getParameterMap(Map<String, Integer> parameters, Set<String> blacklist) {
    final Map<String, String> parsedParameterMap = Maps.newHashMap();
    final Map<String, String[]> requestParameterMap = request.getParameterMap();
  
    for (String key : Sets.intersection(requestParameterMap.keySet(), parameters.keySet())) {
      parsedParameterMap.put(key, safeArrayValue(0, requestParameterMap.get(key)));
    }
    final String path = request.getPathInfo();
    if (path == null) {
      return parsedParameterMap;
    }
  
    final String[] components = path.split("/");
    for (int i = 0; i < components.length; i++) {
      final String component = components[i].toLowerCase();
      if (parameters.containsKey(component)) {
        i += parameters.get(component); // increment that number of arguments
        parsedParameterMap.put(component, safeArrayValue(i, components));
      } else if (!blacklist.contains(component) && !component.isEmpty()) {
        throw new RuntimeException("Unknown argument: " + component + " in " + path);
      }
    }
    return parsedParameterMap;
  }

  String safeArrayValue(int index, String[] values) {
    return index < values.length ? values[index] : null;
  }
}