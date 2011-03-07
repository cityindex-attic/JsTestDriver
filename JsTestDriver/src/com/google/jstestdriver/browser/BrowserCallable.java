package com.google.jstestdriver.browser;

import java.util.concurrent.Callable;


/**
 * Manages a BrowserRunner lifecycle around a BrowserActionRunner.
 *
 * @author corbinrsmith@gmail.com (Corbin Smith)
 *
 */
public class BrowserCallable<T> implements Callable<T> {
  
  private final Callable<T> callable;
  private final BrowserControl browserControl;
  private final String browserId;

  public BrowserCallable(Callable<T> callable,
      String browserId, BrowserControl browserControl) {
    this.callable = callable;
    this.browserId = browserId;
    this.browserControl = browserControl;
  }

  public T call() throws Exception {
    try {
      browserControl.captureBrowser(browserId);
      return callable.call();
    } finally {
      browserControl.stopBrowser();
    }
  }
}
