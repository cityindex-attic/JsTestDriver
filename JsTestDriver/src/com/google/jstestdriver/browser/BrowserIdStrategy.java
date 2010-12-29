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

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.jstestdriver.Time;

import java.util.Set;

/**
 * A simple strategy that should avoid duplicated ids.
 * @author corbinrsmith@gmail.com (Cory Smith)
 *
 */
public class BrowserIdStrategy {

  private final Time time;
  private final Set<Long> used = Sets.newHashSet();

  @Inject
  public BrowserIdStrategy(Time time) {
    this.time = time;
  }

  public Long nextId() {
    long nextId = time.now().getMillis();
    while(used.contains(nextId)) {
      nextId++;
    }
    used.add(nextId);
    return nextId;
  }
}
