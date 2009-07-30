// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.ActionFactory.ActionFactoryFileFilter;
import com.google.jstestdriver.hooks.FileReaderHook;

/**
 * Module for API usage, which doesn't require any constructor params.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ActionFactoryModule extends AbstractModule {

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), FileReaderHook.class);
    bind(FileReader.class).to(HookedFileReader.class);
    bind(JsTestDriverFileFilter.class).to(ActionFactoryFileFilter.class);
  }
}
