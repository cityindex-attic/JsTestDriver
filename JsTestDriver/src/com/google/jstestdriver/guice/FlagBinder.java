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

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;
import com.google.inject.util.Types;
import com.google.jstestdriver.Flags;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Reads and binds the Flags data to a Guice scope.
 * @author corysmith
 *
 */
public class FlagBinder {

  private final Flags flags;

  public FlagBinder(Flags flags) {
    this.flags = flags;
  }

  @SuppressWarnings("unchecked")
  public void bind(Binder binder) {
    // TODO(corysmith): figure out how to read annotations off the interface from an instance class.
    for (Method method : Flags.class.getMethods()) {
      GuiceBinding annotation = method.getAnnotation(GuiceBinding.class);
      if (annotation == null) {
        continue;
      }
      Object value;
      try {
        value = method.invoke(flags);
        binder.bind(resolveClass(annotation,
            method.getReturnType())).toProvider(Providers.of(value));
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }


  @SuppressWarnings("unchecked")
  private Key resolveClass(GuiceBinding annotation, Class returnType) {
    Named named = Names.named(annotation.name());
    if (List.class.isAssignableFrom(returnType)) {
      if (annotation.parameterizedType() == GuiceBinding.NullClass.class) {
        throw new IllegalArgumentException("Missing parameterized type for " + annotation);
      }
      return Key.get(Types.listOf(annotation.parameterizedType()), named);
    }
    return Key.get(returnType, named);
  }
}
