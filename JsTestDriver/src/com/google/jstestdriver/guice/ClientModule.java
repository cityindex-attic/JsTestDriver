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

package com.google.jstestdriver.guice;

import java.io.PrintStream;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Module that defines the configuration for a JsTestDriverClient.
 * @author corysmith
 *
 */
public class ClientModule extends AbstractModule {

  private final PrintStream out;

  public ClientModule(PrintStream out) {
    this.out = out;
  }

  @Override
  protected void configure() {
    bind(PrintStream.class).annotatedWith(Names.named("outputStream")).toInstance(out);
  }
}
