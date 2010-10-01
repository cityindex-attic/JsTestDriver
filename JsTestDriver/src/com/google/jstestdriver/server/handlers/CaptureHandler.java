// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.server.handlers;

import static com.google.jstestdriver.runner.RunnerType.CLIENT_CONTROLLED;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.jstestdriver.BrowserHunter;
import com.google.jstestdriver.SlaveBrowser;
import com.google.jstestdriver.UserAgentParser;
import com.google.jstestdriver.requesthandlers.RequestHandler;
import com.google.jstestdriver.runner.RunnerType;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * "Captures" a browser by redirecting it to RemoteConsoleRunner url, and adds
 * it to the CapturedBrowsers collection.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureHandler implements RequestHandler {

  private static final String ID = "id";

  private static final String RUNNER_TYPE = "runnertype";
  private static final String TIMEOUT = "timeout";

  public static final String STRICT = "strict";
  public static final String QUIRKS = "quirks";

  private static final Map<String, Integer> PARAMETERS = Maps.newHashMap();
  {
    PARAMETERS.put(STRICT, 0);
    PARAMETERS.put(QUIRKS, 0);
    PARAMETERS.put(RUNNER_TYPE, 1);
    PARAMETERS.put(ID, 1);
    PARAMETERS.put(TIMEOUT, 1);
  }

  private static final Set<String> BLACKLIST = Sets.newHashSet();
  {
    BLACKLIST.add("capture");
  }

  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final BrowserHunter browserHunter;

  @Inject
  public CaptureHandler(
      HttpServletRequest request,
      HttpServletResponse response,
      BrowserHunter browserHunter) {
    this.browserHunter = browserHunter;
    this.request = request;
    this.response = response;
  }

  public void handleIt() throws IOException {
    final Map<String, String> parameterMap = getParameterMap(request);
    String mode = parameterMap.get(QUIRKS) != null ? QUIRKS : STRICT;
    String id = parameterMap.get(ID);
    RunnerType runnerType = parseRunnerType(parameterMap.get(RUNNER_TYPE));
    Long timeout = parseLong(parameterMap.get(TIMEOUT));
    response.sendRedirect(service(request.getHeader("User-Agent"), mode, id, runnerType, timeout));
  }

  private Long parseLong(final String value) {
    return value == null ? null : Long.parseLong(value);
  }

  @SuppressWarnings("unchecked")
  private Map<String, String> getParameterMap(HttpServletRequest req) {
    final Map<String, String> parsedParameterMap = Maps.newHashMap();
    final Map<String, String[]> requestParameterMap = req.getParameterMap();

    for (String key : Sets.intersection(requestParameterMap.keySet(), PARAMETERS.keySet())) {
      parsedParameterMap.put(key, safeArrayValue(0, requestParameterMap.get(key)));
    }
    final String path = req.getPathInfo();
    if (path == null) {
      return parsedParameterMap;
    }

    final String[] components = path.split("/");
    for (int i = 0; i < components.length; i++) {
      final String component = components[i].toLowerCase();
      if (PARAMETERS.containsKey(component)) {
        i += PARAMETERS.get(component); // increment that number of arguments
        parsedParameterMap.put(component, safeArrayValue(i, components));
      } else if (!BLACKLIST.contains(component) && !component.isEmpty()) {
        throw new RuntimeException("Unknown argument: " + component);
      }
    }
    return parsedParameterMap;
  }

  private String safeArrayValue(int index, String[] values) {
    return index < values.length ? values[index] : null;
  }

  private RunnerType parseRunnerType(String runnerType) {
    return runnerType == null ? CLIENT_CONTROLLED : RunnerType.valueOf(runnerType.toUpperCase());
  }

  public String service(String userAgent, String mode, String id, RunnerType runnerType,
      Long timeout) {
    UserAgentParser parser = new UserAgentParser();

    parser.parse(userAgent);
    SlaveBrowser slaveBrowser =
        browserHunter.captureBrowser(id, parser.getName(), parser.getVersion(), parser.getOs(), timeout);
    return browserHunter.getCaptureUrl(slaveBrowser.getId(), mode, runnerType);
  }
}
