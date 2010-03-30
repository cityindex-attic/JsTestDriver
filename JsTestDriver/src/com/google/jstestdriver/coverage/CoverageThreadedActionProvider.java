package com.google.jstestdriver.coverage;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.jstestdriver.ResetAction;
import com.google.jstestdriver.BrowserAction;
import com.google.jstestdriver.guice.DefaultThreadedActionProvider;
import com.google.jstestdriver.guice.ThreadedActionProvider;

/**
 * Temporary class that ensures the browser is reset 
 * @author corysmith
 *
 */
public class CoverageThreadedActionProvider implements ThreadedActionProvider {

  private final ResetAction reset;
  private final DefaultThreadedActionProvider provider;

  @Inject
  public CoverageThreadedActionProvider(ResetAction reset,
                                        DefaultThreadedActionProvider provider
                                        ) {
    this.reset = reset;
    this.provider = provider;
    
  }

  public List<BrowserAction> get() {
    List<BrowserAction> actions = Lists.newLinkedList();
    actions.add(reset); //start with a clean slate.
    actions.addAll(provider.get());
    actions.add(reset); //clean up after the coverage run
    return actions;
  }
}
