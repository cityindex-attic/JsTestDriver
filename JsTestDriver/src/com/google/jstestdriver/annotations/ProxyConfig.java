// Copyright 2010 Google Inc. All Rights Reserved.
package com.google.jstestdriver.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.google.inject.BindingAnnotation;

/**
* @author rdionne@google.com (Robert Dionne)
*/
@BindingAnnotation
@Retention(RUNTIME) public @interface ProxyConfig {}