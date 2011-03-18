package com.google.jstestdriver.coverage;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.jstestdriver.BrowserAction;
import com.google.jstestdriver.ResetAction;
import com.google.jstestdriver.guice.BrowserActionProvider;
import com.google.jstestdriver.guice.DefaultBrowserActionProvider;

import java.util.List;

/**
 * Temporary class that ensures the browser is reset 
 * @author corysmith
 *
 */
public class CoverageThreadedActionProvider implements BrowserActionProvider {

  private final ResetAction reset;
  private final DefaultBrowserActionProvider provider;

  @Inject
  public CoverageThreadedActionProvider(ResetAction reset,
                                        DefaultBrowserActionProvider provider
                                        ) {
    this.reset = reset;
    this.provider = provider;
    
  }

  public List<BrowserAction> get() {
    List<BrowserAction> actions = Lists.newLinkedList();
    actions.add(reset); //start with a clean slate.
    actions.addAll(provider.get());
    //actions.add(reset); //clean up after the coverage run
    return actions;
  }
}
