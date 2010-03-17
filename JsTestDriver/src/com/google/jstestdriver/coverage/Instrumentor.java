package com.google.jstestdriver.coverage;


/**
 * defines the expected interface for instrumenting code.
 * @author corysmith@google.com (Cory Smith)
 *
 */
public interface Instrumentor {

  public abstract InstrumentedCode instrument(Code code);

}
