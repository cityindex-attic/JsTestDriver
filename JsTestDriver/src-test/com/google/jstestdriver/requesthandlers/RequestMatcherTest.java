// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import static com.google.jstestdriver.requesthandlers.HttpMethod.GET;
import static com.google.jstestdriver.requesthandlers.HttpMethod.POST;
import junit.framework.TestCase;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class RequestMatcherTest extends TestCase {

  public void testPrefixMatcher() {
    RequestMatcher prefix = new RequestMatcher(GET, "*.mp3");

    assertTrue(prefix.uriMatches(".mp3"));
    assertTrue(prefix.uriMatches("/home.mp3"));
    
    assertFalse(prefix.uriMatches("/home.mp3/trailing"));
    assertFalse(prefix.uriMatches("/home"));
  }

  public void testSuffixMatcher() {
    RequestMatcher suffix = new RequestMatcher(POST, "/context/servlet/*");

    assertTrue(suffix.uriMatches("/context/servlet/"));
    assertTrue(suffix.uriMatches("/context/servlet/directories"));

    assertFalse(suffix.uriMatches("/context/servlet"));
    assertFalse(suffix.uriMatches("prefix/context/servlet/directories"));
    assertFalse(suffix.uriMatches("/context/infix/servlet/directories"));
  }

  public void testLiteralMatcher() {
    RequestMatcher literal = new RequestMatcher(POST, "/something.txt");

    assertTrue(literal.uriMatches("/something.txt"));

    assertFalse(literal.uriMatches("a/something.txt"));
    assertFalse(literal.uriMatches("/something.txt/a"));
  }

  public void testMethodMatcher() {
    RequestMatcher method = new RequestMatcher(POST, "asdf");

    assertTrue(method.methodMatches(POST));
    assertFalse(method.methodMatches(GET));
  }
}
