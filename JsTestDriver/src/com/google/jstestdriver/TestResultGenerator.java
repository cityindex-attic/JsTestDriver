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

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.jstestdriver.FailureParser.Failure;
import com.google.jstestdriver.Response.ResponseType;
import com.google.jstestdriver.TestResult.Result;
import com.google.jstestdriver.model.NullPathPrefix;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Generates the test results given a gson and a response.
 * 
 * @author shyamseshadri@gmail.com (Shyam Seshadri)
 */
public class TestResultGenerator {

  private static String NEW_LINE = System.getProperty("line.separator");

  private final Gson gson = new Gson();
  

  private final FailureParser failureParser;
  
  /**
   * 
   */
  @Deprecated
  public TestResultGenerator() {
    this(new FailureParser(new NullPathPrefix()));
  }

  @Inject
  public TestResultGenerator(FailureParser failureParser) {
    this.failureParser = failureParser;
  }

  /**
   * Loads the test results from the gson and response, sets the browser info on
   * each result and returns it.
   *
   * @param response The response object
   * @return a {@link Collection} of {@link TestResult} with accurate
   *         {@link BrowserInfo} set.
   */
  public Collection<TestResult> getTestResults(Response response) {
    try {
      // TODO(corysmith): Remove the check when all the ide plugins have been
      // updated.
      if (response.getResponseType() != ResponseType.TEST_RESULT) {
        return Collections.<TestResult> emptyList();
      }

      Collection<TestResult> results = gson.fromJson(response.getResponse(),
          new TypeToken<Collection<TestResult>>() {}.getType());

      for (TestResult result : results) {
        BrowserInfo browserInfo = response.getBrowser();

        result.setBrowserInfo(browserInfo);

        if (result.getResult() != Result.passed) {
          final List<Failure> failures = failureParser.parse(result.getMessage());
          result.setParsedMessage(
              Joiner.on(NEW_LINE).join(Lists.transform(failures, new Function<Failure, String>() {
                public String apply(Failure failure) {
                  return failure.getMessage();
                }
              })));
          StringBuilder sb = new StringBuilder();
          for (Failure failure : failures) {
            for (String l : failure.getStack()) {
              sb.append(l);
              sb.append(NEW_LINE);
            }
            sb.append(NEW_LINE);
          }
          result.setStack(sb.toString());
        }
      }
      return results;
    } catch (JsonParseException e) {
      throw new RuntimeException(e);
    }
  }
}
