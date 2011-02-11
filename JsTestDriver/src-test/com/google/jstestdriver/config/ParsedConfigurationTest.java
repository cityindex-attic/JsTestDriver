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

package com.google.jstestdriver.config;

import com.google.jstestdriver.model.ConcretePathPrefix;
import com.google.jstestdriver.model.NullPathPrefix;

import junit.framework.TestCase;

/**
 * @author corbinrsmith@gmail.com
 *
 */
public class ParsedConfigurationTest extends TestCase {
  public void testGetServerFromFlag() throws Exception {
    String configServer = "configServer";
    String flagServer = "flagServer";
    final ParsedConfiguration config =
        new ParsedConfiguration(null, null, null, configServer, 0, null, null, null);
    assertEquals(flagServer, config.getServer(flagServer, -1, new NullPathPrefix()));
    assertEquals(configServer, config.getServer("", -1, new NullPathPrefix()));
    final ConcretePathPrefix prefix = new ConcretePathPrefix("jstd");
    assertEquals(prefix.suffixServer(flagServer), config.getServer(flagServer, -1, prefix));
    assertEquals(prefix.suffixServer(configServer), config.getServer("", -1, prefix));
  }
}
