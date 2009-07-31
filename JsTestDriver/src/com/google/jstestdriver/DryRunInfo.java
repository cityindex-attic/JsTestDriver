// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.gson.Gson;

import java.util.List;

/**
 * Dry run responses may be deserialized into this class.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class DryRunInfo {
  private int numTests;
  private List<String> testNames;

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
    return new Gson().fromJson(response.getResponse(), DryRunInfo.class);        
  }
}
