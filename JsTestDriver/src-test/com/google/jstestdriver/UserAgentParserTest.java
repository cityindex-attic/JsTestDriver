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

import junit.framework.TestCase;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public class UserAgentParserTest extends TestCase {

  private static final String CHROME_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) " +
  		"AppleWebKit/530.5 (KHTML, like Gecko) Chrome/2.0.172.31 Safari/530.5";
  private static final String FIREFOX_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; " +
  		"rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)";
  private static final String SAFARI_WINDOWS = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) " +
  		"AppleWebKit/525.28.3 (KHTML, like Gecko) Version/3.2.3 Safari/525.29";
  private static final String IE_WINDOWS = "Mozilla/4.0 " +
  		"(compatible; MSIE 7.0; Windows NT 5.1; InfoPath.2; .NET CLR 2.0.50727; " +
  		".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)";

  private static final String FIREFOX_LINUX = "Mozilla/5.0 " +
  		"(X11; U; Linux x86_64; en-US; rv:1.9.0.10) Gecko/2009042513 Ubuntu/8.04 (hardy) " +
  		"Firefox/3.0.10";

  private static final String FIREFOX_MACOS = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; " +
  		"en-US; rv:1.9.0.10) Gecko/2009042315 Firefox/3.0.10";

  private static final String SAFARI_MACOS = "Mozilla/5.0 " +
  		"(Macintosh; U; Intel Mac OS X 10_5_7; en-us) AppleWebKit/528.16 (KHTML, like Gecko) " +
  		"Version/4.0 Safari/528.16";

  public void testGetBrowserName() throws Exception {
    UserAgentParser parser = null;

    parser = new UserAgentParser();
    parser.parse(CHROME_WINDOWS);
    assertEquals("Chrome", parser.getName());

    parser = new UserAgentParser();
    parser.parse(FIREFOX_WINDOWS);
    assertEquals("Firefox", parser.getName());

    parser = new UserAgentParser();
    parser.parse(FIREFOX_LINUX);
    assertEquals("Firefox", parser.getName());

    parser = new UserAgentParser();
    parser.parse(FIREFOX_MACOS);
    assertEquals("Firefox", parser.getName());

    parser = new UserAgentParser();
    parser.parse(SAFARI_WINDOWS);
    assertEquals("Safari", parser.getName());

    parser = new UserAgentParser();
    parser.parse(SAFARI_MACOS);
    assertEquals("Safari", parser.getName());

    parser = new UserAgentParser();
    parser.parse(IE_WINDOWS);
    assertEquals("Microsoft Internet Explorer", parser.getName());

    parser = new UserAgentParser();
    parser.parse("Some weird unrecognized user-agent");
    assertEquals("Some weird unrecognized user-agent", parser.getName());
  }
}
