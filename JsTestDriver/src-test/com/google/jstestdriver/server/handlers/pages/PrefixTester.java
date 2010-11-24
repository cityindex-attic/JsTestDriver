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
package com.google.jstestdriver.server.handlers.pages;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.jstestdriver.model.ConcretePathPrefix;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.runner.RunnerType;
import com.google.jstestdriver.server.handlers.CaptureHandler;
import com.google.jstestdriver.util.HtmlWriter;


/**
 * Tests that all paths in a given page are correctly prefixed.
 *
 * @author Cory Smith (corbinrsmith@gmail.com)
 */
public class PrefixTester {
  /**
   * @param page
   * @throws IOException
   */
  public void testPrefixes(Page page) throws IOException {
    Map<String, String> parameters = ImmutableMap.<String, String>builder()
    .put(SlavePageRequest.ID, "1")
    .put(CaptureHandler.RUNNER_TYPE, RunnerType.CLIENT.toString())
    .build();
    String jstd = "jstd";
    HandlerPathPrefix prefix = new ConcretePathPrefix(jstd);
    SlavePageRequest request = new SlavePageRequest(parameters, null, prefix, null);
    CharArrayWriter writer = new CharArrayWriter();
    final HtmlWriter htmlWriter =
      new HtmlWriter(writer, prefix);
    page.render(htmlWriter, request);
    Pattern pathFinder = Pattern.compile("(href|src)=\"([^\"]*)\"");
    Matcher matcher = pathFinder.matcher(writer.toString());
    while (matcher.find()) {
      MatchResult result = matcher.toMatchResult();
      String[] components = result.group(2).trim().split("/");
      ConsolePageTest.assertEquals("Missing initial /","", components[0]);
      ConsolePageTest.assertEquals(jstd, components[1]);
      ConsolePageTest.assertFalse("Second component should not be the prefix.",
        jstd.equals(components[2]));
    }
  }
}
