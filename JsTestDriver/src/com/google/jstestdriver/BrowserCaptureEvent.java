package com.google.jstestdriver;

/**
 * An event when a browser becomes captured or is no longer captured.
 * Observers of the {@link CapturedBrowsers} will recieve this event.
 *  
 * @author alexeagle@google.com (Alex Eagle)
*/
public class BrowserCaptureEvent {
  public enum Event { CONNECTED, DISCONNECTED }
  public final Event event;
  private final SlaveBrowser browser;

  BrowserCaptureEvent(Event event, SlaveBrowser browser) {
    this.event = event;
    this.browser = browser;
  }

  public SlaveBrowser getBrowser() {
    return browser;
  }
}
