package com.google.jstestdriver.coverage;

import com.google.inject.ImplementedBy;


/**
 * Defines the expected interface for instrumenting code.
 * @author corysmith@google.com (Cory Smith)
 *
 */
@ImplementedBy(CodeInstrumentor.class)
public interface Instrumentor {
  public InstrumentedCode instrument(Code code);
}
