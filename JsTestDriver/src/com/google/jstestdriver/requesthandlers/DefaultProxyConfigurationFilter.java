// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.jstestdriver.hooks.ProxyConfigurationFilter;
import com.google.jstestdriver.hooks.ProxyDestination;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class DefaultProxyConfigurationFilter implements ProxyConfigurationFilter {
  
  @Inject(optional=true) private ProxyDestination destination;

  public JsonArray filter(JsonArray proxyConfig) {
    if (destination == null) {
      return proxyConfig;
    } else {
      JsonObject entry = new JsonObject();
      entry.addProperty("matcher", "*");
      entry.addProperty("server", destination.getDestinationAddress());
      proxyConfig = copy(proxyConfig);
      proxyConfig.add(entry);
      return proxyConfig;
    }
  }

  private JsonArray copy(JsonArray original) {
    JsonArray copy = new JsonArray();
    for (JsonElement element : original) {
      copy.add(element);
    }
    return copy;
  }
}
