// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.hooks;

import com.google.gson.JsonArray;
import com.google.inject.ImplementedBy;
import com.google.jstestdriver.requesthandlers.DefaultProxyConfigurationFilter;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
@ImplementedBy(DefaultProxyConfigurationFilter.class)
public interface ProxyConfigurationFilter {

  JsonArray filter(JsonArray proxyConfig);
}
