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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class UserAgentParser {

  private static final Pattern BROWSER_NAME_AND_VERSION =
      Pattern.compile("(Safari|Firefox|Opera|Konqueror)/([0-9\\.]+)");
  private static final Pattern CHROME_VERSION = Pattern.compile("Chrome/([0-9\\.]+)");
  private static final Pattern MSIE_VERSION = Pattern.compile("; MSIE ([0-9\\.]+);");

  private String userAgentName = "";
  private String userAgentVersion = "";
  private String userAgentOs = "";

  public void parse(String userAgent) {    
    String lowerCaseUserAgent = userAgent.toLowerCase();

    if (lowerCaseUserAgent.contains("windows")) {
      userAgentOs = "Windows";
    } else if (lowerCaseUserAgent.contains("linux")) {
      userAgentOs = "Linux";
    } else if (lowerCaseUserAgent.contains("iphone")) {
      userAgentOs = "iPhone OS";
    } else if (lowerCaseUserAgent.contains("mac")) {
      userAgentOs = "Mac OS";
    }
    if (userAgent.contains("Chrome")) {
      userAgentName = "Chrome";
      Matcher matcher = CHROME_VERSION.matcher(userAgent);

      if (matcher.find()) {
        userAgentVersion = matcher.group(1);
      }
    } else if (userAgent.contains("MSIE")) {
      userAgentName = "Microsoft Internet Explorer";
      Matcher matcher = MSIE_VERSION.matcher(userAgent);

      if (matcher.find()) {
        userAgentVersion = matcher.group(1);
      }
    } else {
      Matcher matcher = BROWSER_NAME_AND_VERSION.matcher(userAgent);

      if (matcher.find()) {
        userAgentName = matcher.group(1);
        userAgentVersion = matcher.group(2);
      } else {
        userAgentName = userAgent;
      }
    }
  }

  public String getName() {
    return userAgentName;
  }

  public String getVersion() {
    return userAgentVersion;
  }

  public String getOs() {
    return userAgentOs;
  }
}
