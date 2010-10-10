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

import java.util.List;
import java.util.Set;

import com.google.jstestdriver.browser.BrowserRunner;
import com.google.jstestdriver.guice.GuiceBinding;
import com.google.jstestdriver.model.HandlerPathPrefix;

/**
 * @author jeremiele@google.com (Jeremie Lenfant-Engelmann)
 */
public interface Flags {

  @GuiceBinding(name="port")
  public Integer getPort();

  // server is not bound into Guice.
  public String getServer();

  @GuiceBinding(name="arguments", parameterizedType = String.class)
  public List<String> getArguments();

  @GuiceBinding(name="testOutput")
  public String getTestOutput();

  public Set<BrowserRunner> getBrowser();
  
  /** A set of strings for browsers that all actions should be run on. */
  public Set<String> getRequiredBrowsers();

  @GuiceBinding(name="reset")
  public boolean getReset();

  @GuiceBinding(name="config")
  public String getConfig();

  @GuiceBinding(name="tests", parameterizedType = String.class)
  public List<String> getTests();

  @GuiceBinding(name="verbose")
  public boolean getVerbose();

  @GuiceBinding(name="captureConsole")
  public boolean getCaptureConsole();

  @GuiceBinding(name="preloadFiles")
  public boolean getPreloadFiles();

  @GuiceBinding(name="dryRunFor", parameterizedType = String.class)
  public List<String> getDryRunFor();
  
  @GuiceBinding(name="browserTimeout")
  public long getBrowserTimeout();
  
  @GuiceBinding(name="serverHandlerPrefix")
  public HandlerPathPrefix getServerHandlerPrefix();
  
  public boolean getDisplayHelp();
}
