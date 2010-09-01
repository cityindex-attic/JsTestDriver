// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

import java.util.Map;

/**
 * The {@link Guice} {@link Scope} associated with HTTP requests. Maintains a
 * {@link Map} of {@link Key}s to {@link Object}s that exist within the scope
 * of the HTTP request.
 *
 * @author rdionne@google.com (Robert Dionne)
 */
class RequestScope implements Scope {

  private final ThreadLocal<Map<Key<?>, Object>> values =
      new ThreadLocal<Map<Key<?>, Object>>();

  /**
   * Enter the {@link RequestScope}.
   */
  public void enter() {
    Preconditions.checkState(values.get() == null, "Scope already entered.");
    values.set(Maps.<Key<?>, Object>newHashMap());
  }

  /**
   * Exit the {@link RequestScope}.
   */
  public void exit() {
    Preconditions.checkState(values.get() != null, "Scope not entered.");
    values.remove();
  }

  /**
   * Seed the {@link RequestScope} with an object of type T.
   *
   * @param key a {@link Key} that represents T
   * @param value the object of type T
   * @param <T> the type of {@code value}
   */
  public <T> void seed(Key<T> key, T value) {
    Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

    // TODO(rdionne): replace error message
    Preconditions.checkState(!scopedObjects.containsKey(key), "Error");
    scopedObjects.put(key, value);
  }

  /**
   * Seed the {@link RequestScope} with an object of type T
   *
   * @param clazz {@code value}'s class
   * @param value the object of type T
   * @param <T> the type of {@code value}
   */
  public <T> void seed(Class<T> clazz, T value) {
    seed(Key.get(clazz), value);
  }
  
  public <T> Provider<T> scope(final Key<T> tKey, final Provider<T> tProvider) {
    return new Provider<T>() {
      public T get() {
        Map<Key<?>, Object> scopedObjects = getScopedObjectMap(tKey);
        
        @SuppressWarnings("unchecked")
        T current = (T) scopedObjects.get(tKey);
        if (current == null && !scopedObjects.containsKey(tKey)) {
          current = tProvider.get();
          scopedObjects.put(tKey, current);
        }
        return current;
      }
    };
  }
  
  private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
    Map<Key<?>, Object> scopedObjects = values.get();
    if (scopedObjects == null) {
      throw new OutOfScopeException(new StringBuilder("Cannot access ")
          .append(key)
          .append(" outside of a scoping block.")
          .toString());
    }
    return scopedObjects;
  }
}
