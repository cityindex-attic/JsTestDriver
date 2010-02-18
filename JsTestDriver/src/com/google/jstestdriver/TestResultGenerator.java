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
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.List;

/**
 * Generates the test results given a gson and a response.
 * 
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class TestResultGenerator {

  private static String NEW_LINE = System.getProperty("line.separator");

  private final Gson gson = new Gson();
  
  /**
   * Loads the test results from the gson and response, sets the browser info on each result and
   * returns it.
   * 
   * @param response The response object
   * @return a {@link Collection} of {@link TestResult} with accurate {@link BrowserInfo} set.
   */
  public Collection<TestResult> getTestResults(Response response) {
    Collection<TestResult> results = gson.fromJson(response.getResponse(),
        new TypeToken<Collection<TestResult>>() {}.getType());

    for (TestResult result : results) {
      BrowserInfo browserInfo = response.getBrowser();

      result.setBrowserInfo(browserInfo);
      FailureParser failureParser = new FailureParser();

      failureParser.parse(result.getMessage());
      result.setParsedMessage(failureParser.getMessage());
      List<String> stackTrace = failureParser.getStack();
      StringBuilder sb = new StringBuilder();

      for (String l : stackTrace) {
        sb.append(l);
        sb.append(NEW_LINE);
      }
      result.setStack(sb.toString());
    }
    return results;
  }
}
