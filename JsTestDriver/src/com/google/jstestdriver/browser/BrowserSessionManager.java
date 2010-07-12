// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.jstestdriver.browser;

import com.google.inject.ImplementedBy;



/**
 * Manages the client portion of session for a given captured browser.
 * @author corysmith@google.com (Cory Smith)
 
 */
@ImplementedBy(BaseBrowserSessionManager.class)
public interface BrowserSessionManager {

  /**
   * Starts the session with the Server.
   * @param browserId The browser to start the session with.
   * @return A string to uniquely identify this session.
   */
  public String startSession(String browserId);

  /**
   * Ends a session with a browser.
   * @param sessionId The unique id for the session.
   * @param browserId The id of the browser to end the session for.
   */
  public void stopSession(String sessionId, String browserId);

}
