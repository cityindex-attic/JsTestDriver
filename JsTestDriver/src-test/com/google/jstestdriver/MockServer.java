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
package com.google.jstestdriver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 * @author misko@google.com
 */
public class MockServer implements Server {

  private Map<String, String> expectations = new HashMap<String, String>();

  public void expect(String url, String response) {
    expectations.put(url, response);
  }

  public String fetch(String url) {
    return get(url);
  }

  public String post(String url, Map<String, String> params) {
    return get(url + "?POST?" + params);
  }

  private String get(String request) {
    String response = expectations.get(request);
    if (response == null) {
      throw new IllegalArgumentException("Unexpected request: " + request);
    }
    return response;
  }

  public String startSession(String baseUrl, String id) {
    return "ID";
  }

  public void stopSession(String baseUrl, String id, String sessionId) {
    // noop
  }
}
