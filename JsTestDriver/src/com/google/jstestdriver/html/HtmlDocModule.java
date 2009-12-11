// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.html;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;

/**
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class HtmlDocModule extends AbstractModule{
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(),
        FileLoadPostProcessor.class).addBinding().to(InlineHtmlProcessor.class);
  }
}
