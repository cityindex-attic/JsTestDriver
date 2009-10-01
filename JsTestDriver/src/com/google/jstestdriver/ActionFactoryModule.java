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

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.hooks.ActionListProcessor;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;
import com.google.jstestdriver.hooks.FileLoadPreProcessor;
import com.google.jstestdriver.hooks.TestsPreProcessor;
import com.google.jstestdriver.html.InlineHtmlProcessor;

/**
 * Module for the action handler.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ActionFactoryModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(),
        FileLoadPostProcessor.class).addBinding().to(InlineHtmlProcessor.class);
    Multibinder.newSetBinder(binder(), FileLoadPreProcessor.class);
    Multibinder.newSetBinder(binder(), ResponseStreamFactory.class);
    Multibinder.newSetBinder(binder(), ActionListProcessor.class);
    Multibinder.newSetBinder(binder(), TestsPreProcessor.class);
  }
}
