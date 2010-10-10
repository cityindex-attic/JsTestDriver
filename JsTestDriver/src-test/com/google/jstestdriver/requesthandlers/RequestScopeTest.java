// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.requesthandlers;

import junit.framework.TestCase;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.util.Providers;

/**
 * @author rdionne@google.com (Robert Dionne)
 */
public class RequestScopeTest extends TestCase {

  public void testExitBeforeEnter() {
    RequestScope scope = new RequestScope();

    try {
      scope.exit();
      fail("scope.exit() should throw exception if scope.enter() never called.");
    } catch (RuntimeException expected) {}
  }

  public void testEnterAfterEnter() {
    RequestScope scope = new RequestScope();

    scope.enter();
    try {
      scope.enter();
      fail("scope.enter() should throw exception if scope.enter() already called.");
    } catch (RuntimeException expected) {}
  }

  public void testSeedBeforeEnter() {
    RequestScope scope = new RequestScope();

    try {
      scope.seed(new Key<String>() {}, "Happy Birthday!");
      fail("scope.seed() should complain if scope.enter() never called.");
    } catch (OutOfScopeException expected) {}

    try {
      scope.seed(String.class, "Happy Birthday!");
      fail("scope.seed() should complain if scope.enter() never called.");
    } catch (OutOfScopeException expected) {}
  }

  public void testScopeBeforeEnter() {
    RequestScope scope = new RequestScope();

    Provider<String> stringProvider =
        scope.scope(new Key<String>() {}, Providers.of("Happy Birthday!"));
    try {
      stringProvider.get();
      fail("scope.scope() should complain if scope.enter() never called.");
    } catch (OutOfScopeException expected) {}
  }

  public void testScopeAfterExplicitSeed() {
    RequestScope scope = new RequestScope();

    scope.enter();

    scope.seed(String.class, "Happy Birthday!");
    assertEquals("Happy Birthday!", scope.scope(new Key<String>() {}, null).get());

    scope.exit();
  }

  public void testScopeWithoutSeedButWithExternalProvider() {
    RequestScope scope = new RequestScope();

    scope.enter();

    assertEquals("Happy Birthday!", scope.scope(new Key<String>() {}, Providers.of("Happy Birthday!")).get());

    scope.exit();
  }

  public void testScopeWithoutSeedAndWithoutExternalProvider() {
    RequestScope scope = new RequestScope();

    scope.enter();

    assertNull(scope.scope(new Key<String>() {}, Providers.<String>of(null)).get());

    scope.exit();
  }
}
