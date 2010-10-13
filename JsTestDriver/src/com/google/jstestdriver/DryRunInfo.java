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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.List;

/**
 * Dry run responses may be deserialized into this class.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class DryRunInfo {
  private int numTests;
  private List<String> testNames;
  private static Gson gson = new Gson();

  /**
   * @return the numTests
   */
  public int getNumTests() {
    return numTests;
  }

  /**
   * @return the testNames
   */
  public List<String> getTestNames() {
    return testNames;
  }

  public static DryRunInfo fromJson(Response response) {
    try {
      return gson.fromJson(response.getResponse(), DryRunInfo.class);
    } catch (JsonParseException e) {
      throw new RuntimeException("error deserializing " + response.getResponse(), e);
    }
  }
}
