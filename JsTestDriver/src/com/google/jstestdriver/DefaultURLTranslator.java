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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 *
 */
public class DefaultURLTranslator implements URLTranslator {

  private static final String FORWARD = "/forward";

  private final BiMap<String, String> cache = HashBiMap.create();

  public void translate(String url) throws MalformedURLException {
    URL netUrl = new URL(url);
    String file = netUrl.getFile();

    if (file.length() == 0) {
      file = "/";
    }
    cache.put(url, FORWARD + file);
  }

  public String getTranslation(String url) {
    return cache.get(url);
  }

  public String getOriginal(String translatedUrl) {
    return cache.inverse().get(translatedUrl);
  }

  public void clear() {
    cache.clear();
  }
}
