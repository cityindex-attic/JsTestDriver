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


package com.google.jstestdriver.browser;

import com.google.jstestdriver.MockTime;

import junit.framework.TestCase;

/**
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class BrowserIdStrategyTest extends TestCase {
  Long time = 1L;
  
  public void testGetId() throws Exception {
    BrowserIdStrategy strategy = createStrategy();
    
    Long nextId = strategy.nextId();
    assertNotNull(nextId);
    assertTrue(nextId != 0);
  }
  
  public void testUniqueNess() throws Exception {
    BrowserIdStrategy strategy = createStrategy();
    assertTrue(strategy.nextId() != strategy.nextId());
  }

  public void testAssertUniqueNessAccrossInstances() throws Exception {
    assertTrue(createStrategy().nextId() != createStrategy().nextId());
  }

  private BrowserIdStrategy createStrategy() {
    return new BrowserIdStrategy(new MockTime(time++));
  }
}
