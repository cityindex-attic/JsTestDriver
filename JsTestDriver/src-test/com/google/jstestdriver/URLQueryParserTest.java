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

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 *
 */
public class URLQueryParserTest extends TestCase {

  public void testParseHappyQueryString() throws Exception {
    URLQueryParser parser = new URLQueryParser("key1=value1&key2=value2");

    parser.parse();
    assertEquals("value1", parser.getParameter("key1"));
    assertEquals("value2", parser.getParameter("key2"));
  }

  public void testKeyButNoValue() throws Exception {
    URLQueryParser parser = new URLQueryParser("key1=value1&key2");

    parser.parse();
    assertEquals("value1", parser.getParameter("key1"));
    assertEquals("", parser.getParameter("key2"));    
  }

  public void testEncodedValue() throws Exception {
    URLQueryParser parser = new URLQueryParser("key1=value1&key2&key3=value%3D3");

    parser.parse();
    assertEquals("value1", parser.getParameter("key1"));
    assertEquals("", parser.getParameter("key2"));
    assertEquals("value=3", parser.getParameter("key3"));
  }

  public void testQueryStringIsNull() throws Exception {
    URLQueryParser parser = new URLQueryParser(null);

    try {
      parser.parse();
    } catch (NullPointerException e) {
      fail("Exception was thrown when nothing should have happen");
    }
  }

  public void testQuestionMark() throws Exception {
    URLQueryParser parser = new URLQueryParser("/somestuff/?key1=value1&key2=value2");

    parser.parse();
    assertEquals("value1", parser.getParameter("key1"));
    assertEquals("value2", parser.getParameter("key2"));    
  }
}
