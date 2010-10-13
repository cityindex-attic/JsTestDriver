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
package com.google.jstestdriver.coverage;

import com.google.inject.Singleton;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Handles the mapping and unmapping of coverage names to ints. */
@Singleton
public class CoverageNameMapper {
  private final AtomicInteger id = new AtomicInteger(0);
  private final ConcurrentMap<Integer, String> idToNameMap =
      new ConcurrentHashMap<Integer, String>();
  private final ConcurrentMap<String, Integer> nameToIdMap =
    new ConcurrentHashMap<String, Integer>();

  public Integer map(String filePath) {
    if (nameToIdMap.containsKey(filePath)) {
      return nameToIdMap.get(filePath);
    }
    Integer pathId = id.getAndIncrement();
    idToNameMap.put(pathId, filePath);
    nameToIdMap.put(filePath, pathId);
    return pathId;
  }

  public String unmap(Integer fileId) {
    return idToNameMap.get(fileId);
  }
}
