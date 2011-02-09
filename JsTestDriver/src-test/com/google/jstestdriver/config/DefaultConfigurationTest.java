// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.config;

import java.io.File;

import com.google.jstestdriver.model.ConcretePathPrefix;
import com.google.jstestdriver.model.NullPathPrefix;

import junit.framework.TestCase;

/**
 * @author corysmith@google.com (Your Name Here)
 *
 */
public class DefaultConfigurationTest extends TestCase {
  public void testGetServerFromFlag() throws Exception {
    String configServer = "configServer";
    String flagServer = "flagServer";
    final DefaultConfiguration config = new DefaultConfiguration(new File("."));
    assertEquals(flagServer, config.getServer(flagServer, -1, new NullPathPrefix()));
    final ConcretePathPrefix prefix = new ConcretePathPrefix("jstd");
    assertEquals(prefix.suffixServer(flagServer), config.getServer(flagServer, -1, prefix));
  }
}
