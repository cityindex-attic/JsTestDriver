package com.google.jstestdriver.browser;

import com.google.jstestdriver.BrowserActionRunner;
import com.google.jstestdriver.BrowserInfo;
import com.google.jstestdriver.JsTestDriverClient;
import com.google.jstestdriver.ResponseStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Manages a BrowserRunner lifecycle around a BrowserActionRunner.
 *
 * @author corbinrsmith@gmail.com (Corbin Smith)
 *
 */
public class BrowserManagedRunner implements Callable<ResponseStream> {
  private static final Logger logger =
      LoggerFactory.getLogger(BrowserManagedRunner.class);

  private final BrowserRunner runner;
  private final String browserId;
  private final BrowserActionRunner browserActionRunner;
  private final String serverAddress;
  private final JsTestDriverClient client;

  public BrowserManagedRunner(BrowserRunner runner, String browserId, String serverAddress,
      JsTestDriverClient client, BrowserActionRunner browserActionRunner) {
    this.runner = runner;
    this.browserId = browserId;
    this.serverAddress = serverAddress;
    this.client = client;
    this.browserActionRunner = browserActionRunner;
  }

  public ResponseStream call() throws Exception {
    String url = String.format("%s/capture?id=%s", serverAddress, browserId);
    runner.startBrowser(url);
    long timeOut = TimeUnit.MILLISECONDS.convert(runner.getTimeout(), TimeUnit.SECONDS);
    long start = System.currentTimeMillis();
    // TODO(corysmith): replace this with a stream from the client on browser
    // updates.
    while (!isBrowserCaptured(browserId, client)) {
      Thread.sleep(50);
      if (System.currentTimeMillis() - start > timeOut) {
        throw new RuntimeException("Could not start browser " + runner + " in "
            + runner.getTimeout());
      }
    }
    return browserActionRunner.call();
  }

  private boolean isBrowserCaptured(String browserId, JsTestDriverClient client) {
    for (BrowserInfo browserInfo : client.listBrowsers()) {
      if (browserId.equals(String.valueOf(browserInfo.getId()))) {
        logger.debug("Started {}", browserInfo);
        return true;
      }
    }
    return false;
  }
}
