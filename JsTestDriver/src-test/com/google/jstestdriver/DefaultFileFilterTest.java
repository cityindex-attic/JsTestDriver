/*
 * Copyright 2011 Google Inc.
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

import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;

/**
 * @author Cory Smith (corbinrsmith@gmail.com) 
 */
public class DefaultFileFilterTest extends TestCase {
  public void testResolveDepenencies() throws Exception {
    DefaultFileFilter filter = new DefaultFileFilter();

    FileInfo changed = new FileInfo("foo2.js", -1, -1, false, false, "");
    FileInfo reloaded = new FileInfo("foo3.js", -1, -1, false, false, "");
    List<FileInfo> files = Lists.newArrayList(
      new FileInfo("foo1.js", -1, -1, false, false, ""),
      changed,
      reloaded
    );
    assertEquals(Lists.newArrayList(changed, reloaded),
        filter.resolveFilesDeps(changed, files));
  }
}
