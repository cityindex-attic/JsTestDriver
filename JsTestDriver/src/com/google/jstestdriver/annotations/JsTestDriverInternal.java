package com.google.jstestdriver.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;

/**
 * The annotation used by JsTestDriver internal injections.
 * @author corysmith
 *
 */
@BindingAnnotation @Retention(RUNTIME)
public @interface JsTestDriverInternal {}
