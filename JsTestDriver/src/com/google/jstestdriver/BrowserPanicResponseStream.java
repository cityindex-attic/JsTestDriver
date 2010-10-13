package com.google.jstestdriver;

import com.google.gson.Gson;
import com.google.jstestdriver.Response.ResponseType;
import com.google.jstestdriver.browser.BrowserPanicException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A response stream that throws an exception if the browser panics.
 * @author corysmith@google.com (Cory Smith)
 */
class BrowserPanicResponseStream implements ResponseStream {

  private static final Logger logger = LoggerFactory.getLogger(BrowserPanicResponseStream.class);

  private final Gson gson = new Gson();

  public void stream(Response response) {
    if (response.getResponseType() == ResponseType.BROWSER_PANIC) {
      BrowserPanic panic = gson.fromJson(response.getResponse(),
                                         response.getResponseType().type);
      BrowserPanicException exception =
          new BrowserPanicException(panic.getBrowserInfo(),
                                    "");
      logger.error("Browser not found : {}\n Exception: {}",
                   new Object[]{response.getResponse(),
                                exception});
      throw exception;
    }
  }

  public void finish() {
    // NOOP
  }
}