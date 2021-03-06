// Copyright 2011 Google Inc. All Rights Reserved.
package com.google.jstestdriver.action;

import com.google.gson.JsonArray;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.google.jstestdriver.Action;
import com.google.jstestdriver.Server;
import com.google.jstestdriver.hooks.ProxyConfigurationFilter;
import com.google.jstestdriver.model.HandlerPathPrefix;
import com.google.jstestdriver.model.RunData;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Configures the proxy on the server by sending a POST application/jsonrequest
 * to /jstd/proxy with the proxy mappings.
 * @author rdionne@google.com (Robert Dionne)
 */
public class ConfigureProxyAction implements Action {

  public interface Factory {
    ConfigureProxyAction create(JsonArray proxyConfig);
  }

  private final HandlerPathPrefix prefixer;
  private final String baseUrl;
  private final Server server;
  private final ProxyConfigurationFilter filter;
  private final JsonArray proxyConfig;

  @Inject
  public ConfigureProxyAction(
      @Named("serverHandlerPrefix") HandlerPathPrefix prefixer,
      @Named("server") String baseUrl,
      Server server,
      ProxyConfigurationFilter filter,
      @Assisted JsonArray proxyConfig) {
    this.prefixer = prefixer;
    this.baseUrl = baseUrl;
    this.server = server;
    this.filter = filter;
    this.proxyConfig = proxyConfig;
  }

  public RunData run(RunData runData) {
    try {
      URL initialUrl = new URL(baseUrl + "/proxy");
      URL adjustedUrl = new URL(
          initialUrl.getProtocol(),
          initialUrl.getHost(),
          initialUrl.getPort(),
          prefixer.prefixPath(initialUrl.getPath(), "jstd"));
      server.postJson(adjustedUrl.toString(), filter.filter(proxyConfig));
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return runData;
  }
}
