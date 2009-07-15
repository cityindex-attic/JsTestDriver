package com.google.jstestdriver;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.google.inject.BindingAnnotation;

/**
 * The annotation used by JsTestDriver internal injections.
 * @author corysmith
 *
 */
@BindingAnnotation @Retention(RUNTIME)
public @interface JsTestDriverInternal {}
