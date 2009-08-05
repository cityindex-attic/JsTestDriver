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
package com.google.jstestdriver.eclipse.ui.launch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 *
 */
public class TestCaseNameFinder {
  private static final Pattern TESTCASE_DECLARATION_PATTERN =
      Pattern.compile(".*jstestdriver\\.testCaseManager\\.TestCase\\(\'(.*?)\'\\);.*");
  private static final Pattern TESTMETHOD_DECLARATION_PATTERN =
      Pattern.compile(".*\\.prototype\\.(test.*?)[\\s=].*");

  public List<String> getTestCases(File jsFile) throws IOException {
    return getTestCases(new FileInputStream(jsFile));
  }
  
  public List<String> getTestCases(InputStream jsStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(jsStream));
    List<String> testCases = new ArrayList<String>();
    String input = null;
    String testCaseName = null;
    while ((input = reader.readLine()) != null) {
      Matcher matcher = TESTCASE_DECLARATION_PATTERN.matcher(input);
      if (matcher.matches()) {
        testCaseName = matcher.group(1);
      } else {
        matcher = TESTMETHOD_DECLARATION_PATTERN.matcher(input);
        if (matcher.matches()) {
          String testMethodName = matcher.group(1);
          testCases.add(testCaseName + "." + testMethodName);
        }
      }
    }
    return testCases;
  }
}
