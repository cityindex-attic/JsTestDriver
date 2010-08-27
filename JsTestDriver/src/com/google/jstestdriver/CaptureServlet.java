/*
 * Copyright 2008 Google Inc.
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

import static com.google.jstestdriver.runner.RunnerType.CLIENT_CONTROLLED;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.jstestdriver.runner.RunnerType;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * "Captures" a browser by redirecting it to RemoteConsoleRunner url, and adds
 * it to the CapturedBrowsers collection.
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class CaptureServlet extends HttpServlet {

  private static final String ID = "id";

  private static final String RUNNER_TYPE = "runnertype";
  private static final String TIMEOUT = "timeout";

  private static final long serialVersionUID = -6565114508964010323L;

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
  

  private final BrowserHunter browserHunter;

  public CaptureServlet(BrowserHunter browserHunter) {
    this.browserHunter = browserHunter;
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    final Map<String, String> parameterMap = getParameterMap(req);
    String mode = parameterMap.get(QUIRKS) != null ? STRICT : QUIRKS;
    String id = parameterMap.get(ID);
    RunnerType runnerType = parseRunnerType(parameterMap.get(RUNNER_TYPE));
    Long timeout = parseLong(parameterMap.get(TIMEOUT));
    resp.sendRedirect(service(req.getHeader("User-Agent"), mode, id, runnerType, timeout));
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
      } else if (!component.isEmpty()) {
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
