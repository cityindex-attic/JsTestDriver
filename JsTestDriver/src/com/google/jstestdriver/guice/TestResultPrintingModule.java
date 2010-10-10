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

import static com.google.inject.multibindings.Multibinder.newSetBinder;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.DefaultResponseStreamFactory;
import com.google.jstestdriver.Flags;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.config.Configuration;
import com.google.jstestdriver.hooks.PluginInitializer;
import com.google.jstestdriver.output.DefaultListener;
import com.google.jstestdriver.output.MultiTestResultListener;
import com.google.jstestdriver.output.TestResultHolder;
import com.google.jstestdriver.output.TestResultListener;


/**
 * Configuration for outputting test results. If a testOutput flag was
 * provided, then XML result files will be written to that directory.
 * A text report is always written to the provided PrintStream.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestResultPrintingModule extends AbstractModule {

  public static class TestResultPrintingInitializer implements PluginInitializer {
    public Module initializeModule(Flags flags, Configuration config) {
      return new TestResultPrintingModule();
    }
  }

  @Deprecated
  public TestResultPrintingModule(String testOutput) {
  }

  public TestResultPrintingModule() {
  }

  @Override
  protected void configure() {
    Multibinder<TestResultListener> testResultListeners =
        newSetBinder(binder(), TestResultListener.class);

    testResultListeners.addBinding().to(TestResultHolder.class);
    testResultListeners.addBinding().to(DefaultListener.class).in(Singleton.class);

    bind(TestResultListener.class).to(MultiTestResultListener.class);
    newSetBinder(binder(),
        ResponseStreamFactory.class).addBinding().to(DefaultResponseStreamFactory.class);
  }
}
