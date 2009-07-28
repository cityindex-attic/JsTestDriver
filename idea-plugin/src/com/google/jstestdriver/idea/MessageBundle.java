// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.jstestdriver.idea;

import com.intellij.CommonBundle;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 * Provides localized messages via the appropriate Idea message bundle loader.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MessageBundle {
  private static Reference<ResourceBundle> bundle;

  @NonNls
  private static final String BUNDLE = "com.google.jstestdriver.idea.MessageBundle";

  private MessageBundle() {
  }

  public static String message(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key,
                               Object... params) {
    return CommonBundle.message(MessageBundle.getBundle(), key, params);
  }

  private static ResourceBundle getBundle() {
    ResourceBundle bundle = null;
    if (MessageBundle.bundle != null) {
      bundle = MessageBundle.bundle.get();
    }
    if (bundle == null) {
      bundle = ResourceBundle.getBundle(MessageBundle.BUNDLE);
      MessageBundle.bundle = new SoftReference<ResourceBundle>(bundle);
    }
    return bundle;
  }
}
