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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class URLQueryParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(URLQueryParser.class);

  private final String query;
  private final Map<String, String> parameters = new HashMap<String, String>();

  private boolean parsed = false;

  public URLQueryParser(String query) {
    this.query = query;
  }

  public void parse() {
    if (!parsed) {
      if (query == null) {
        parsed = true;
        return;
      }
      String queryString = query;

      int indexOf = query.indexOf("?");

      if (indexOf != -1) {
        queryString = query.substring(indexOf + 1);
      }
      String[] pairs = queryString.split("&");

      for (String pair : pairs) {
        String[] keyValue = pair.split("=", 2);
        String key = "";
        String value = "";

        try {
          key = URLDecoder.decode(keyValue[0], "UTF-8");
          value = keyValue.length == 2 ? URLDecoder.decode(keyValue[1], "UTF-8") : "";

          parameters.put(key, value);
        } catch (UnsupportedEncodingException e) {
          LOGGER.warn("Could not decode: [ " + key + ", " + value + " ]", e);
        }
      }
      parsed = true;
    }
  }

  public String getParameter(String key) {
    return parameters.get(key);
  }
}
