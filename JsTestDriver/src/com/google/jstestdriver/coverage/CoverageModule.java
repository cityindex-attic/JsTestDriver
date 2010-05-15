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
package com.google.jstestdriver.coverage;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.jstestdriver.ResponseStreamFactory;
import com.google.jstestdriver.guice.BrowserActionProvider;
import com.google.jstestdriver.hooks.ActionListProcessor;
import com.google.jstestdriver.hooks.FileLoadPostProcessor;
import com.google.jstestdriver.hooks.FileLoadPreProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * Configure the code coverage plugin.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public class CoverageModule extends AbstractModule {

  private final List<String> excludes;

  public CoverageModule(List<String> excludes) {
    this.excludes = excludes;
  }

  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), FileLoadPostProcessor.class)
        .addBinding().to(CoverageInstrumentingProcessor.class);
    Multibinder.newSetBinder(binder(), FileLoadPreProcessor.class)
        .addBinding().to(CoverageJsAdder.class);
    Multibinder.newSetBinder(binder(), ResponseStreamFactory.class)
        .addBinding().to(CoverageResponseStreamFactory.class);
    Multibinder.newSetBinder(binder(), ActionListProcessor.class)
        .addBinding().to(CoverageActionDecorator.class);
    bind(new TypeLiteral<Set<String>>(){})
      .annotatedWith(new CoverageImpl("coverageExcludes")).toInstance(Sets.newHashSet(excludes));
    // TODO(corysmith): Remove this when there is a correct separation of phases.
    bind(BrowserActionProvider.class).to(CoverageThreadedActionProvider.class);
  }

  /** Lets me bind to annotations with arguments, namespaced to the Coverage plugin.*/
  public static class CoverageImpl implements Coverage {

    private final String value;

    @Override
    public int hashCode() {
      // This is specified in java.lang.Annotation.
      return (127 * "value".hashCode()) ^ value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (!(obj instanceof Coverage))
        return false;
      Coverage other = (Coverage) obj;
      if (value == null) {
        if (other.value() != null)
          return false;
      } else if (!value.equals(other.value()))
        return false;
      return true;
    }

    public CoverageImpl(String name) {
      this.value = name;
    }

    public String value() {
      return value;
    }

    public Class<? extends Annotation> annotationType() {
      return Coverage.class;
    }
  }

  // TODO(corysmith): figure out if there is a better way for plugins to configure themselves.
  // no point in requiring bad practice to integrate. (unlike some frameworks...)
  @Provides @Inject
  public CoverageWriter createCoverageWriter(@Named("testOutput") String testOut,
                                             @Named("config") String configFileName,
                                             @Named("outputStream") PrintStream out,
                                             CoverageNameMapper mapper) {
    if (testOut.length() > 0) {
      try {
        File testOutDir = new File(testOut);
        if (!testOutDir.exists()) {
          testOutDir.mkdirs();
        }
        // this should probably be configurable
        File coverageFile = new File(testOutDir, String.format("%s-coverage.dat", configFileName));
        if (coverageFile.exists()) {
          coverageFile.delete();
        }
        coverageFile.createNewFile();
        return new LcovWriter(new FileWriter(coverageFile), mapper);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new SummaryCoverageWriter(out, mapper);
  }
}
