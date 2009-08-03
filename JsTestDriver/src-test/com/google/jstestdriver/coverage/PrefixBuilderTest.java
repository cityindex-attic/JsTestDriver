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

package com.google.jstestdriver.coverage;

import junit.framework.TestCase;

public class PrefixBuilderTest extends TestCase {

  public void testBuildPrefix() throws Exception {
    PrefixBuilder prefixBuilder = new PrefixBuilder("foo();", "HASH", 10, 20);
    assertEquals("\nLCOV_HASH[10]++; foo();", prefixBuilder.build());
  }
  
  public void testBuildPrefixPrependAppend() throws Exception {
    PrefixBuilder prefixBuilder = new PrefixBuilder("foo();", "HASH", 10, 20);
    prefixBuilder.prepend("{").append("}");
    assertEquals("\n{LCOV_HASH[10]++; foo();}", prefixBuilder.build());
  }
  
  public void testBuildPrefixPaddingForDigitLength() throws Exception {
    PrefixBuilder prefixBuilder = new PrefixBuilder("foo();", "HASH", 10, 200);
    assertEquals("\nLCOV_HASH[10 ]++; foo();", prefixBuilder.build());
  }

  public void testBuildIndent() throws Exception {
    PrefixBuilder prefixBuilder = new PrefixBuilder(null, "HASH", 10, 200);
    assertEquals("                  ", prefixBuilder.buildIndent());
  }
}
